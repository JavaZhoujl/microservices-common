package com.msc.microservices.common.spring.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 空闲端口环境变量设置
 *
 * @author zjl
 */
public class FreePortEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        RangeFreeRepeatablePropertySource.addToEnvironment(environment);
    }
}
