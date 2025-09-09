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
package com.aston.cloudthread.core.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigFileTypeEnumTest {

    @Test
    void testOfKnownValues() {
        assertEquals(ConfigFileTypeEnum.PROPERTIES, ConfigFileTypeEnum.of("properties"));
        assertEquals(ConfigFileTypeEnum.YML, ConfigFileTypeEnum.of("yml"));
        assertEquals(ConfigFileTypeEnum.YAML, ConfigFileTypeEnum.of("yaml"));
    }

    @Test
    void testOfUnknownValueDefaultsToProperties() {
        assertEquals(ConfigFileTypeEnum.PROPERTIES, ConfigFileTypeEnum.of("unknown"));
        assertEquals(ConfigFileTypeEnum.PROPERTIES, ConfigFileTypeEnum.of(""));
    }

    @Test
    void testOfNullDefaultsToProperties() {
        assertEquals(ConfigFileTypeEnum.PROPERTIES, ConfigFileTypeEnum.of(null));
    }
}