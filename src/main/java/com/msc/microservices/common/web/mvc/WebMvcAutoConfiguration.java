package com.msc.microservices.common.web.mvc;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.msc.microservices.common.exception.BusinessDataIllegalException;
import com.msc.microservices.common.logging.ErrorLogger;
import com.msc.microservices.common.spring.converter.DeserializableEnumConverterFactory;
import com.msc.microservices.common.util.ExceptionUtil;
import com.msc.microservices.common.web.EmptyMeta;
import com.msc.microservices.common.web.ResponseBody;
import com.msc.microservices.common.web.mvc.formatter.LocalDateFormatter;
import com.msc.microservices.common.web.mvc.formatter.LocalDateTimeFormatter;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.ConstraintViolationException;

/**
 * 自定义的webmvc配置
 *
 * @author zjl
 */
@Configuration
public class WebMvcAutoConfiguration {
    /**
     * 统一异常处理器
     */
    @ConditionalOnProperty(prefix = "web", name = "rest-advice", havingValue = "true")
    @RestControllerAdvice(annotations = RestController.class)
    static class RestControllerExceptionAdvice implements EnvironmentAware {
        private Environment environment;

        /**
         * 统一处理框架方法级别校验异常
         *
         * @param ex 框架方法级别校验异常
         * @return 统一异常处理结果, 422校验错误
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseBody<Object, EmptyMeta> handleMethodValidationException(
                ConstraintViolationException ex) {
            return ResponseBody.fail(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY), ExceptionUtil.handleMethodValidationException(ex));
        }

        /**
         * 统一处理框架bean校验异常
         *
         * @param ex 框架bean校验异常
         * @return 统一异常处理结果, 422校验错误
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseBody<Object, EmptyMeta> handleBeanValidationException(
                MethodArgumentNotValidException ex) {
            return ResponseBody.fail(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY), ExceptionUtil.handleBeanValidationException(ex));
        }

        /**
         * 统一处理框架bean get/form校验异常
         *
         * @param ex 校验异常
         * @return 统一异常处理结果, 422校验错误
         */
        @ExceptionHandler(BindException.class)
        public ResponseBody<Object, EmptyMeta> handleBindException(BindException ex) {
            return ResponseBody.fail(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY), ExceptionUtil.handleBindException(ex));
        }

        /**
         * 统一处理框架MissingServletRequestParameterException校验异常
         *
         * @param ex 缺失请求参数校验异常
         * @return 统一异常处理结果, 422校验错误
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseBody<Object, EmptyMeta> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
            return ResponseBody.fail(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY), ExceptionUtil.handleMissingServletRequestParameterException(ex));
        }

        /**
         * 统一处理框架MissingPathVariableException校验异常
         *
         * @param ex 缺失请求路径参数校验异常
         * @return 统一异常处理结果, 422校验错误
         */
        @ExceptionHandler(MissingPathVariableException.class)
        public ResponseBody<Object, EmptyMeta> handleMissingPathVariableException(MissingPathVariableException ex) {
            return ResponseBody.fail(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY), ExceptionUtil.handleMissingPathVariableException(ex));
        }

        /**
         * 统一处理框架UnsatisfiedServletRequestParameterException校验异常
         *
         * @param ex 请求参数条件不满足校验异常
         * @return 统一异常处理结果, 422校验错误
         */
        @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
        public ResponseBody<Object, EmptyMeta> handleUnsatisfiedServletRequestParameterException(UnsatisfiedServletRequestParameterException ex) {
            return ResponseBody.fail(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY), ExceptionUtil.handleUnsatisfiedServletRequestParameterException(ex));
        }


        /**
         * 统一处理其余所有异常
         *
         * @param ex 异常类
         */
        @ExceptionHandler(Exception.class)
        public ResponseBody<Object, EmptyMeta> handleAllException(Exception ex) {
            // 对于业务数据非法异常不记录错误日志，这异常频繁，不可控
            if (!(ex instanceof BusinessDataIllegalException)) {
                String selfProfile = "self";
                if (ArrayUtils.contains(environment.getActiveProfiles(), selfProfile)) {
                    ex.printStackTrace();
                }
                ErrorLogger.getInstance().log("运行时异常===>" + ex.getMessage(), ex);
            }
            return ResponseBody.fail(String.valueOf(HttpStatus.BAD_REQUEST), ExceptionUtil.handleAllException(ex));
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }
    }

//    /**
//     * ResponseBody失败状态码赋值(与配置中心Apollo相关暂时注释)
//     */
//    @ConditionalOnProperty(prefix = "web", name = "business-code")
//    @Bean
//    public ResponseBodyFailCodeSetter responseBodyFailCodeSetter() {
//        return new ResponseBodyFailCodeSetter();
//    }

    @Configuration
    @ConditionalOnProperty(prefix = "spring.mvc", name = "auto", havingValue = "true")
    static class SpringMVCConfiguration implements WebMvcConfigurer {
        /**
         * 注入自定义的validator
         */
        @Autowired
        private LocalValidatorFactoryBean validatorFactoryBean;
        /**
         * 自定义的MVC扩展配置类(莫名其妙会引起循环依赖,添加@Lazy)
         */
        /**
         * 如果没有AdditionalWebMvcConfigurer bean又会报Optional dependency not present for lazy injection point,直接舍弃不用了
         */
        /*@Lazy
        @Autowired(required = false)
        private AdditionalWebMvcConfigurer additionalWebMvcConfigurer;*/

        /**
         * 注入拦截器
         */
        /*@Override
        public void addInterceptors(InterceptorRegistry registry) {
            // TODO 后期去掉,因为中台根本不传递用户信息
            // 更改为覆盖方式
            if (additionalWebMvcConfigurer != null) {
                additionalWebMvcConfigurer.addInterceptors(registry);
            } else {
                // 用户信息上下文拦截器
                UserContextInterceptor userContextInterceptor = new UserContextInterceptor();
                registry.addInterceptor(userContextInterceptor).addPathPatterns("/**");
                registry.addInterceptor(new Log4jMDCInterceptor()).addPathPatterns("/**");
            }
        }*/

        /**
         * 注入上传配置
         */
        //@Bean(name = "multipartResolver")
        public MultipartResolver multipartResolver() {
            CommonsMultipartResolver resolver = new CommonsMultipartResolver();
            resolver.setDefaultEncoding("UTF-8");
            return resolver;
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(0, processor());
        }

        @Bean
        public SnakeToCamelModelAttributeMethodProcessor processor() {
            return new SnakeToCamelModelAttributeMethodProcessor(true);
        }

        /**
         * 自定义一个jackson配置
         */
        // TODO 1:如果以后有问题,首先考虑添加Order接口;2:@JsonComponent也是一种替代方案
        @Bean
        public Jackson2ObjectMapperBuilderCustomizer snakeCustomizer() {
            Jackson2ObjectMapperBuilderCustomizer snakeCustomizer = jacksonObjectMapperBuilder -> {
                jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                jacksonObjectMapperBuilder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
                jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                jacksonObjectMapperBuilder.serializerByType(LocalDate.class, new LocalDateSerializer(dateFormatter));
                jacksonObjectMapperBuilder.deserializerByType(LocalDate.class, new LocalDateDeserializer(dateFormatter));
            };
            return snakeCustomizer;
        }

        @Override
        public Validator getValidator() {
            return validatorFactoryBean;
        }

        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverterFactory(new DeserializableEnumConverterFactory());
            registry.addFormatterForFieldType(LocalDate.class, new LocalDateFormatter());
            registry.addFormatterForFieldType(LocalDateTime.class, new LocalDateTimeFormatter());
        }
    }
}
