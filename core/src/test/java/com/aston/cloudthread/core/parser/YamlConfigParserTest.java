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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlConfigParserTest {
    private final YamlConfigParser parser = new YamlConfigParser();

    @Test
    void testDoParseWithSimpleYaml() {
        String yaml = "key1: value1\nkey2: value2";
        Map<Object, Object> result = parser.doParse(yaml);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void testDoParseWithNestedYaml() {
        String yaml = """
                parent:
                  child1: val1
                  child2: val2
                """;
        Map<Object, Object> result = parser.doParse(yaml);

        assertEquals(2, result.size());
        assertEquals("val1", result.get("parent.child1"));
        assertEquals("val2", result.get("parent.child2"));
    }

    @Test
    void testDoParseWithListYaml() {
        String yaml = """
                servers:
                  - host: localhost
                    port: 8080
                  - host: remote
                    port: 9090
                """;
        Map<Object, Object> result = parser.doParse(yaml);

        assertEquals(4, result.size());
        assertEquals("localhost", result.get("servers[0].host"));
        assertEquals("8080", result.get("servers[0].port"));
        assertEquals("remote", result.get("servers[1].host"));
        assertEquals("9090", result.get("servers[1].port"));
    }

    @Test
    void testDoParseWithEmptyYaml() {
        Map<Object, Object> result = parser.doParse("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDoParseWithNullYaml() {
        Map<Object, Object> result = parser.doParse(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDoParseWithNonMapRootYaml() {
        String yaml = "- item1\n- item2"; // list as root
        Map<Object, Object> result = parser.doParse(yaml);

        assertEquals(2, result.size());
        assertEquals("item1", result.get("[0]"));
        assertEquals("item2", result.get("[1]"));
    }

    @Test
    void testGetConfigFileTypes() {
        List<ConfigFileTypeEnum> types = parser.getConfigFileTypes();

        assertNotNull(types);
        assertEquals(2, types.size());
        assertTrue(types.contains(ConfigFileTypeEnum.YAML));
        assertTrue(types.contains(ConfigFileTypeEnum.YML));
    }

    @Test
    void testDoParseWithComplexNestedYaml() {
        String yaml = """
                app:
                  servers:
                    - host: localhost
                      ports:
                        - 8080
                        - 8081
                    - host: remote
                      ports:
                        - 9090
                """;
        Map<Object, Object> result = parser.doParse(yaml);

        assertEquals("localhost", result.get("app.servers[0].host"));
        assertEquals("8080", result.get("app.servers[0].ports[0]"));
        assertEquals("8081", result.get("app.servers[0].ports[1]"));
        assertEquals("remote", result.get("app.servers[1].host"));
        assertEquals("9090", result.get("app.servers[1].ports[0]"));
    }
}