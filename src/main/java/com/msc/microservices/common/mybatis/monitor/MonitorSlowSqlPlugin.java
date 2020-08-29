package com.msc.microservices.common.mybatis.monitor;

import com.msc.microservices.common.logging.BaseLog;
import com.msc.microservices.common.logging.Channel;
import com.msc.microservices.common.logging.LevelEnum;
import com.msc.microservices.common.mybatis.MybatisEnum;
import com.msc.microservices.common.mybatis.MybatisEnumTypeHandler;
import com.msc.microservices.common.util.*;
import com.msc.microservices.common.web.Trace;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.msc.microservices.common.util.ThreadHolderUtil.TRACE_KEY;

/**
 * 监控慢SQL插件
 *
 * @author ty
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
})
public class MonitorSlowSqlPlugin implements Interceptor {
    public static final String SLOW_SQL_ENABLE = "sql.slow.enable";
    /*private static boolean POSTGRESQL_DRIVER_AVAILABLE;
    private static boolean MYSQL_DRIVER_AVAILABLE;

    static {
        try {
            Class.forName("org.postgresql.jdbc.PgPreparedStatement");
            POSTGRESQL_DRIVER_AVAILABLE = true;
        } catch (ClassNotFoundException e) {
            // ignore
            POSTGRESQL_DRIVER_AVAILABLE = false;
        }
        try {
            Class.forName("com.mysql.jdbc.PreparedStatement");
            MYSQL_DRIVER_AVAILABLE = true;
        } catch (ClassNotFoundException e) {
            // ignore
            MYSQL_DRIVER_AVAILABLE = false;
        }
    }*/

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object obj = invocation.proceed();
            return obj;
        } finally {
            long end = System.currentTimeMillis();
            long used = end - start;
            // >= 1s
            final long max = 1000L;
            if (used >= max) {
                try {
                    Object target = invocation.getTarget();
                    String sql = "unknown";
                    if (target instanceof StatementHandler) {
                        sql = actualSql(((StatementHandler) target).getBoundSql());
                    }
                    SlowSqlLog slowSqlLog = new SlowSqlLog();
                    slowSqlLog.setTraceId(Optional.ofNullable(ThreadHolderUtil.getValue(TRACE_KEY, Trace.class)).map(Trace::getTraceId).orElse(""));
                    slowSqlLog.setType(SlowSqlEnum.DML);
                    slowSqlLog.setMessage("执行DML[" + sql + "]超时1秒");
                    slowSqlLog.setStart(LocalDateTimeUtil.formatMilliPlus8(start));
                    slowSqlLog.setEnd(LocalDateTimeUtil.formatMilliPlus8(end));
                    slowSqlLog.setUsed(used);
                    BaseLog<SlowSqlLog> baseLog = new BaseLog<>();
                    baseLog.setContext(slowSqlLog);
                    baseLog.setLevel(LevelEnum.WARNING.getLevel());
                    baseLog.setLevelName(LevelEnum.WARNING.getLevelName());
                    baseLog.setChannel(Channel.SYSTEM);
                    baseLog.setMessage("slowsql log");
                    baseLog.setDatetime(LocalDateTimeUtil.getMicroSecondFormattedNow());
                    LoggerUtil.getSlowSqlLogger().info(ObjectMapperUtil.getSnakeObjectMapper().writeValueAsString(baseLog));
                } catch (Throwable ex) {
                    // ignore
                }
            }
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private String actualSql(BoundSql boundSql) {
        String preparedSql = boundSql.getSql();
        boolean mightPreparedSql = preparedSql.contains("?");
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
        if (mightPreparedSql && CollectionUtils.isNotEmpty(parameterMappingList) && boundSql.getParameterObject() instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) boundSql.getParameterObject();
            try {
                for (ParameterMapping parameterMapping : parameterMappingList) {
                    String placeHolder = parameterMapping.getProperty();
                    Object value = paramMap.get(placeHolder);
                    String translateValue;
                    // 目前只能尽量尝试覆盖各种类型
                    if (value instanceof Date) {
                        translateValue = StringUtil.withSingleQuote(DateUtil.DATE_TIME_FORMAT.format((Date) value));
                    } else if (value instanceof LocalDate) {
                        translateValue = StringUtil.withSingleQuote(LocalDateTimeUtil.LOCAL_DATE_FORMATTER.format((LocalDate) value));
                    } else if (value instanceof LocalDateTime) {
                        translateValue = StringUtil.withSingleQuote(LocalDateTimeUtil.LOCAL_DATE_TIME_FORMATTER.format((LocalDateTime) value));
                    } else if (value instanceof MybatisEnum) {
                        translateValue = String.valueOf(((MybatisEnum) value).getEnumByte());
                    }
                    // 其余的统一使用toString
                    else {
                        translateValue = String.valueOf(value);
                    }
                    preparedSql = preparedSql.replaceFirst("\\?", translateValue);
                }
            } catch (Throwable th) {
                // ignore
            }
        }
        return preparedSql;
    }

    public static void main(String[] args) {
        String sql = "SELECT id FROM user WHERE ac_id=? AND start=? AND end=? AND aa=? AND bb=? AND cc=?";
        List<ParameterMapping> parameterMappingList = new ArrayList<>();
        parameterMappingList.add(new ParameterMapping.Builder(new Configuration(), "ac_id", Object.class).build());
        parameterMappingList.add(new ParameterMapping.Builder(new Configuration(), "start", Date.class).build());
        parameterMappingList.add(new ParameterMapping.Builder(new Configuration(), "end", LocalDate.class).build());
        parameterMappingList.add(new ParameterMapping.Builder(new Configuration(), "aa", LocalDateTime.class).build());
        Configuration configuration = new Configuration();
        configuration.getTypeHandlerRegistry().register(MybatisEnum.class, MybatisEnumTypeHandler.class);
        parameterMappingList.add(new ParameterMapping.Builder(configuration, "bb", MybatisEnum.class).build());
        parameterMappingList.add(new ParameterMapping.Builder(new Configuration(), "cc", Object.class).build());
        MapperMethod.ParamMap<Object> paramMap = new MapperMethod.ParamMap<>();
        paramMap.put("ac_id", 123);
        paramMap.put("start", new Date());
        paramMap.put("end", LocalDate.now());
        paramMap.put("aa", LocalDateTime.now());
        paramMap.put("bb", (MybatisEnum) () -> (byte) 2);
        paramMap.put("cc", null);
        BoundSql boundSql = new BoundSql(new Configuration(), sql, parameterMappingList, paramMap);
        String actualSql = new MonitorSlowSqlPlugin().actualSql(boundSql);
        System.out.println(actualSql);
    }
}
