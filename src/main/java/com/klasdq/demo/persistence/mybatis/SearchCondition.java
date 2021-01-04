package com.klasdq.demo.persistence.mybatis;

import com.klasdq.demo.persistence.mybatis.enums.SQLOperator;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 查询条件对象
 *
 */
public class SearchCondition implements Serializable {
    /**
     * 大写字母匹配
     */
    private static Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");
    /**
     * 属性名 bean中的属性名称
     */
    private String property;
    /**
     * SQL操作符
     */
    private SQLOperator operator;
    /**
     * 查询条件值
     */
    private Object value;
    /**
     * 列名 属性名对应的数据表中的列名
     */
    private String column;

    public SearchCondition(String column, SQLOperator operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public SQLOperator getOperator() {
        return operator;
    }

    public void setOperator(SQLOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     *  根据驼峰命名规则将 属性名转换为表名
     * @return  表中列名
     */
    public String getColumn() {
        if (this.column != null) {
            return column;
        }
        Matcher matcher = HUMP_PATTERN.matcher(property);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
