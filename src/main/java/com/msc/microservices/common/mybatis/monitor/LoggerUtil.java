package com.msc.microservices.common.mybatis.monitor;

import com.msc.microservices.common.consts.StringConst;
import com.msc.microservices.common.util.OSUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CronTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DirectWriteRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 日志工具类
 *
 * @author zjl
 */
class LoggerUtil {
    /**
     * 日志根目录
     */
    private static final boolean SHOULD_INIT;
    private static final String DEFAULT_SLOW_SQL_LOG_ROOT;
    private static final String SLOW_SQL_LOG_ROOT;
    private static final String SLOW_SQL_LOG_ROOT_PROP = "slowSql.logRoot";
    static final String SLOW_SQL_LOGGER_NAME = "slowSqlLogger";
    private static Logger SLOW_SQL_LOGGER;
    static String PROJECT_NAME;

    static {
        // windows操作系统
        if (OSUtil.isWindows()) {
            DEFAULT_SLOW_SQL_LOG_ROOT = "C:\\wwwlogs\\sqlmonitor\\";
        } else {
            DEFAULT_SLOW_SQL_LOG_ROOT = "/wwwlogs/sqlmonitor/";
        }
        // 新方案使用统一property取值
        PROJECT_NAME = StringConst.PROJECT_NAME;
        if (StringUtils.isBlank(PROJECT_NAME)) {
            SLOW_SQL_LOG_ROOT = null;
            SHOULD_INIT = false;
        } else {
            SLOW_SQL_LOG_ROOT = DEFAULT_SLOW_SQL_LOG_ROOT + PROJECT_NAME;
            System.setProperty(SLOW_SQL_LOG_ROOT_PROP, SLOW_SQL_LOG_ROOT);
            SHOULD_INIT = true;
        }
        createSlowSqlLogger();
    }

    /**
     * 获取slowSql日志
     */
    static final Logger getSlowSqlLogger() {
        return SLOW_SQL_LOGGER;
    }

    private static void createSlowSqlLogger() {
        if (SHOULD_INIT) {
            // 一定要用false,否则使用SLF4J获取的LoggerContext与全局的LoggerContext不一致
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration configuration = context.getConfiguration();
            final Layout layout = PatternLayout.newBuilder()
                    .withPattern("%m%n")
                    .withConfiguration(configuration)
                    .withCharset(StandardCharsets.UTF_8)
                    .withAlwaysWriteExceptions(false)
                    .withNoConsoleNoAnsi(true)
                    .build();
            final TriggeringPolicy policy = CronTriggeringPolicy.createPolicy(configuration, null, "0 0 0 * * ?");
            // directWrite策略
            RolloverStrategy directWriteRolloverStrategy = DirectWriteRolloverStrategy.newBuilder()
                    .withMaxFiles("10")
                    .withConfig(configuration)
                    .build();
            final Appender appender = RollingRandomAccessFileAppender.newBuilder()
                    .withFilePattern(SLOW_SQL_LOG_ROOT + "/slowsql_" + PROJECT_NAME + "-%d{yyyy-MM-dd}.log")
                    .withAppend(true)
                    .withName("slowSqlFile")
                    .setConfiguration(configuration)
                    .withImmediateFlush(true)
                    .withPolicy(policy)
                    .withStrategy(directWriteRolloverStrategy)
                    .withLayout(layout)
                    .build();
            appender.start();
            // 新增json日志appender
            configuration.addAppender(appender);
            AppenderRef appenderRef = AppenderRef.createAppenderRef("slowSqlFile", Level.INFO, null);
            LoggerConfig loggerConfig = AsyncLoggerConfig.createLogger("false", "info", SLOW_SQL_LOGGER_NAME, "false", new AppenderRef[]{appenderRef}, null, configuration, null);
            // appenderRef添加还不够，必须手动添加appender
            loggerConfig.addAppender(appender, Level.INFO, null);
            loggerConfig.start();
            configuration.addLogger(SLOW_SQL_LOGGER_NAME, loggerConfig);
            context.updateLoggers();
        }
        // 如果没有配置会使用默认日志
        SLOW_SQL_LOGGER = LoggerFactory.getLogger(SLOW_SQL_LOGGER_NAME);
    }
}
