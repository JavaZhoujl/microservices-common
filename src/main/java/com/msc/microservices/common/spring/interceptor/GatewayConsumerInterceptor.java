//package com.msc.microservices.common.spring.interceptor;
//
//import com.msc.microservices.common.util.ThreadHolderUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.lang.Nullable;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * 网关消费者身份拦截器,直接实现接口(因为java8默认方法)
// *
// * @author zjl
// */
//public class GatewayConsumerInterceptor implements HandlerInterceptor {
//    private static final String CONSUMER_ID = "x-consumer-id";
//    private static final String CONSUMER_CUSTOM_ID = "x-consumer-custom-id";
//    private static final String CONSUMER_USERNAME = "x-consumer-username";
//    public static final String CONSUMER_ID_KEY = ThreadHolderUtil.CONSUMER_ID_KEY;
//    public static final String CONSUMER_CUSTOM_ID_KEY = ThreadHolderUtil.CONSUMER_CUSTOM_ID_KEY;
//    public static final String CONSUMER_USERNAME_KEY = ThreadHolderUtil.CONSUMER_USERNAME_KEY;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // TODO 为了保证兼容,暂时不变,后续切换为CONSUMER_ID
//        String consumerId = request.getHeader(CONSUMER_CUSTOM_ID);
//        if (StringUtils.isNotBlank(consumerId)) {
//            ThreadHolderUtil.setValue(CONSUMER_ID_KEY, consumerId);
//        }
//        String consumerCustomId = request.getHeader(CONSUMER_CUSTOM_ID);
//        if (StringUtils.isNotBlank(consumerCustomId)) {
//            ThreadHolderUtil.setValue(CONSUMER_CUSTOM_ID_KEY, consumerCustomId);
//        }
//        String consumerUsername = request.getHeader(CONSUMER_USERNAME);
//        if (StringUtils.isNotBlank(consumerUsername)) {
//            ThreadHolderUtil.setValue(CONSUMER_USERNAME_KEY, consumerUsername);
//        }
//        return true;
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
//    }
//}
