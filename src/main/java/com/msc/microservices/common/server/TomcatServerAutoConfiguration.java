package com.msc.microservices.common.server;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * tomcat服务器自动化配置
 *
 * @author zjl
 */
@Configuration
@ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
@EnableConfigurationProperties(TomcatProperties.class)
public class TomcatServerAutoConfiguration {
    /**
     * tomcat配置,因为 TomcatWebServerFactoryCustomizer本身也会设置部分属性,所以我们这个设置只管keepAliveTimeout,maxKeepAliveRequests,nio2这三个属性
     */
    @ConditionalOnProperty(name = "server.tomcat.auto", havingValue = "true")
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(TomcatProperties tomcatProperties) {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        if (tomcatProperties.isNio2()) {
            tomcatServletWebServerFactory.setProtocol("org.apache.coyote.http11.Http11Nio2Protocol");
        }
        tomcatServletWebServerFactory.addConnectorCustomizers(connector -> {
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
            if (protocolHandler instanceof AbstractHttp11Protocol) {
                AbstractHttp11Protocol http11NioProtocol = (AbstractHttp11Protocol) protocolHandler;
                // 由于我们内置Tomcat使用NIO,所以maxConnection>>maxThread>>CPU
                // 如果不设置,默认等于server.connectionTimeout
                if (tomcatProperties.getKeepAliveTimeout() > 0) {
                    http11NioProtocol.setKeepAliveTimeout(tomcatProperties.getKeepAliveTimeout());
                }
                // 允许最多keep-alive的socket连接数量默认100
                if (tomcatProperties.getMaxKeepAliveRequests() > 0) {
                    http11NioProtocol.setMaxKeepAliveRequests(tomcatProperties.getMaxKeepAliveRequests());
                }
                // 神奇的名字,居然叫backlog==acceptCount
                // fix:2019/1/8 spring boot 2已经支持设置acceptCount,为了兼容旧版系统，我们还是会考虑backlog
                if (tomcatProperties.getAcceptCount() <= 0 && tomcatProperties.getBacklog() > 0) {
                    http11NioProtocol.setBacklog(tomcatProperties.getBacklog());
                }
            }
        });
        return tomcatServletWebServerFactory;
    }

    /**
     * 支持RFC2109标准
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieCustomizer() {
        return factory -> factory.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
    }
}
