package com.coder4.lmsia.cfg4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author coder4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseHytrixConfig {

    private static int DEFAULT_EXECUTION_TIMEOUT_IN_MILLISECONDS = 1000;

    private static int DEFAULT_FALL_BACK_ISOLATION_SEMAPHORE_MAX_CON_CURRENT_REQUESTS = 512;

    private static int DEFAULT_CORE_POOL_SIZE = 64;

    private static int DEFAULT_MAX_POOL_SIZE = 512;

    private static int DEFAULT_MAX_QUEUE_SIZE = 32;

    // 执行时限(毫秒)
    private int executionTimeoutInMilliseconds
            = DEFAULT_EXECUTION_TIMEOUT_IN_MILLISECONDS;

    // 启动断路器
    private boolean circuitBreakerEnabled = true;

    // (信号量隔离时)降级调用最大并发数
    private int fallbackIsolationSemaphoreMaxConcurrentRequests
            = DEFAULT_FALL_BACK_ISOLATION_SEMAPHORE_MAX_CON_CURRENT_REQUESTS;

    // 允许线程数峰值超过coreSize
    private boolean allowMaximumSizeToDivergeFromCoreSize = true;

    // 核心线程数
    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;

    // 最大线程数量
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

    // 最大队列等待数量
    private int maxQueueSize = DEFAULT_MAX_QUEUE_SIZE;
}