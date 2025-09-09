package com.aston.cloudthread.core.toolkit;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Builder for creating customized {@link ThreadFactory} instances.
 * <p>
 * Supports:
 * <ul>
 *     <li>Setting a thread name prefix (e.g., "worker-")</li>
 *     <li>Daemon threads</li>
 *     <li>Thread priority (1-10)</li>
 *     <li>Custom uncaught exception handler</li>
 *     <li>Providing a backing ThreadFactory</li>
 * </ul>
 * <p>
 * Example usage:
 * <pre>{@code
 * ThreadFactory factory = ThreadFactoryBuilder.builder()
 *      .namePrefix("mythread-")
 *      .daemon(true)
 *      .priority(Thread.MAX_PRIORITY)
 *      .uncaughtExceptionHandler((t, e) -> e.printStackTrace())
 *      .build();
 * }
 * </pre>
 */
public class ThreadFactoryBuilder {
    /**
     * Base thread factory, default {@code Executors#defaultThreadFactory()}
     */
    private ThreadFactory backingThreadFactory;

    /**
     * Thread prefix name like "cloudThread-", like "cloudThread-1"
     */
    private String namePrefix;

    /**
     * Daemon thread or not, default false.
     */
    private Boolean daemon;

    /**
     * Thread priority level (1~10)
     */
    private Integer priority;

    /**
     * Uncaught exception handler
     */
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    /**
     * Create instance of ThreadFactoryBuilder
     */
    public static ThreadFactoryBuilder builder() {
        return new ThreadFactoryBuilder();
    }

    public ThreadFactoryBuilder threadFactory(ThreadFactory backingThreadFactory) {
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    public ThreadFactoryBuilder namePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    public ThreadFactoryBuilder daemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder priority(int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException("Illegal thread priority! Given thread " +
                    "priority must be in the range of [1~10]");
        }
        this.priority = priority;
        return this;
    }

    public ThreadFactoryBuilder uncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        this.uncaughtExceptionHandler = handler;
        return this;
    }

    /**
     * Construct thread factory instance
     */
    public ThreadFactory build() {
        final ThreadFactory factory = (this.backingThreadFactory != null) ?
                this.backingThreadFactory : Executors.defaultThreadFactory();
        Assert.notEmpty(namePrefix, "Thread name cannot be null or blank!");
        final AtomicLong count = (StrUtil.isNotBlank(namePrefix)) ? new AtomicLong(0) : null;

        return runnable -> {
            Thread thread = factory.newThread(runnable);

            if (count != null) {
                thread.setName(namePrefix + count.getAndIncrement());
            }

            if (daemon != null) {
                thread.setDaemon(daemon);
            }

            if (priority != null) {
                thread.setPriority(priority);
            }

            if (uncaughtExceptionHandler != null) {
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }

            return thread;
        };
    }


}
