package com.msc.microservices.common.spring.interceptor;

import com.msc.microservices.common.util.ThreadHolderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网关消费者身份拦截器,直接实现接口(因为java8默认方法)
 *
 * @author zjl
 */
public class GatewayConsumerInterceptor implements HandlerInterceptor {

    private static final String CONSUMER_ID = "x-consumer-id";
    private static final String CONSUMER_CUSTOM_ID = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String consumerId = request.getHeader(CONSUMER_CUSTOM_ID);
        if (StringUtils.isNotBlank(consumerId)) {
            ThreadHolderUtil.setValue(CONSUMER_ID, consumerId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
