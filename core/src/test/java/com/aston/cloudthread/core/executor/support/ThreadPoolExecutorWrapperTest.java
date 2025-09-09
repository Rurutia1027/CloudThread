package com.aston.cloudthread.core.executor.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolExecutorWrapperTest {
    private CloudThreadExecutor executor;
    private ThreadPoolExecutorProperties props;

    @BeforeEach
    void setUp() {
        executor = new CloudThreadExecutor(
                UUID.randomUUID().toString(),
                1,
                1,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy(),
                1000L
        );

        props = ThreadPoolExecutorProperties.builder()
                .threadPoolId(executor.getThreadPoolUID())
                .coolPoolSize(1)
                .maximumPoolSize(1)
                .queueCapacity(2)
                .workingQueue("ArrayBlockingQueue")
                .rejectedHandler("AbortPolicy")
                .keeAliveTime(1L)
                .allowCoreThreadTimeout(true)
                .build();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        ThreadPoolExecutorWrapper wrapper =
                new ThreadPoolExecutorWrapper(executor.getThreadPoolUID(), executor, props);

        assertEquals(executor.getThreadPoolUID(), wrapper.getThreadPoolUID());
        assertEquals(executor, wrapper.getExecutor());
        assertEquals(props, wrapper.getExecutorProperties());
    }

    @Test
    void testSetters() {
        ThreadPoolExecutorWrapper wrapper =
                new ThreadPoolExecutorWrapper(null, null, null);

        wrapper.setThreadPoolUID(executor.getThreadPoolUID());
        wrapper.setExecutor(executor);
        wrapper.setExecutorProperties(props);

        assertEquals(executor.getThreadPoolUID(), wrapper.getThreadPoolUID());
        assertEquals(executor, wrapper.getExecutor());
        assertEquals(props, wrapper.getExecutorProperties());
    }

    @Test
    void testEqualsAndHashCode() {
        ThreadPoolExecutorWrapper wrapper1 =
                new ThreadPoolExecutorWrapper(executor.getThreadPoolUID(), executor, props);

        ThreadPoolExecutorWrapper wrapper2 =
                new ThreadPoolExecutorWrapper(executor.getThreadPoolUID(), executor, props);

        assertEquals(wrapper1, wrapper2);
        assertEquals(wrapper1.hashCode(), wrapper2.hashCode());
    }

    @Test
    void testToStringContainsUID() {
        ThreadPoolExecutorWrapper wrapper =
                new ThreadPoolExecutorWrapper(executor.getThreadPoolUID(), executor, props);

        String str = wrapper.toString();
        assertNotNull(str);
        assertTrue(str.contains(executor.getThreadPoolUID()));
    }
}