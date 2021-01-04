package com.klasdq.demo.persistence.mybatis.enums;

/**
 * SQL连接符
 */
public enum SQLConnector {
    AND, OR;

    /**
     * 获取枚举对象名称,如:SQLConnector.AND 返回的就是 AND
     * @return AND 或者 OR
     */
    public String getName() {
        return this.name();
    }
}
