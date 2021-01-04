package com.klasdq.demo.persistence.mybatis;



import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <!--StringUtils包-->
 * <dependency>
 *     <groupId>commons-lang</groupId>
 *     <artifactId>commons-lang</artifactId>
 *     <version>2.5</version>
 * </dependency>
 * Map<排序字段, 排序方式> 如map.put("name", "asc");
 */
public class Sort extends LinkedHashMap<String, String> {
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";

    public boolean isNotEmpty() {
        return this.size() > 0;
    }

    //在设置排序的字段和方式之后，解析成sql语句，然后在mapper中使用

    /**
     * 获取排序SQL子句
     * @return
     */
    public String toOrderBy() {
        if (this.isEmpty()) {
            return "";
        }
        /**
         * 1、去掉列名（KEY）为空的项
         * 2、将map中的元素转换为“ age DESC ”的字符串
         * 3、返回" ORDER BY age DESC,birthday ASC "形式
         */
        List<String> list = this.keySet().stream()
                .filter(StringUtils::isNotBlank)
                .map(p -> String.format(" %s %s", p, DESC.equalsIgnoreCase(this.get(p)) ? DESC : ASC))
                .collect(Collectors.toList());
        return list.isEmpty() ? "" : " ORDER BY" + StringUtils.join(list, ',');
    }
}
