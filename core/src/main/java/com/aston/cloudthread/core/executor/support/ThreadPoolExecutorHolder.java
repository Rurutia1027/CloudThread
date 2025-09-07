package com.aston.cloudthread.core.executor.support;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread pool's holder class.
 */

@Data
@AllArgsConstructor
public class ThreadPoolExecutorHolder {
    /**
     * Global thread pool unique ID.
     */
    private String threadPoolId;

    /**
     * Thread Pool.
     */
    private ThreadPoolExecutor executor;

    /**
     * Thread Pool properties.
     */
    private ThreadPoolExecutorProperties executorProperties;
}
