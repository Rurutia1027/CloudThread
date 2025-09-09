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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PropsConfigParserTest {
    private final PropsConfigParser parser = new PropsConfigParser();

    @Test
    void testDoParseValidProperties() throws IOException {
        String content = "key1=value1\nkey2=value2";
        Map<Object, Object> result = parser.doParse(content);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void testDoParseEmptyProperties() throws IOException {
        String content = "";
        Map<Object, Object> result = parser.doParse(content);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDoParseInvalidProperties() {
        String content = "invalid content \u0000 "; // contains an invalid character
        assertThrows(IOException.class, () -> parser.doParse(content));
    }

    @Test
    void testGetConfigFileTypes() {
        List<ConfigFileTypeEnum> types = parser.getConfigFileTypes();

        assertNotNull(types);
        assertEquals(1, types.size());
        assertEquals(ConfigFileTypeEnum.PROPERTIES, types.get(0));
    }
}