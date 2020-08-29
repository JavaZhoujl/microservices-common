package com.msc.microservices.common.mybatis.monitor;

import com.msc.microservices.common.logging.BaseLog;
import com.msc.microservices.common.logging.Channel;
import com.msc.microservices.common.logging.LevelEnum;
import com.msc.microservices.common.util.LocalDateTimeUtil;
import com.msc.microservices.common.util.ObjectMapperUtil;
import com.msc.microservices.common.util.ThreadHolderUtil;
import com.msc.microservices.common.web.Trace;
import org.mybatis.spring.transaction.SpringManagedTransaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.msc.microservices.common.util.ThreadHolderUtil.TRACE_KEY;

/**
 * 具备监控功能的spring事务管理器
 *
 * @author zjl
 */
public class MonitorSpringManagedTransaction extends SpringManagedTransaction {

    public MonitorSpringManagedTransaction(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        long start = System.currentTimeMillis();
        Connection connection = super.getConnection();
        try {
            long end = System.currentTimeMillis();
            long used = end - start;
            // 获取数据库连接池超过500豪秒
            final long max = 500L;
            if (used >= max) {
                SlowSqlLog slowSqlLog = new SlowSqlLog();
                slowSqlLog.setTraceId(Optional.ofNullable(ThreadHolderUtil.getValue(TRACE_KEY, Trace.class)).map(Trace::getTraceId).orElse(""));
                slowSqlLog.setMessage("获取数据库连接池超时500毫秒");
                slowSqlLog.setType(SlowSqlEnum.CONNECTION_POOL);
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
            }
        } catch (Throwable th) {
            // ignore
        }
        return connection;
    }
}
