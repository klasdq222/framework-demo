package com.klasdq.demo.common.utils;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * pom.xml
 *  <!-- orika object copy-->
 *  <dependency>
 *      <groupId>ma.glasnost.orika</groupId>
 *      <artifactId>orika-core</artifactId>
 *      <version>1.5.4</version>
 *  </dependency>
 *
 *  <!-- https://mvnrepository.com/artifact/com.esotericsoftware.reflectasm/reflectasm -->
 *  <dependency>
 *      <groupId>com.esotericsoftware.reflectasm</groupId>
 *      <artifactId>reflectasm</artifactId>
 *      <version>1.07</version>
 *  </dependency>
 *
 * JavaBean拷贝
 */
public class BeanMapper {

    /**
     * Orika
     *      缺点：只能进行一对一映射
     *      优点：效率较高，可以使用map进行不同字段名映射
     */
    /****************************Orika 对象拷贝方式********************************/
    private static final MapperFactory MAPPER_FACTORY;
    private static final MapperFacade MAPPER_FACADE;
    static {
        MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();
        MAPPER_FACADE = MAPPER_FACTORY.getMapperFacade();
    }
    /**
     * 对象复制
     * @param src              源对象
     * @param targetClass     目标对象类 对象
     * @param <S>               源对象类型
     * @param <T>               目标对象类型
     * @return
     */
    public static <S,T> T mapper(S src,Class<T> targetClass){
        return mapper(src,targetClass,()->MAPPER_FACADE);
    }

    /**
     * 对象复制
     * @param src               源对象
     * @param targetClass       目标对象类 对象
     * @param supplier          Supplier接口 自定义传入MapperFacade
     * @param <S>               源对象类型
     * @param <T>               目标对象类型
     * @return
     */
    public static <S,T> T mapper(S src, Class<T> targetClass, Supplier<MapperFacade> supplier){
        return supplier.get().map(src,targetClass);
    }

    /**
     * 预先获取orika转换所需要的Type，避免每次转换.
     *  TypeFactory相当于一个缓存 valueOf()执行过程为如果有缓存类型就直接返回否则进行反射
     * 对于基本类型而言 参数传递为值传递
     * 对于其他类型 参数传递为地址传递或者说引用传递 如果随意修改 参数的值将会引起某些问题
     * 当参数值为final时即在方法内修改参数的值会出现异常（仅对引用类型有效）
     */
    public static <E> Type<E> getType(final Class<E> rawType) {
        return TypeFactory.valueOf(rawType);
    }

    public static MapperFactory getFactory() {
        return MAPPER_FACTORY;
    }


    /**
     *  对象复制
     * @param src           源对象
     * @param srcType       源类型
     * @param targetType    目标类型
     * @param <S>           源对象类型
     * @param <T>           目标对象类型
     * @return  复制后的对象
     */
    public static <S,T> T mapper(S src, Type<S> srcType,Type<T> targetType){
        return MAPPER_FACADE.map(src,srcType,targetType);
    }

    /**
     * 简单的复制出新对象列表到ArrayList
     * <p>
     * 不建议使用mapper.mapAsList(Iterable<S>,Class<D>)接口, srcClass需要反射，有点慢
     */
    public static <S, T> List<T> mapList(Iterable<S> srcList, Class<S> srcClass, Class<T> targetClass) {
        return MAPPER_FACADE.mapAsList(srcList, TypeFactory.valueOf(srcClass), TypeFactory.valueOf(targetClass));
    }

    /**
     * 极致性能的复制出新类型对象到ArrayList.
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S,T> List<T> mapList(Iterable<S> srcList, Type<S> sourceType, Type<T> destinationType) {
        return MAPPER_FACADE.mapAsList(srcList, sourceType, destinationType);
    }

    /**
     * 简单复制出新对象列表到数组
     * <p>
     * 通过source.getComponentType() 获得源Class
     */
    public static <S, T> T[] mapArray(final T[] destination, final S[] source, final Class<T> destinationClass) {
        return MAPPER_FACADE.mapAsArray(destination, source, destinationClass);
    }

    /**
     * 极致性能的复制出新类型对象到数组
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, T> T[] mapArray(T[] destination, S[] source, Type<S> sourceType, Type<T> destinationType) {
        return MAPPER_FACADE.mapAsArray(destination, source, sourceType, destinationType);
    }

    /**
     * 构建具有属性映射的MapperFacade
     * @param srcClass      源对象类
     * @param targetClass   目标对象类
     * @param propertyMap   源对象与目标对象属性映射 Key源对象属性名 Value目标对象属性名
     * @param <S>            源对象类型
     * @param <T>            目标对象类型
     * @return
     */
    private static <S,T> MapperFacade buildMapperFacade(Class<S> srcClass, Class<T> targetClass, Map<String,String> propertyMap){
        //1、获取一个默认的factory
        MapperFactory factory = new DefaultMapperFactory.Builder().build();

        //2、设置映射源 与 目标
        ClassMapBuilder<S,T> builder = factory.classMap(srcClass,targetClass);

        //3、添加字段映射
        for (String key:propertyMap.keySet()) {
            builder.field(key,propertyMap.get(key));
        }
        //4、剩余字段按默认处理
        builder.byDefault().register();
        return factory.getMapperFacade();
    }

    /**
     * 根据对象属性映射map进行复制
     * @param src           源对象
     * @param targetClass  目标类型
     * @param propertyMap  属性映射map
     * @param <S>           源对象类型
     * @param <T>           目标对象类型
     * @return  复制后的对象
     */
    public static <S,T> T mapper(S src,Class<T> targetClass,Map<String,String> propertyMap){
        return buildMapperFacade(src.getClass(),targetClass,propertyMap).map(src,targetClass);
    }

    /**
     * 根据对象属性映射map进行集合复制
     * @param srcList
     * @param srcClass
     * @param targetClass
     * @param propertyMap
     * @param <S>
     * @param <T>
     * @return 复制后的对象集合
     */
    public static <S,T> List<T> mapper(Iterable<S> srcList,Class<S> srcClass,Class<T> targetClass,Map<String,String> propertyMap){
        return buildMapperFacade(srcClass,targetClass,propertyMap).mapAsList(srcList, TypeFactory.valueOf(srcClass),TypeFactory.valueOf(targetClass));
    }
    /****************************Orika 对象拷贝方式 end********************************/

    /**
     * CGlib BeanCopier
     *      缺点:不能进行不同字段名映射，相同字段名但类型不同需要Converter转换
     *      优点：效率高，能够进行多对一映射
     */
    /***************************CGlib 对象拷贝方式********************************/
    /**
     * BeanCopier只复制属性名称 类型都相同
     * 若属性名称相同 类型不同 则需要使用Converter进行转换
     */
    //Beancopire缓存 Beancopier效率很高，其最主要消耗时间都在于创建copier上 所以可以将用过的copier缓存起来
    private static final Map<String, BeanCopier> BEAN_CPOIER_CATCH;
    //ReflectASM反射构造函数 能够极大提升反射构造实例速度  缓存原因和copier一样
    private static final Map<String, ConstructorAccess> CONSTRUCTOR_ACCESS_CACHE;
    static {
        BEAN_CPOIER_CATCH = new ConcurrentHashMap<>();
        CONSTRUCTOR_ACCESS_CACHE = new ConcurrentHashMap<>();
    }
    /**
     * 工具函数：用源类型名与目标类型名生成BEAN_CPOIER_CATCH的Key
     * @param srcClass      源对象类型
     * @param targetClass   目标对象类型
     * @return              Key的String
     */
    private static <S,T> String generateKey(Class<S> srcClass,Class<T> targetClass,boolean useConverter){
        return srcClass.getName()+targetClass.getName()+useConverter;
    }
    /**
     * 工具函数：查看是否有缓存的BeanCopier
     * @param srcClass      源类型
     * @param targetClass   目标类型
     * @param <S>            源对象类型
     * @param <T>            目标对象类型
     * @return
     */
    private static <S,T> BeanCopier getBeanCopier(Class<S> srcClass, Class<T> targetClass,boolean useConverter){
        String key = generateKey(srcClass,targetClass,useConverter);
        BeanCopier copier = null;
        if (BEAN_CPOIER_CATCH.containsKey(key)){
            copier = BEAN_CPOIER_CATCH.get(key);
        }else{
            copier = BeanCopier.create(srcClass,targetClass,useConverter);
            BEAN_CPOIER_CATCH.put(key,copier);
        }
        return copier;
    }

    /**
     *  对象复制
     * @param src       源对象
     * @param target    目标对象
     * @param <S>       源类型
     * @param <T>       目标类型
     */
    public static<S,T> void copyProperties(S src,T target){
        BeanCopier copier = getBeanCopier(src.getClass(),target.getClass(),false);
        copier.copy(src,target,null);
    }

    /**
     * 自定义转换器复制对象
     * @param src       源对象
     * @param target    目标对象
     * @param converter  转换器
     * @param <S>       源目标类型
     * @param <T>       目标对象类型
     */
    public static <S,T> void copyProperties(S src,T target,Converter converter){
        if (converter!=null){
            BeanCopier copier = getBeanCopier(src.getClass(),target.getClass(),true);
            copier.copy(src,target,converter);
        }else{
            try {
                throw new Exception("Converter can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对象复制
     * @param src           源对象
     * @param targetClass   目标对象类型
     * @param <S>           源对象类型
     * @param <T>           目标对象类型
     * @return  目标对象
     */
    public static <S,T> T copyProperties(S src,Class<T> targetClass){
        T target = null;
        try {
            target = targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        copyProperties(src,target);
        return target;
    }


    /**
     * 工具函数：ReflectASM 反射构造
     * @param targetClass   目标类型
     * @param <T>
     * @return  ReflectASM的构造对象
     */
    private static <T> ConstructorAccess<T> getConstructorAccess(Class<T> targetClass) {
        ConstructorAccess<T> constructorAccess = CONSTRUCTOR_ACCESS_CACHE.get(targetClass.getName());
        if (constructorAccess != null) {
            return constructorAccess;
        }
        try {
            constructorAccess = ConstructorAccess.get(targetClass);
            constructorAccess.newInstance();
            CONSTRUCTOR_ACCESS_CACHE.put(targetClass.toString(), constructorAccess);
        } catch (Exception e) {
            throw new RuntimeException("Create new instance of "+targetClass+" failed: "+e.getMessage()+"");
        }
        return constructorAccess;
    }
    /**
     *  链表复制
     * @param srcList       源链表
     * @param targetList    目标链表
     * @param targetClass   目标类型
     * @param <S>           源类型
     * @param <T>           目标类型
     */
    public static <S,T> void copyList(List<S> srcList,List<T> targetList,Class<T> targetClass){
        if(srcList==null||srcList.isEmpty()){
            targetList = Collections.emptyList();
        }
        ConstructorAccess<T> constructorAccess = getConstructorAccess(targetClass);
        for (S src: srcList) {
            T t = constructorAccess.newInstance();
            copyProperties(src,t);
            targetList.add(t);
        }
    }
    /**
     * 链表复制
     * @param srcList       源链表
     * @param targetClass   目标链表元素类型
     * @param <S>           源类型
     * @param <T>           目标类型
     * @return  目标链表
     */
    public static <S,T> List<T> copyList(List<S> srcList,Class<T> targetClass){
        if(srcList==null||srcList.isEmpty()){ return Collections.emptyList();}
        List<T> targetList = new ArrayList<>(srcList.size());
        ConstructorAccess<T> constructorAccess = getConstructorAccess(targetClass);
        for (S src: srcList) {
            T t = constructorAccess.newInstance();
            copyProperties(src,t);
            targetList.add(t);
        }
        return targetList;
    }
    /****************************CGlib 对象拷贝方式 end********************************/

    /**
     * Spring BeanUtils
     *      缺点:不能进行不同字段名映射，相同字段名但类型不同需要Converter转换，效率极低
     *      优点：能够进行多对一映射，可选择忽略目标对象某些映射字段
     */
    /****************************BeanUtils 对象拷贝方式 少用********************************/
    /**
     * 根据对应属性进行复制
     * @param src                   复制来源
     * @param target                复制目标
     * @param ingnoreProperties    忽略的属性
     */
    public static void copyPorperties(Object src,Object target,String ... ingnoreProperties){
        BeanUtils.copyProperties(src,target,ingnoreProperties);
    }
    /****************************BeanUtils 对象拷贝方式********************************/
}
