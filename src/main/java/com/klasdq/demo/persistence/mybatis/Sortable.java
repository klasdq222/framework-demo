package com.klasdq.demo.persistence.mybatis;

import com.klasdq.demo.persistence.mybatis.enums.SortDirection;

/**
 * 可排序接口
 */
public interface Sortable {
    /**
     * 获取Sort对象
     * @return
     */
    Sort getSort();

    /**
     * 设置Sort对象
     * @param sort
     */
    void setSort(Sort sort);

    /**
     * 判断是否需要排序
     * @return
     */
    default boolean hasSort() {
        return this.getSort() != null && this.getSort().isNotEmpty();
    }

    /**
     * 加入排序条件
     * @param column    列名
     * @param direction 排序方向
     * @return
     */
    default Sort addOrder(String column, SortDirection direction) {
        Sort sort = this.getSort();
        if (sort == null) {
            sort = new Sort();
            this.setSort(sort);
        }
        sort.put(column, direction.getName());
        return sort;
    }

    default String getSortString() {
        if (this.getSort() == null) {
            return "";
        }
        return this.getSort().toOrderBy();
    }
}
