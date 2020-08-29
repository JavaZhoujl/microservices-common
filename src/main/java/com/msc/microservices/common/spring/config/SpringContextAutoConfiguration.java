package com.msc.microservices.common.spring.config;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.BeanValidationPostProcessor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * spring上下文通用配置
 *
 * @author zjl
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.context", name = "auto", havingValue = "true")
@EnableConfigurationProperties(SpringContextProperties.class)
public class SpringContextAutoConfiguration {
    @Autowired
    private SpringContextProperties springContextProperties;

    /**
     * 注入消息国际化bean
     */
    @Bean("messageSource")
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        // 默认的hibernate国际化文件
        messageSource.addBasenames("classpath:org/hibernate/validator/ValidationMessages");
        if (StringUtils.isNotBlank(springContextProperties.getAdditionalValidate())) {
            // 加入自定义的国际化文件
            messageSource.addBasenames(springContextProperties.getAdditionalValidate());
        }
        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setDefaultEncoding("utf-8");
        messageSource.setCacheSeconds(1800);
        return messageSource;
    }

    /**
     * 注入validator
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        Map<String, String> propertiesMap = new HashMap<>(1);
        propertiesMap.put("hibernate.validator.fail_fast", "true");
        validatorFactoryBean.setValidationPropertyMap(propertiesMap);
        validatorFactoryBean.setValidationMessageSource(messageSource());
        return validatorFactoryBean;
    }

    /**
     * 注入方法级别的校验bean
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor =
                new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

    /**
     * 注入validator依赖bean
     */
    @Bean
    public BeanValidationPostProcessor beanValidationPostProcessor() {
        BeanValidationPostProcessor beanValidationPostProcessor = new BeanValidationPostProcessor();
        beanValidationPostProcessor.setValidator(validator());
        return beanValidationPostProcessor;
    }
}
