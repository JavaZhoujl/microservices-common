//package com.msc.microservices.common.system;
//
//import com.epet.microservices.common.logging.ErrorLogger;
//import com.msc.microservices.common.logging.ErrorLogger;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.event.ContextClosedEvent;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//
///**
// * 优雅关闭自动配置
// *
// * @author zjl
// */
//@Configuration
//public class GracefulAutoConfiguration {
//    @Autowired(required = false)
//    private List<Graceful> gracefulList;
//
//    @Bean
//    public GracefulApplicationListener gracefulApplicationListener() {
//        return new GracefulApplicationListener(gracefulList);
//    }
//
//    /**
//     * 显示申明监听事件类型,不能使用lambda表达式
//     *
//     * @author ty
//     */
//    public static class GracefulApplicationListener implements ApplicationListener<ContextClosedEvent> {
//        private final List<Graceful> gracefulList;
//        private final AtomicBoolean grace = new AtomicBoolean(false);
//
//        public GracefulApplicationListener(List<Graceful> gracefulList) {
//            this.gracefulList = gracefulList;
//        }
//
//        @Override
//        public void onApplicationEvent(ContextClosedEvent event) {
//            if (grace.compareAndSet(false, true) && CollectionUtils.isNotEmpty(gracefulList)) {
//                int size = gracefulList.size();
//                CountDownLatch countDownLatch = new CountDownLatch(size);
//                // 暂时采用顺序执行方案
//                gracefulList.sort(Comparator.comparingInt(Graceful::getPriority));
//                // TODO 后续看需求是否对Result处理
//                Executors.newSingleThreadExecutor()
//                        .execute(() ->
//                                gracefulList.forEach(graceful -> {
//                                    try {
//                                        graceful.grace(countDownLatch);
//                                    } catch (Throwable th) {
//                                        ErrorLogger.getInstance().log("graceful instance:{" + graceful + "} fail", th);
//                                    }
//                                }));
//                // 最多等待60秒
//                try {
//                    long timeout = 60L;
//                    if (!countDownLatch.await(timeout, TimeUnit.SECONDS)) {
//                        ErrorLogger.getInstance().log("graceful listener timeout");
//                    }
//                } catch (InterruptedException e) {
//                    ErrorLogger.getInstance().log("graceful listener interrupted");
//                }
//            }
//        }
//    }
//}
