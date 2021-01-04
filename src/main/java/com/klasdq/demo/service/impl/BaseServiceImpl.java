package com.klasdq.demo.service.impl;

import com.klasdq.demo.common.utils.BeanMapper;
import com.klasdq.demo.domain.BaseBean;
import com.klasdq.demo.persistence.dao.BaseDao;
import com.klasdq.demo.persistence.mybatis.BatchSaveParameter;
import com.klasdq.demo.persistence.mybatis.Page;
import com.klasdq.demo.persistence.mybatis.PageParam;
import com.klasdq.demo.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.*;

public class BaseServiceImpl<T extends BaseBean,D extends BaseDao<T>> implements BaseService<T> {
    @Autowired
    protected D dao;

    private void preInsert(BaseBean bean) {
        bean.setCreateDt(new Date());
        bean.setUpdateDt(new Date());
    }

    private void preUpdate(BaseBean bean){
        bean.setUpdateDt(new Date());
    }

    @Override
    public T get(long id) {
        return dao.get(id);
    }

    @Override
    public T find(Object condition) {
        List<T> list = this.getList(condition);
        return list==null || list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean has(Object condition) {
        List<T> list = this.getList(condition);
        return list!=null && list.size()>0;
    }

    @Override
    public List<T> getList(Object condition) {
        return dao.select(condition);
    }

    @Override
    public<V> List<V> getList(Object condition, Class<V> vClass){
        List<T> list = this.getList(condition);
        if(list==null || list.isEmpty()){ return new ArrayList<>();}
        //强制将泛型T转换为具体的类型
//        Class<T> beanClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return BeanMapper.copyList(list,vClass);
    }

    @Override
    public Page<T> getPage(PageParam<?> pageParam){
        return dao.selectPage(pageParam);
    }

    @Override
    public <V> Page<V> getPage(PageParam<?> pageParam, Class<V> vClass){
        Page<T> page = this.getPage(pageParam);
        Class<T> beanClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return page.map(vClass);
    }


    @Override
    public int save(T bean){
        if (bean.getId()==null){
            preInsert(bean);
             return dao.insert(bean);
        }else{
            preUpdate(bean);
            return dao.update(bean);
        }
    }

    @Override
    public int saveAll(Collection<T> beans){
        if (beans==null||beans.isEmpty()) {
            return 0;
        }
        int count = 0;
        Set<T> inserts = new HashSet<>();
        for (T bean : beans) {
            if (bean.getId() == null) {
                preInsert(bean);
                inserts.add(bean);
            } else {
                preUpdate(bean);
                dao.update(bean);
                count++;
            }
        }

        if(!inserts.isEmpty()){
            count += dao.batchInsert(BatchSaveParameter.of(inserts));
        }
        return count;
    }

    @Override
    public  int dynamicUpdate(T bean){
        return dao.dynamicUpdate(bean);
    }

    @Override
    public int delete(long id){
        return dao.delete(id);
    }

    @Override
    public int deleteByIds(Collection<Long> ids){
        return dao.deleteByIds(ids);
    }
}
