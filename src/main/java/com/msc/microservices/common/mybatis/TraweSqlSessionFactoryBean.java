package com.msc.microservices.common.mybatis;

import com.msc.microservices.common.mybatis.monitor.MonitorSlowSqlPlugin;
import com.msc.microservices.common.mybatis.monitor.MonitorSpringManagedTransactionFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import static com.msc.microservices.common.mybatis.SqlMetricPlugin.SQL_METRIC_BOUNDARY;
import static com.msc.microservices.common.mybatis.SqlMetricPlugin.SQL_METRIC_ENABLE;
import static com.msc.microservices.common.mybatis.monitor.MonitorSlowSqlPlugin.SLOW_SQL_ENABLE;

/**
 * 自定义的sqlSessionFactoryBean
 *
 * @author zjl
 */
public class TraweSqlSessionFactoryBean extends SqlSessionFactoryBean implements EnvironmentAware {
    private boolean sqlMetricEnabled = false;
    private int boundary = 0;
    private Interceptor[] plugins;
    private boolean slowSqlEnabled = true;

    public TraweSqlSessionFactoryBean() {
        this(null);
    }

    public TraweSqlSessionFactoryBean(Configuration configuration) {
        super();
        // 为空时默认设置mapUnderscoreToCamelCase=true,autoMappingBehavior=FULL
        if (configuration == null) {
            configuration = new Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setAutoMappingBehavior(AutoMappingBehavior.FULL);
        }
        setConfiguration(configuration);
        TypeHandler<?>[] typeHandlers = {
                new BigDecimalArrayTypeHandler(),
                new DoubleArrayTypeHandler(),
                new FloatArrayTypeHandler(),
                new IntegerArrayTypeHandler(),
                new LongArrayTypeHandler(),
                new ShortArrayTypeHandler(),
                new StringArrayTypeHandler(),
                new ShortPrimitiveArrayTypeHandler(),
                new IntPrimitiveArrayTypeHandler(),
                new FloatPrimitiveArrayTypeHandler(),
                new LongPrimitiveArrayTypeHandler(),
                new DoublePrimitiveArrayTypeHandler()
        };
        setTypeHandlers(typeHandlers);
    }

    @Override
    public void setPlugins(Interceptor[] plugins) {
        this.plugins = plugins;
    }

    /**
     * 真实执行设置插件,setPlugins只用于记录客户端自定义的plugin,便于后续拷贝
     */
    private void actualSetPlugins() {
        if (slowSqlEnabled) {
            // 使用自定义监控功能的事务管理器工厂类
            setTransactionFactory(new MonitorSpringManagedTransactionFactory());
            this.plugins = ArrayUtils.add(plugins == null ? new Interceptor[0] : plugins, new MonitorSlowSqlPlugin());
        }
        if (sqlMetricEnabled) {
            // 默认加载sql度量插件
            this.plugins = ArrayUtils.add(plugins == null ? new Interceptor[0] : plugins, new SqlMetricPlugin(boundary));
        }
        super.setPlugins(plugins);
    }

    @Override
    public void setEnvironment(Environment environment) {
        // 插件内新增了包装sql执行异常打印参数逻辑,所以默认开启插件,但是debug级别才会打印审计日志
        sqlMetricEnabled = environment.getProperty(SQL_METRIC_ENABLE, boolean.class, true);
        boundary = environment.getProperty(SQL_METRIC_BOUNDARY, int.class, 0);
        slowSqlEnabled = environment.getProperty(SLOW_SQL_ENABLE, boolean.class, true);
        actualSetPlugins();
    }
}
