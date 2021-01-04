package com.klasdq.demo.persistence.mybatis;



import com.klasdq.demo.persistence.mybatis.enums.SQLConnector;
import com.klasdq.demo.persistence.mybatis.enums.SQLOperator;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL 查询条件组
 * @author klasdq
 */
public class SearchConditionGroup implements Serializable {
    /**
     * 条件组 外部连接符
     */
    private SQLConnector outerConnector;
    /**
     * 条件组 内部连接符
     */
    private SQLConnector innerConnector;
    /**
     * 查询条件组
     */
    private List<SearchCondition> conditions;

    public SearchConditionGroup(SQLConnector outerConnector, SQLConnector innerConnector, List<SearchCondition> conditions) {
        this.outerConnector = outerConnector;
        this.innerConnector = innerConnector;
        this.conditions = conditions;
    }

    /**
     * 创建多列 LIKE 查询条件组
     * @param searchString  LIKE比对的字符串
     * @param columns       比对的列
     * @return 查询条件组：外连接符AND 内连接符OR 运算符LIKE
     */
    public static SearchConditionGroup buildMultiColumnsSearch(String searchString, String... columns) {
        return buildMultiColumnsSearch(SQLConnector.AND, searchString, columns);
    }


    /**
     * 创建多列 LIKE 查询条件组 组内部连接符为OR
     * @param outerConnector    外连接符
     * @param searchString      LIKE比对的字符串
     * @param columns           比对列
     * @return  查询条件组：外连接符outerConnector 内连接符OR 运算符LIKE
     */
    public static SearchConditionGroup buildMultiColumnsSearch(SQLConnector outerConnector, String searchString, String... columns) {
        if (columns.length == 0) {
            return null;
        }
        List<SearchCondition> conditions = Arrays.stream(columns).map(c -> new SearchCondition(c, SQLOperator.LIKE, searchString)).collect(Collectors.toList());
        return new SearchConditionGroup(outerConnector, SQLConnector.OR, conditions);
    }

    public SQLConnector getOuterConnector() {
        return outerConnector;
    }

    public void setOuterConnector(SQLConnector outerConnector) {
        this.outerConnector = outerConnector;
    }

    public SQLConnector getInnerConnector() {
        return innerConnector;
    }

    public void setInnerConnector(SQLConnector innerConnector) {
        this.innerConnector = innerConnector;
    }

    public List<SearchCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<SearchCondition> conditions) {
        this.conditions = conditions;
    }
}
