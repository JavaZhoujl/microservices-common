//package com.msc.microservices.common.server;
//
//import com.epet.microservices.common.logging.ErrorLogger;
//import com.epet.microservices.common.system.Graceful;
//import com.epet.microservices.common.system.Result;
//import com.msc.microservices.common.base.Result;
//import com.msc.microservices.common.logging.ErrorLogger;
//import org.apache.catalina.connector.Connector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * tomcat容器优雅关闭
// *
// * @author zjl
// */
//public class TomcatGraceful implements Graceful, TomcatConnectorCustomizer {
//    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatGraceful.class);
//    private Connector connector;
//
//    @Override
//    public Result grace(CountDownLatch countDownLatch) {
//        // tomcat暂停接受新的请求
//        connector.pause();
//        Executor executor = connector.getProtocolHandler().getExecutor();
//        if (executor instanceof ThreadPoolExecutor) {
//            try {
//                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
//                threadPoolExecutor.shutdown();
//                final long maxSeconds = 30L;
//                if (!threadPoolExecutor.awaitTermination(maxSeconds, TimeUnit.SECONDS)) {
//                    ErrorLogger.getInstance().log("tomcat thread pool shutdown timeout > 30S");
//                    return Result.FAIL;
//                }
//                return Result.SUCCESS;
//            } catch (InterruptedException e) {
//                // ignore
//                return Result.FAIL;
//            } finally {
//                countDownLatch.countDown();
//                LOGGER.info("Graceful ==> tomcat pause");
//            }
//        }
//        return Result.SUCCESS;
//    }
//
//    @Override
//    public int getPriority() {
//        return Integer.MIN_VALUE + 1;
//    }
//
//    @Override
//    public void customize(Connector connector) {
//        this.connector = connector;
//    }
//}
