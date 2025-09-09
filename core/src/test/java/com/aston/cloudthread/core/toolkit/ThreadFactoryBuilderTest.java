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

import org.junit.jupiter.api.Test;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadFactoryBuilderTest {
    @Test
    void testBuild_WithNamePrefix() {
        ThreadFactory factory = ThreadFactoryBuilder.builder()
                .namePrefix("test-")
                .build();
        Thread thread = factory.newThread(() -> {
        });
        assertTrue(thread.getName().startsWith("test-"));
    }

    @Test
    void testBuild_DaemonThread() {
        ThreadFactory factory = ThreadFactoryBuilder.builder()
                .daemon(true)
                .namePrefix("daemon-")
                .build();
        Thread thread = factory.newThread(() -> {
        });
        assertTrue(thread.isDaemon());
    }

    @Test
    void testBuild_WithPriority() {
        ThreadFactory factory = ThreadFactoryBuilder.builder()
                .priority(Thread.MAX_PRIORITY)
                .namePrefix("prio-")
                .build();
        Thread thread = factory.newThread(() -> {
        });
        assertEquals(Thread.MAX_PRIORITY, thread.getPriority());
    }

    @Test
    void testBuild_WithUncaughtExceptionHandler() {
        AtomicBoolean invoked = new AtomicBoolean(false);
        Thread.UncaughtExceptionHandler handler = (t, e) -> invoked.set(true);

        ThreadFactory factory = ThreadFactoryBuilder.builder()
                .uncaughtExceptionHandler(handler)
                .namePrefix("exh-")
                .build();

        Thread thread = factory.newThread(() -> {
            throw new RuntimeException();
        });
        assertEquals(handler, thread.getUncaughtExceptionHandler());
    }

    @Test
    void testBuild_WithBackingThreadFactory() {
        ThreadFactory backing = r -> new Thread(r) {{
            setName("backing");
        }};
        ThreadFactory factory = ThreadFactoryBuilder.builder()
                .threadFactory(backing)
                .namePrefix("custom-")
                .build();

        Thread thread = factory.newThread(() -> {
        });
        assertTrue(thread.getName().startsWith("custom-")); // still uses name prefix counting
    }

    @Test
    void testPriority_OutOfRange_Throws() {
        ThreadFactoryBuilder builder = ThreadFactoryBuilder.builder();
        assertThrows(IllegalArgumentException.class, () -> builder.priority(0));
        assertThrows(IllegalArgumentException.class, () -> builder.priority(11));
    }

    @Test
    void testNamePrefix_Empty_Throws() {
        ThreadFactoryBuilder builder = ThreadFactoryBuilder.builder()
                .namePrefix("");
        assertThrows(IllegalArgumentException.class, builder::build);
    }
}