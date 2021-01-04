package com.klasdq.demo.persistence.mybatis.intercepts;


import com.klasdq.demo.common.utils.ReflectUtils;
import com.klasdq.demo.persistence.mybatis.Page;
import com.klasdq.demo.persistence.mybatis.PageParam;
import com.klasdq.demo.persistence.mybatis.Sortable;
import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PageInterceptor implements Interceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageInterceptor.class);
    private static final ObjectFactory OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

    private static final Pattern PATTERN_GROUP = Pattern.compile("\\s(GROUP\\s+BY|DISTINCT)\\s", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_FROM = Pattern.compile("\\sFROM\\s", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_ORDERBY = Pattern.compile("\\sORDER\\sBY\\s", Pattern.CASE_INSENSITIVE);

    class BoundSqlSource implements SqlSource {
            private BoundSql boundSql;

        BoundSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        Object[] args = invocation.getArgs();

        Object param = args[1];
        if (param == null) {
            return invocation.proceed();
        }
        if (param instanceof PageParam) {
            PageParam page = (PageParam) param;
            BoundSql boundSql = statement.getBoundSql(page.getCondition());

            Connection connection = getConnection((Executor) invocation.getTarget(), statement.getStatementLog());
            long count = page.getCount() == null ? queryCount(connection, statement, boundSql, page.getCondition()) : page.getCount();

            Page dataPage = new Page(page.getPage(), page.getSize(), count);
            if (count > 0) {
                String pageSql = getPageSql(statement, boundSql.getSql(), page);
                args[0] = buildNewMappedStatement(statement, boundSql, pageSql);
                args[1] = page.getCondition();
                dataPage.setContent((List) invocation.proceed());
            }
            return Collections.singletonList(dataPage);
        } else if (param instanceof Sortable) {
            Sortable sort = (Sortable) param;
            if (sort.hasSort()) {
                BoundSql boundSql = statement.getBoundSql(sort);
                String originSql = boundSql.getSql();
                if (!PATTERN_ORDERBY.matcher(originSql).find()) {
                    String sortSql = originSql + sort.getSortString();
                    args[0] = buildNewMappedStatement(statement, boundSql, sortSql);
                    return invocation.proceed();
                }
            }
        }
        return invocation.proceed();
    }
    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * MyBatis通过MappedStatement描述<select|update|insert|delete>或者@Select、@Update等注解配置
     * @param ms
     * @param boundSql
     * @param sql
     * @return
     */
    private MappedStatement buildNewMappedStatement(MappedStatement ms, BoundSql boundSql, String sql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        BoundSqlSource sqlSource = new BoundSqlSource(newBoundSql);
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    // 查询总数
    private long queryCount(Connection connection, MappedStatement statement, BoundSql boundSql, Object param) throws SQLException {
        String sql = boundSql.getSql();
        boolean needOutCount = PATTERN_GROUP.matcher(sql).find();
        String countSql;
        if (needOutCount) {
            countSql = "SELECT COUNT(*) FROM (" + sql + ") AS counts";
        } else {
            Matcher matcher = PATTERN_FROM.matcher(sql);
            if (matcher.find()) {
                int endIndex = matcher.end();
                countSql = "SELECT COUNT(*) FROM " + sql.substring(endIndex);
            } else {
                return 0;
            }
        }

        PreparedStatement countStmt = connection.prepareStatement(countSql);
        DefaultParameterHandler handler = new DefaultParameterHandler(statement, param, boundSql);
        handler.setParameters(countStmt);

        ResultSet resultSet = countStmt.executeQuery();
        long count = 0;
        if (resultSet.next()) {
            count = resultSet.getLong(1);
        }
        resultSet.close();
        countStmt.close();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<==     Result: " + count);
        }
        return count;
    }

    private Connection getConnection(Executor executor, Log log) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException {
        BaseExecutor baseExecutor = null;
        if (executor instanceof CachingExecutor) {
            baseExecutor = (BaseExecutor) ReflectUtils.getFieldValue(executor, "delegate");
        } else if (executor instanceof BaseExecutor) {
            baseExecutor = (BaseExecutor) executor;
        }

        Method connectionMethod = BaseExecutor.class.getDeclaredMethod("getConnection", Log.class);
        if (!connectionMethod.isAccessible()) {
            connectionMethod.setAccessible(true);
        }

        if (baseExecutor != null) {
            return (Connection) connectionMethod.invoke(baseExecutor, log);
        }
        return executor.getTransaction().getConnection();
    }

    private static String getPageSql(MappedStatement ms, String sql, PageParam page) {
        String sortString;
        if (page.getCondition() != null && page.getCondition() instanceof Sortable) {
            sortString = ((Sortable) page.getCondition()).getSortString();
        } else {
            sortString = "";
        }

        StringBuilder sb = new StringBuilder();
        if (ms.getResource().toLowerCase().contains("oracle")) {
            sb.append("SELECT * FROM ( SELECT a.*, ROWNUM rown ");
            sb.append("FROM (").append(sql).append(sortString).append(") a ");
            sb.append("WHERE rownum <= ").append(page.getEnd()).append(") WHERE rown > ").append(page.getStart()).append(" ");
            return sb.toString();
        } else if (ms.getResource().toLowerCase().contains("sqlserver")) {
            sb.append("SELECT TOP ").append(page.getSize()).append(" * ");
            sb.append("FROM (SELECT ROW_NUMBER() OVER(").append(sortString).append(") AS rownumber, b.* FROM (").append(sql).append(") b ");
            sb.append(") a WHERE rownumber > ").append(page.getStart());
            return sb.toString();
        } else {
            return sql + sortString + " LIMIT " + page.getStart() + "," + page.getSize();
        }
    }
}
