package com.msc.microservices.common.server;

import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * tomcat优雅关闭自动配置
 *
 * @author zjl
 */
@Configuration
@ConditionalOnClass({Servlet.class, Tomcat.class})
public class TomcatGracefulAutoConfiguration {
    @Bean
    public TomcatGraceful tomcatGraceful() {
        return new TomcatGraceful();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer(TomcatGraceful tomcatGraceful) {
        return factory -> factory.addConnectorCustomizers(tomcatGraceful);
    }
}
