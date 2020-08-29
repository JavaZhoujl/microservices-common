package com.msc.microservices.common.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring上下文配置类
 *
 * @author zjl
 */
@ConfigurationProperties(prefix = "spring.context")
public class SpringContextProperties {
    /**
     * 是否自动化配置
     */
    private boolean auto;
    /**
     * 额外的校验配置文件,推荐以classpath:开头
     */
    private String additionalValidate;

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public String getAdditionalValidate() {
        return additionalValidate;
    }

    public void setAdditionalValidate(String additionalValidate) {
        this.additionalValidate = additionalValidate;
    }

    @Override
    public String toString() {
        return "SpringContextProperties{" +
                "auto=" + auto +
                ", additionalValidate='" + additionalValidate + '\'' +
                '}';
    }
}
