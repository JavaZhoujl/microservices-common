package com.msc.microservices.common.mybatis;

import com.msc.microservices.common.util.BoundaryStopWatch;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sql度量插件
 *
 * @author zjl
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SqlMetricPlugin implements Interceptor {
    public static final String SQL_METRIC_ENABLE = "sql.metric.enable";
    public static final String SQL_METRIC_BOUNDARY = "sql.metric.boundary";
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlMetricPlugin.class);
    private final ConcurrentHashMap<String, BoundaryStopWatch> STOP_WATCH_CACHE = new ConcurrentHashMap<>();
    private int boundary = 0;

    public SqlMetricPlugin() {

    }

    public SqlMetricPlugin(int boundary) {
        this.boundary = boundary;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        // 此对象已经被MethodSignature包装处理过,其实就是一个Map<String, Object>
        Object parameter = invocation.getArgs()[1];
        String sqlId = mappedStatement.getId();
        BoundaryStopWatch stopWatch = null;
        if (LOGGER.isDebugEnabled()) {
            String methodName = sqlId.substring(sqlId.lastIndexOf(".") + 1);
            stopWatch = getStopWatch(sqlId);
            stopWatch.start(methodName);
        }
        try {
            return invocation.proceed();
        } catch (Throwable th) {
            // 默认日志输出param是在debug级别,而抛出异常是在设置参数后执行时才会出现,所以在执行前ParameterHandler中就打印会造成浪费,只能在抛出异常后打印
            // 而此时只能打印所有参数对象,无法一一映射sql中的param。如果此时打印参数日志,会造成sql错误信息和参数无法显示在一条日志记录中,在请求量大的情况
            // 下很难唯一定位，所以采用包装异常，再次抛出方式
            throw new SqlExecutionErrorParamException(parameter, th);
        } finally {
            if (stopWatch != null && LOGGER.isDebugEnabled()) {
                // sql度量并非完全线程安全,只是作为一个测试和开发环境的sql执行时长参考,真正最精准的执行时长是当前方法中直接计算
                stopWatch.stop();
                LOGGER.debug(stopWatch.prettyPrint());
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

    private BoundaryStopWatch getStopWatch(String id) {
        BoundaryStopWatch stopWatch = boundary > 0 ? new BoundaryStopWatch(id, boundary) : new BoundaryStopWatch(id);
        BoundaryStopWatch old = STOP_WATCH_CACHE.putIfAbsent(id, stopWatch);
        return old == null ? stopWatch : old;
    }
}
