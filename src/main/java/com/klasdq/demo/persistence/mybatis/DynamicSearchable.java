package com.klasdq.demo.persistence.mybatis;

import java.util.ArrayList;
import java.util.List;

/**
 * 可动态查询接口，即实现该接口的能够进行动态查询
 */
public interface DynamicSearchable {
    /**
     * 获取动态查询条件 List
     * @return
     */
    List<SearchCondition> getSearchConditions();

    /**
     * 设置动态查询条件 List
     * @param searchConditions
     */
    void setSearchConditions(List<SearchCondition> searchConditions);

    /**
     * 向动态查询条件 List中加入新的条件
     * @param condition
     * @return
     */
    default List<SearchCondition> addSearchCondition(SearchCondition condition) {
        List<SearchCondition> conditions = this.getSearchConditions();
        if (conditions == null) {
            conditions = new ArrayList<>();
            this.setSearchConditions(conditions);
        }
        conditions.add(condition);
        return conditions;
    }

    /**
     * 获取动态查询条件组 List
     * @return
     */
    List<SearchConditionGroup> getSearchConditionGroups();

    /**
     * 设置动态查询条件组 List
     * @param searchConditionGroups
     */
    void setSearchConditionGroups(List<SearchConditionGroup> searchConditionGroups);


    /**
     * 向动态查询条件组 List中加入新的条件
     * @param group
     * @return
     */
    default List<SearchConditionGroup> addSearchConditionGroup(SearchConditionGroup group) {
        List<SearchConditionGroup> groups = this.getSearchConditionGroups();
        if (groups == null) {
            groups = new ArrayList<>();
            this.setSearchConditionGroups(groups);
        }
        groups.add(group);
        return groups;
    }
}
