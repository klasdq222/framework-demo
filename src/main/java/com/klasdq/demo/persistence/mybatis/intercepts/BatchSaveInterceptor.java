package com.klasdq.demo.persistence.mybatis.intercepts;


import com.klasdq.demo.common.utils.ReflectUtils;
import com.klasdq.demo.persistence.mybatis.BatchSaveParameter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Properties;


@Intercepts({@Signature(type = StatementHandler.class, method = "update", args = {Statement.class})})
public class BatchSaveInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler routingStatementHandler = (RoutingStatementHandler) invocation.getTarget();
            StatementHandler delegate = (StatementHandler) ReflectUtils.getFieldValue(routingStatementHandler, "delegate");
            if (delegate instanceof PreparedStatementHandler) {
                PreparedStatementHandler statementHandler = (PreparedStatementHandler) delegate;
                BoundSql boundSql = (BoundSql) ReflectUtils.getFieldValue(statementHandler, "boundSql");
                if (boundSql == null) {
                    throw new IllegalArgumentException("Can't get BoundSql from PreparedStatementHandler");
                }

                Object param = boundSql.getParameterObject();
                if (param instanceof BatchSaveParameter) {
                    BatchSaveParameter parameter = (BatchSaveParameter) param;

                    MappedStatement statement = (MappedStatement) ReflectUtils.getFieldValue(statementHandler, "mappedStatement");
                    if (statement == null) {
                        throw new IllegalArgumentException("Can't get MappedStatement from PreparedStatementHandler");
                    }

                    SqlCommandType type = statement.getSqlCommandType();

                    PreparedStatement ps = (PreparedStatement) invocation.getArgs()[0];
                    int rows = ps.getUpdateCount();

                    if (type == SqlCommandType.INSERT) {
                        Executor executor = (Executor) ReflectUtils.getFieldValue(statementHandler, "executor");

                        KeyGenerator keyGenerator = statement.getKeyGenerator();
                        if (keyGenerator instanceof Jdbc3KeyGenerator) {
                            Jdbc3KeyGenerator jdbc3KeyGenerator = (Jdbc3KeyGenerator) keyGenerator;
                            // 获取实际需要设置自增ID的对象列表
                            Collection params = parameter.getData();
                            jdbc3KeyGenerator.processBatch(statement, ps, params);
                        } else {
                            keyGenerator.processAfter(executor, statement, ps, param);
                        }
                    }
                    return rows;
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
