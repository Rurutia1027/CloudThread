/*
 * Copyright 2024 Rurutia1027
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.aston.cloudthread.web.starter.core.executor;

import com.aston.cloudthread.web.starter.core.config.WebThreadPoolConfig;
import com.aston.cloudthread.web.starter.core.enums.WebContainerEnum;
import com.aston.cloudthread.web.starter.core.metric.WebThreadPoolMetrics;
import com.aston.cloudthread.web.starter.core.snapshot.WebThreadPoolSnapshot;

/**
 * Common interface definition for Web Thread Pool services.
 *
 * <p>Provides operations to manipulate and monitor the internal
 * thread pool of a web servlet container (e.g., Tomcat, Jetty, Undertow).
 * Implementations bind CloudThread’s dynamic configuration and observability
 * features to the underlying container’s thread pool.</p>
 */
public interface WebThreadPoolService {
    /**
     * Apply new thread pool configuration dynamically.
     *
     * <p>Updates parameters such as core pool size, maximum pool size,
     * and keep-alive time. Typically invoked when configuration changes
     * are synced from a remote config center.</p>
     *
     * @param config new thread pool configuration
     */
    void updateThreadPool(WebThreadPoolConfig config);

    /**
     * Retrieve lightweight runtime metrics of the thread pool.
     *
     * @return current metrics snapshot (non-intrusive)
     */
    WebThreadPoolMetrics getMetrics();

    /**
     * Retrieve a detailed runtime snapshot of the thread pool.
     *
     * <p>This operation may acquire locks internally; frequent
     * invocations are not recommended for performance reasons.</p>
     *
     * @return complete snapshot of thread pool state
     */
    WebThreadPoolSnapshot getRuntimeSnapshot();

    /**
     * Retrieve a textual representation of the current thread pool state.
     *
     * @return human-readable runtime status string
     */
    String getRuntimeContextStatus();

    /**
     * Identify the web servlet container type that this thread pool is attached to.
     *
     * @return container type (e.g., Tomcat, Jetty, Undertow)
     */
    WebContainerEnum getWebContainerType();
}
