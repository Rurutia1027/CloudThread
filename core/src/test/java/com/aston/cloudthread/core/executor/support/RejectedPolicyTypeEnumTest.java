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
package com.aston.cloudthread.core.executor.support;

import org.junit.jupiter.api.Test;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RejectedPolicyTypeEnumTest {
    @Test
    void testCreatePolicy_CallerRunsPolicy() {
        RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy(
                "CallerRunsPolicy");
        assertNotNull(handler);
        assertTrue(handler instanceof ThreadPoolExecutor.CallerRunsPolicy);
    }

    @Test
    void testCreatePolicy_AbortPolicy() {
        RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy("AbortPolicy");
        assertNotNull(handler);
        assertTrue(handler instanceof ThreadPoolExecutor.AbortPolicy);
    }

    @Test
    void testCreatePolicy_DiscardPolicy() {
        RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy("DiscardPolicy");
        assertNotNull(handler);
        assertTrue(handler instanceof ThreadPoolExecutor.DiscardPolicy);
    }

    @Test
    void testCreatePolicy_DiscardOldestPolicy() {
        RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy("DiscardOldestPolicy");
        assertNotNull(handler);
        assertTrue(handler instanceof ThreadPoolExecutor.DiscardOldestPolicy);
    }

    @Test
    void testCreatePolicy_InvalidName() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                RejectedPolicyTypeEnum.createPolicy("NonExistentPolicy")
        );
        assertTrue(ex.getMessage().contains("No matching type of rejected execution was found"));
    }

    @Test
    void testEnumMappingConsistency() {
        for (RejectedPolicyTypeEnum policyEnum : RejectedPolicyTypeEnum.values()) {
            RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy(policyEnum.getName());
            assertSame(handler, policyEnum.getRejectedHandler(), "Handler should match the enum instance");
        }
    }
}