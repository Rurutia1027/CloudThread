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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadPoolExecutorPropertiesTest {
    @Test
    void testBuilderCreatePropertiesCorrectly() {
        ThreadPoolExecutorProperties.NotifyConfig notifyConfig =
                new ThreadPoolExecutorProperties.NotifyConfig("user1,user2", 10L);
        ThreadPoolExecutorProperties.AlarmConfig alarmConfig =
                new ThreadPoolExecutorProperties.AlarmConfig(true, 90, 70);

        ThreadPoolExecutorProperties props = ThreadPoolExecutorProperties.builder()
                .threadPoolId("pool-1")
                .coolPoolSize(2)
                .maximumPoolSize(4)
                .queueCapacity(100)
                .workingQueue("ArrayBlockingQueue")
                .rejectedHandler("AbortPolicy")
                .keeAliveTimeSeconds(60L)
                .allowCoreThreadTimeout(true)
                .notify(notifyConfig)
                .alarmConfig(alarmConfig)
                .build();

        assertEquals("pool-1", props.getThreadPoolId());
        assertEquals(2, props.getCoolPoolSize());
        assertEquals(4, props.getMaximumPoolSize());
        assertEquals(100, props.getQueueCapacity());
        assertEquals("ArrayBlockingQueue", props.getWorkingQueue());
        assertEquals(60L, props.getKeeAliveTimeSeconds());
        assertTrue(props.getAllowCoreThreadTimeout());
        assertEquals(notifyConfig, props.getNotify());
        assertEquals(alarmConfig, props.getAlarmConfig());
    }
    @Test
    void testChainedSetters() {
        ThreadPoolExecutorProperties props = new ThreadPoolExecutorProperties()
                .setThreadPoolId("pool-2")
                .setCoolPoolSize(1)
                .setMaximumPoolSize(2)
                .setQueueCapacity(10)
                .setWorkingQueue("LinkedBlockingQueue")
                .setRejectedHandler("DiscardPolicy")
                .setKeeAliveTimeSeconds(30L)
                .setAllowCoreThreadTimeout(false);

        assertEquals("pool-2", props.getThreadPoolId());
        assertEquals(1, props.getCoolPoolSize());
        assertEquals(2, props.getMaximumPoolSize());
        assertEquals(10, props.getQueueCapacity());
        assertEquals("LinkedBlockingQueue", props.getWorkingQueue());
        assertEquals("DiscardPolicy", props.getRejectedHandler());
        assertEquals(30L, props.getKeeAliveTimeSeconds());
        assertFalse(props.getAllowCoreThreadTimeout());
    }

    @Test
    void testDefaultValuesInNestedClasses() {
        ThreadPoolExecutorProperties.NotifyConfig notifyConfig =
                new ThreadPoolExecutorProperties.NotifyConfig();
        ThreadPoolExecutorProperties.AlarmConfig alarmConfig =
                new ThreadPoolExecutorProperties.AlarmConfig();
        assertEquals(5L, notifyConfig.getIntervalMinutes());
        assertNull(notifyConfig.getSubscribers());

        assertTrue(alarmConfig.getEnable());
        assertEquals(80, alarmConfig.getQueueThreshold());
        assertEquals(80, alarmConfig.getActiveThreadThreshold());
    }

    @Test
    void testEqualsAndHashCode() {
        ThreadPoolExecutorProperties props1 = ThreadPoolExecutorProperties.builder()
                .threadPoolId("pool-x")
                .coolPoolSize(1)
                .build();

        ThreadPoolExecutorProperties props2 = ThreadPoolExecutorProperties.builder()
                .threadPoolId("pool-x")
                .coolPoolSize(1)
                .build();

        assertEquals(props1, props2);
        assertEquals(props1.hashCode(), props2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        ThreadPoolExecutorProperties props = ThreadPoolExecutorProperties.builder()
                .threadPoolId("pool-y")
                .build();

        assertNotNull(props.toString());
        assertTrue(props.toString().contains("pool-y"));
    }
}