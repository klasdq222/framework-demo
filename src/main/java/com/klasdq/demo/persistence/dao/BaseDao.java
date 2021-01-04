package com.klasdq.demo.persistence.dao;

import com.klasdq.demo.domain.BaseBean;
import com.klasdq.demo.persistence.mybatis.BatchSaveParameter;
import com.klasdq.demo.persistence.mybatis.Page;
import com.klasdq.demo.persistence.mybatis.PageParam;

import java.util.Collection;
import java.util.List;

public interface BaseDao<T extends BaseBean>{
    /**
     * 根据Id查询对象
     *
     * @param id 对象Id
     * @return 查询到的对象
     */
    T get(long id);

    /**
     * 根据条件查询对象列表
     *
     * @param condition 查询条件对象
     * @return 对象列表
     */
    List<T> select(Object condition);

    /**
     * 分页查询对象列表
     *
     * @param pageParam 分页查询条件
     * @return 查询到的对象列表
     */
    Page<T> selectPage(PageParam<?> pageParam);

    /**
     * 保存对象
     *
     * @param bean 需要保存的对象
     * @return 新增的行数
     */
    int insert(T bean);

    /**
     * 批量新增数据
     *
     * @param parameter 批量新增参数构造器
     * @return 新增行数
     */
    int batchInsert(BatchSaveParameter<T> parameter);

    /**
     * 全属性更新对象
     *
     * @param bean 需要更新的对象
     * @return 更新行数
     */
    int update(T bean);

    /**
     * 对象非空属性更新
     *
     * @param bean 需要更新的对象
     * @return 更新行数
     */
    int dynamicUpdate(T bean);

    /**
     * 根据Id删除对象
     *
     * @param id 对象Id
     * @return 删除的行数
     */
    int delete(long id);

    /**
     * 根据Id列表删除对象
     *
     * @param ids Id列表
     * @return 删除行数
     */
    int deleteByIds(Collection<Long> ids);
}
