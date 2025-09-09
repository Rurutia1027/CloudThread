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
package com.aston.cloudthread.core.toolkit;

import cn.hutool.core.lang.Assert;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Getter
public class ThreadPoolExecutorBuilder {
    /**
     * Thread pool unique ID (UID)
     */
    private String threadPoolUID;

    /**
     * Thread pool core thread size;
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * Thread pool maximum thread size;
     */
    private Integer maximumPoolSize = corePoolSize >> 1;

    /**
     * Thread pool blocking queue type
     */
    private BlockingQueueTypeEnum workQueueType = BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE;

    /**
     * Thread pool work queue capacity
     */
    private Integer workQueueCapacity = 4096;

    /**
     * Thread pool reject policy, default AbortPolicy
     */
    private RejectedExecutionHandler rejectedHandler = new ThreadPoolExecutor.AbortPolicy();

    /**
     * Thread factory
     */
    private ThreadFactory threadFactory;

    /**
     * Idle thread alive time in seconds
     */
    private long keepAliveTimeSeconds = 30000L;

    /**
     * Allow core thread timeout or not, default not allowed
     */
    private boolean allowCoreThreadTimeOut = false;

    /**
     * Dynamic thread pool flag, default not dynamic
     */
    private boolean dynamicPool = false;

    /**
     * Thread in thread pool maximum await in milliseconds when terminated, default 0 MS
     */
    private long awaitTerminationMillis = 0L;

    /**
     * Enable current thread pool as dynamic thread pool
     */
    public ThreadPoolExecutorBuilder dynamicPool() {
        this.dynamicPool = true;
        return this;
    }

    /**
     * Set thread pool UID
     *
     * @param threadPoolUID
     */
    public ThreadPoolExecutorBuilder threadPoolUID(String threadPoolUID) {
        this.threadPoolUID = threadPoolUID;
        return this;
    }

    /**
     * Set thread pool core thread size
     *
     * @param corePoolSize thread pool core thread size
     */
    public ThreadPoolExecutorBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Set maximum size of threads in thread pool
     *
     * @param maximumPoolSize maximum threads can be hold in thread pool
     */
    public ThreadPoolExecutorBuilder maximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    /**
     * Set capacity of thread pool's work queue.
     *
     * @param workQueueCapacity capacity of work queue in thread pool
     */
    public ThreadPoolExecutorBuilder workQueueCapacity(int workQueueCapacity) {
        this.workQueueCapacity = workQueueCapacity;
        return this;
    }

    /**
     * Set type of blocking queue in thread pool
     *
     * @param workQueueType type of work queue in thread pool
     */
    public ThreadPoolExecutorBuilder workQueueType(BlockingQueueTypeEnum workQueueType) {
        this.workQueueType = workQueueType;
        return this;
    }

    /**
     * Set thread pool factory with defining thread name prefix.
     *
     * @param namePrefix prefix of thread name, "cloudThread-", such as: cloudThread-1
     */
    public ThreadPoolExecutorBuilder threadFactory(String namePrefix) {
        this.threadFactory = ThreadFactoryBuilder.builder()
                .namePrefix(namePrefix)
                .build();
        return this;
    }

    /**
     * Common way for creating thread pool builder instance, only declare thread pool's
     * inner thread prefix name, and thread is daemon or not.
     *
     * @param namePrefix thread pool's inner thread name prefix
     * @param daemon     thread pool's inner thread type daemon or not daemon,
     *                   true: daemon thread, false: non-daemon thread,
     *                   daemon thread(true) means thread will not prohibit JVM's exit.
     */
    public ThreadPoolExecutorBuilder threadFactory(String namePrefix, Boolean daemon) {
        this.threadFactory = ThreadFactoryBuilder.builder()
                .namePrefix(namePrefix)
                .daemon(daemon)
                .build();
        return this;
    }

    /**
     * Set thread factory instance to thread pool builder
     *
     * @param threadFactory thread factory instance
     */
    public ThreadPoolExecutorBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Set reject policy.
     *
     * @param rejectedHandler reject policy handler (e.g., AbortPolicy、CallerRunsPolicy）
     */
    public ThreadPoolExecutorBuilder rejectedHandler(RejectedExecutionHandler rejectedHandler) {
        this.rejectedHandler = rejectedHandler;
        return this;
    }

    /**
     * Set idle threads in thread pool alive time in seconds.
     *
     * @param keepAliveTimeSeconds
     */
    public ThreadPoolExecutorBuilder keepAliveTimeSeconds(long keepAliveTimeSeconds) {
        this.keepAliveTimeSeconds = keepAliveTimeSeconds;
        return this;
    }

    /**
     * Set whether core threads timeout is allowed.
     *
     * @param allowCoreThreadTimeOut whether core thread timeout is allowed
     */
    public ThreadPoolExecutorBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * Set maximum thread await time in milliseconds when be terminated
     *
     * @param awaitTerminationMillis maximum await time in milliseconds
     */
    public ThreadPoolExecutorBuilder awaitTerminationMillis(long awaitTerminationMillis) {
        this.awaitTerminationMillis = awaitTerminationMillis;
        return this;
    }

    /**
     * Build instance of cloud thread pool builder
     */
    public static ThreadPoolExecutorBuilder builder() {
        return new ThreadPoolExecutorBuilder();
    }

    /**
     * Build instance of cloud thread pool
     */
    public ThreadPoolExecutor build() {
        BlockingQueue<Runnable> blockingQueue = BlockingQueueTypeEnum.createBlockingQueue(workQueueType.getName(), workQueueCapacity);
        RejectedExecutionHandler rejectedHandler = Optional.ofNullable(this.rejectedHandler)
                .orElseGet(() -> new ThreadPoolExecutor.AbortPolicy());

        Assert.notNull(threadFactory, "The thread factory cannot be null.");

        ThreadPoolExecutor threadPoolExecutor;
        if (dynamicPool) {
            threadPoolExecutor = new CloudThreadExecutor(
                    threadPoolUID,
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTimeSeconds,
                    TimeUnit.SECONDS,
                    blockingQueue,
                    threadFactory,
                    rejectedHandler,
                    awaitTerminationMillis
            );
        } else {
            threadPoolExecutor = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTimeSeconds,
                    TimeUnit.SECONDS,
                    blockingQueue,
                    threadFactory,
                    rejectedHandler
            );
        }

        threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return threadPoolExecutor;
    }
}
