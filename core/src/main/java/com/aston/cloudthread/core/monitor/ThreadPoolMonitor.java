package com.aston.cloudthread.core.monitor;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class ThreadPoolMonitor {
    private ScheduledExecutorService scheduler;
    private Map<String, ThreadPoolRuntimeContext> micrometerMonitorCache;

    private static final String METRIC_NAME_PREFIX = "dynamic.thread-pool";
    private static final String DYNAMIC_THREAD_POOL_ID_TAG = METRIC_NAME_PREFIX + ".id";
    private static final String APPLICATION_NAME_TAG = "application.name";

    /**
     * Setup scheduled checking tasks.
     */
    public void start() {

    }
}
