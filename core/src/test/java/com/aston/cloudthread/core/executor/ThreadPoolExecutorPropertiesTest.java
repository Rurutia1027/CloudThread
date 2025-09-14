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
                new ThreadPoolExecutorProperties.NotifyConfig("user1,user2", 10);
        ThreadPoolExecutorProperties.AlarmConfig alarmConfig =
                new ThreadPoolExecutorProperties.AlarmConfig(true, 90, 70);

        ThreadPoolExecutorProperties props = ThreadPoolExecutorProperties.builder()
                .threadPoolUID("pool-1")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .queueCapacity(100)
                .workingQueue("ArrayBlockingQueue")
                .rejectedHandler("AbortPolicy")
                .keeAliveTimeSeconds(60L)
                .allowCoreThreadTimeout(true)
                .notify(notifyConfig)
                .alarm(alarmConfig)
                .build();

        assertEquals("pool-1", props.getThreadPoolUID());
        assertEquals(2, props.getCorePoolSize());
        assertEquals(4, props.getMaximumPoolSize());
        assertEquals(100, props.getQueueCapacity());
        assertEquals("ArrayBlockingQueue", props.getWorkingQueue());
        assertEquals(60L, props.getKeeAliveTimeSeconds());
        assertTrue(props.getAllowCoreThreadTimeout());
        assertEquals(notifyConfig, props.getNotify());
        assertEquals(alarmConfig, props.getAlarm());
    }
    @Test
    void testChainedSetters() {
        ThreadPoolExecutorProperties props = new ThreadPoolExecutorProperties()
                .setThreadPoolUID("pool-2")
                .setCorePoolSize(1)
                .setMaximumPoolSize(2)
                .setQueueCapacity(10)
                .setWorkingQueue("LinkedBlockingQueue")
                .setRejectedHandler("DiscardPolicy")
                .setKeeAliveTimeSeconds(30L)
                .setAllowCoreThreadTimeout(false);

        assertEquals("pool-2", props.getThreadPoolUID());
        assertEquals(1, props.getCorePoolSize());
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
        assertEquals(5, notifyConfig.getIntervalMinutes());
        assertNull(notifyConfig.getSubscribers());

        assertTrue(alarmConfig.getEnable());
        assertEquals(80, alarmConfig.getQueueThreshold());
        assertEquals(80, alarmConfig.getActiveThreadThreshold());
    }

    @Test
    void testEqualsAndHashCode() {
        ThreadPoolExecutorProperties props1 = ThreadPoolExecutorProperties.builder()
                .threadPoolUID("pool-x")
                .corePoolSize(1)
                .build();

        ThreadPoolExecutorProperties props2 = ThreadPoolExecutorProperties.builder()
                .threadPoolUID("pool-x")
                .corePoolSize(1)
                .build();

        assertEquals(props1, props2);
        assertEquals(props1.hashCode(), props2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        ThreadPoolExecutorProperties props = ThreadPoolExecutorProperties.builder()
                .threadPoolUID("pool-y")
                .build();

        assertNotNull(props.toString());
        assertTrue(props.toString().contains("pool-y"));
    }
}