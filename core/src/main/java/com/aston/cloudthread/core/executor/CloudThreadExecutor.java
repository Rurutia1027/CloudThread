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
package com.aston.cloudthread.core.executor;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
@Slf4j
public class CloudThreadExecutor extends ThreadPoolExecutor {

    /**
     * Thread pool unique id (UID)
     */
    @Getter
    private final String threadPoolUID;

    /**
     * Thread pool reject policy execute times.
     */
    @Getter
    private final AtomicLong rejectCount = new AtomicLong();

    /**
     * Terminating await time in MS.
     */
    private Long awaitTerminationMillis;


    /**
     * Constructs a new {@code CloudThreadExecutor} with the specified parameters.
     *
     * @param threadPoolUID          unique identifier for this thread pool
     * @param corePoolSize           the number of core threads to keep in the pool,
     *                               even if they are idle (unless {@code allowCoreThreadTimeOut} is enabled)
     * @param maximumPoolSize        the maximum number of threads allowed in the pool
     * @param keepAliveTime          the maximum time that excess idle threads (beyond the core size)
     *                               will wait for new tasks before terminating
     * @param timeUnit               the unit for {@code keepAliveTime}
     * @param workQueue              the queue used to hold tasks before they are executed by worker threads;
     *                               only tasks submitted via {@code execute} are stored here
     * @param threadFactory          the factory used to create new threads as needed
     * @param rejectedExecHandler    the handler invoked when task execution is blocked due to
     *                               capacity limits
     * @param awaitTerminationMillis maximum time (in milliseconds) to wait for termination
     *                               after the executor is shut down
     * @throws IllegalArgumentException if:<br>
     *                                  {@code corePoolSize < 0},<br>
     *                                  {@code keepAliveTime < 0},<br>
     *                                  {@code maximumPoolSize <= 0}, or<br>
     *                                  {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException     if {@code workQueue}, {@code timeUnit}, {@code threadFactory},
     *                                  or {@code rejectedExecHandler} is {@code null}
     */
    public CloudThreadExecutor(
            @NonNull String threadPoolUID,
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            @NonNull TimeUnit timeUnit,
            @NonNull BlockingQueue<Runnable> workQueue,
            @NonNull ThreadFactory threadFactory,
            @NonNull RejectedExecutionHandler rejectedExecHandler,
            long awaitTerminationMillis) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue,
                threadFactory, rejectedExecHandler);


        // set rejected execution handler via dynamic proxy
        setRejectedExecutionHandler(rejectedExecHandler);

        // set dynamic thread pool extensible attribute: thread unique id (UID)
        this.threadPoolUID = threadPoolUID;

        // set await termination time, million second
        this.awaitTerminationMillis = awaitTerminationMillis;
    }

    /**
     * Currently, a lightweight static proxy (via Lambda) is used to enhance the
     * rejection policy. At the same time, the framework also supports replacing it with
     * a JDK dynamic proxy-based implementation.
     *
     * <pre>
     *     RejectedExecutionHandler rejectedProxy = (RejectedExecutionHandler) Proxy
     *         .newProxyInstance(
     *                 handler.getClass().getClassLoader(),
     *                 new Class[]{RejectedExecutionHandler.class},
     *                 new RejectedProxyInvocationHandler(handler, rejectCount)
     *         );
     * </pre>
     */
    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        RejectedExecutionHandler handlerWrapper = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                rejectCount.incrementAndGet();
                handler.rejectedExecution(r, executor);
            }

            @Override
            public String toString() {
                return handler.getClass().getSimpleName();
            }
        };

        super.setRejectedExecutionHandler(handlerWrapper);
    }

    @Override
    public void shutdown() {
        if (isShutdown()) {
            return;
        }
        super.shutdown();

        if (this.awaitTerminationMillis <= 0) {
            return;
        }

        log.info("Before shutting down Executor Service {}", threadPoolUID);

        try {
            boolean isTerminated = this.awaitTermination(this.awaitTerminationMillis,
                    TimeUnit.MILLISECONDS);
            if (!isTerminated) {
                log.warn("Timed out while waiting for executor {} to terminate.", threadPoolUID);
            } else {
                log.info("ExecutorService {} has been shutdown.", threadPoolUID);
            }
        } catch (InterruptedException ex) {
            log.warn("Interrupted while waiting for executor {} to terminate.", threadPoolUID);
            Thread.currentThread().interrupt();
        }

    }
}
