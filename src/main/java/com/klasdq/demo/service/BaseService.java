package com.klasdq.demo.service;

import com.klasdq.demo.domain.BaseBean;
import com.klasdq.demo.persistence.mybatis.Page;
import com.klasdq.demo.persistence.mybatis.PageParam;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BaseService<T extends BaseBean> {
    /**
     * 根据 Id 获取对象
     *
     * @param id 对象的 Id
     * @return 获取到的对象或 null
     */
    T get(long id);

    /**
     * 根据条件查询对象
     *
     * @param condition 查询条件
     * @return 获取到的对象或 null
     */
    T find(Object condition);

    /**
     * 根据条件查询数据是否存在
     *
     * @param condition 查询条件
     * @return 数据是否存在
     */
    boolean has(Object condition);

    /**
     * 根据查询条件查询数据
     *
     * @param condition 查询条件
     * @return 查询到的数据列表
     */
    List<T> getList(Object condition);

    /**
     * 根据查询条件查询数据并转换为目标类型
     *
     * @param condition 查询条件
     * @param vClass    目标类对象
     * @param <V>       目标类类型
     * @return 查询并转换后的对象集合
     */
    <V> List<V> getList(Object condition, Class<V> vClass);

    /**
     * 根据分页参数分页查询数据
     *
     * @param pageParam 分页参数
     * @return 查询到的分页数据
     */
    Page<T> getPage(PageParam<?> pageParam);

    /**
     * 根据查询条件分页查询数据并转换为目标类型
     *
     * @param pageParam 分页参数
     * @param vClass    目标类对象
     * @param <V>       目标类类型
     * @return 查询并转换后的分页对象数据
     */
    <V> Page<V> getPage(PageParam<?> pageParam, Class<V> vClass);

    /**
     * 保存一个数据对象<br>
     * 如果数据的 id 不存在则执行 insert<br>
     * 否则全量更新对象
     *
     * @param bean 需要保存的对象
     */
    int save(T bean);

    /**
     * 批量保存数据对象<br>
     * 根据 Id 是否存在自动区别 insert 和 update 操作
     *
     * @param beans 需要保存的对象集合
     */
    int saveAll(Collection<T> beans);

    /**
     * 动态更新对象（为 null 的属性不更新）
     *
     * @param bean 需要更新的对象
     */
    int dynamicUpdate(T bean);

    /**
     * 根据 Id 删除对象
     *
     * @param id 对象的 Id
     * @return 删除的行数
     */
    int delete(long id);

    /**
     * 根据 Id 集合批量删除对象
     *
     * @param ids Id 集合
     * @return 删除的行数
     */
    int deleteByIds(Collection<Long> ids);
}
