package com.coder4.lmsia.cfg4j;

import com.coder4.sbmvt.trace.TraceIdContext;
import com.coder4.sbmvt.trace.TraceIdUtils;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/**
 * @author coder4
 */
public class BaseHystrixCommend<R> extends HystrixCommand<R> {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private final Supplier<R> realSupplier;

    private final Supplier<R> fallbackSupplier;

    public BaseHystrixCommend(String key, Supplier<R> realSupplier, Supplier<R> fallbackSupplier) {
        this(key, new BaseHytrixConfig(), realSupplier, fallbackSupplier);
    }

    public BaseHystrixCommend(String key, BaseHytrixConfig config, Supplier<R> realSupplier, Supplier<R> fallbackSupplier) {
        super(Setter
                // 3个key合一
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
                .andCommandKey(HystrixCommandKey.Factory.asKey(key))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(key))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(config.getExecutionTimeoutInMilliseconds())
                                .withCircuitBreakerEnabled(config.isCircuitBreakerEnabled())
                                .withFallbackIsolationSemaphoreMaxConcurrentRequests(config.getFallbackIsolationSemaphoreMaxConcurrentRequests())
                )
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.defaultSetter()
                                .withAllowMaximumSizeToDivergeFromCoreSize(config.isAllowMaximumSizeToDivergeFromCoreSize())
                                .withCoreSize(config.getCorePoolSize())
                                .withMaximumSize(config.getMaxPoolSize())
                                .withMaxQueueSize(config.getMaxQueueSize())                 // maxQueueSize是申请的blockingQueue的长度, 运行时无法修改
                                .withQueueSizeRejectionThreshold(config.getMaxQueueSize())  // 这是运行中真正允许的最大queue长度，运行时可以修改。默认设为maxQueueSize
                ));
        this.realSupplier = realSupplier;
        this.fallbackSupplier = fallbackSupplier;
    }

    @Override
    protected R run() throws Exception {
        if (StringUtils.isEmpty(TraceIdContext.getTraceId())) {
            TraceIdContext.setTraceId(TraceIdUtils.getTraceId());
        }
        R r = this.realSupplier.get();
        TraceIdContext.removeTraceId();
        return r;
    }

    @Override
    protected R getFallback() {
        try {
            LOG.error("enter fallback because ", getExecutionException());
            return this.fallbackSupplier.get();
        } finally {
            TraceIdContext.removeTraceId();
        }
    }

}