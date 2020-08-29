package com.msc.microservices.common.spring.interceptor;

import com.msc.microservices.common.concurrency.Plugins;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * log4j2 MDC拦截器
 *
 * @author zjl
 */
public class Log4jMDCInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Plugins.getMDCConcurrencyStrategy().put();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Plugins.getMDCConcurrencyStrategy().clear();
    }
}
