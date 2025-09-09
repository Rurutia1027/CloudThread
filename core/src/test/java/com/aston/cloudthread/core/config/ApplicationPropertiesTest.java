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
package com.aston.cloudthread.core.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ApplicationPropertiesTest {
    @AfterEach
    void tearDown() {
        // Reset static fields after each test to avoid test pollution
        ApplicationProperties.setApplicationName(null);
        ApplicationProperties.setActiveProfile(null);
    }

    @Test
    void testSetAndGetApplicationName() {
        String appName = "cloudThreadApp";
        ApplicationProperties.setApplicationName(appName);
        assertThat(ApplicationProperties.getApplicationName()).isEqualTo(appName);
    }

    @Test
    void testSetAndGetActiveProfile() {
        String profile = "dev";
        ApplicationProperties.setActiveProfile(profile);
        assertThat(ApplicationProperties.getActiveProfile()).isEqualTo(profile);
    }

    @Test
    void testDefaultValuesAreNull() {
        assertThat(ApplicationProperties.getApplicationName()).isNull();
        assertThat(ApplicationProperties.getActiveProfile()).isNull();
    }

    @Test
    void testOverwriteApplicationName() {
        ApplicationProperties.setApplicationName("oldName");
        ApplicationProperties.setApplicationName("newName");
        assertThat(ApplicationProperties.getApplicationName()).isEqualTo("newName");
    }

    @Test
    void testOverwriteActiveProfile() {
        ApplicationProperties.setActiveProfile("test");
        ApplicationProperties.setActiveProfile("prod");
        assertThat(ApplicationProperties.getActiveProfile()).isEqualTo("prod");
    }
}