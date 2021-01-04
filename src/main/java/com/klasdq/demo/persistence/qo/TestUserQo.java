package com.klasdq.demo.persistence.qo;

import com.klasdq.demo.domain.bean.TestUserBean;
import com.klasdq.demo.persistence.mybatis.*;

import java.util.List;

public class TestUserQo extends TestUserBean implements Sortable, DynamicSearchable {

    private List<Long> ids;

    private List<SearchCondition> searchConditions;
    private List<SearchConditionGroup> searchConditionGroups;

    private Sort sort;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public List<SearchCondition> getSearchConditions() {
        return this.searchConditions;
    }

    @Override
    public void setSearchConditions(List<SearchCondition> searchConditions) {
        this.searchConditions = searchConditions;
    }

    @Override
    public List<SearchConditionGroup> getSearchConditionGroups() {
        return this.searchConditionGroups;
    }

    @Override
    public void setSearchConditionGroups(List<SearchConditionGroup> searchConditionGroups) {
        this.searchConditionGroups = searchConditionGroups;
    }
}
