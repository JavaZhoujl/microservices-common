package com.msc.microservices.common.spring.config;

import com.msc.microservices.common.util.InetUtil;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

/**
 * 范围获取空闲端口,且获取值是重复的
 *
 * @author zjl
 */
public class RangeFreeRepeatablePropertySource extends PropertySource<Integer> {
    public static final String RANGE_FREE_REPEATABLE_PROPERTY_SOURCE_NAME = "rangeFreeRepeatable";
    private static final String PREFIX = "rangeFreeRepeatable.";
    private int cachePort = 0;

    public RangeFreeRepeatablePropertySource() {
        this(RANGE_FREE_REPEATABLE_PROPERTY_SOURCE_NAME);
    }

    public RangeFreeRepeatablePropertySource(String name) {
        super(name, 9999);
    }

    @Override
    public Object getProperty(String name) {
        if (!name.startsWith(PREFIX)) {
            return null;
        }
        synchronized (this) {
            if (cachePort > 0) {
                return cachePort;
            }
            Object value = getRandomValue(name.substring(PREFIX.length()));
            if (value != null && value instanceof Integer && (Integer) value != 0) {
                cachePort = (Integer) value;
            }
            return value;
        }
    }

    private Object getRandomValue(String type) {
        String range = getRange(type, "int");
        if (range != null) {
            return getNextIntInRange(range);
        }
        return null;
    }

    private String getRange(String type, String prefix) {
        if (type.startsWith(prefix)) {
            int startIndex = prefix.length() + 1;
            if (type.length() > startIndex) {
                return type.substring(startIndex, type.length() - 1);
            }
        }
        return null;
    }

    private int getNextIntInRange(String range) {
        String[] tokens = StringUtils.commaDelimitedListToStringArray(range);
        int start = Integer.parseInt(tokens[0]);
        int max = getSource();
        int end = max;
        final int length = 2;
        if (tokens.length == length) {
            end = Integer.parseInt(tokens[1]);
            end = end > max ? max : end;
        }
        int freePort = 0;
        for (int port = start; port <= end; port++) {
            if (InetUtil.isPortFree(port)) {
                freePort = port;
                break;
            }
        }
        return freePort;
    }

    public static void addToEnvironment(ConfigurableEnvironment environment) {
        environment.getPropertySources()
                .addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, new RangeFreeRepeatablePropertySource());
    }
}
