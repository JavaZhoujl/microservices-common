package com.msc.microservices.common.hystrix;

import com.msc.microservices.common.concurrency.RequestContextCallable;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

import java.util.concurrent.Callable;

/**
 * 自定义的hystrix concurrencyStrategy插件
 *
 * @author zjl
 */
public class RequestContextHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new RequestContextCallable<>(callable);
    }
}
