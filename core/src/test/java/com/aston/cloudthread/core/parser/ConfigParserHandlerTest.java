package com.aston.cloudthread.core.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigParserHandlerTest {

    private ConfigParserHandler handler;

    @BeforeEach
    void setUp() {
        handler = ConfigParserHandler.getInstance();
    }

    @Test
    void testSingletonInstance() {
        ConfigParserHandler another = ConfigParserHandler.getInstance();
        assertSame(handler, another, "ConfigParserHandler should be a singleton");
    }

    @Test
    void testParseYamlConfig() throws IOException {
        String yamlContent = "key1: value1\nkey2: value2";
        Map<Object, Object> result = handler.parseConfig(yamlContent, ConfigFileTypeEnum.YAML);

        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void testParseYmlConfig() throws IOException {
        String ymlContent = "app:\n  name: CloudThread\n  version: 1.0";
        Map<Object, Object> result = handler.parseConfig(ymlContent, ConfigFileTypeEnum.YML);

        assertEquals(2, result.size());
        assertEquals("CloudThread", result.get("app.name"));
        assertEquals("1.0", result.get("app.version"));
    }

    @Test
    void testParsePropertiesConfig() throws IOException {
        String propertiesContent = "thread.pool.size=10\nthread.pool.name=worker";
        Map<Object, Object> result = handler.parseConfig(propertiesContent, ConfigFileTypeEnum.PROPERTIES);

        assertEquals(2, result.size());
        assertEquals("10", result.get("thread.pool.size"));
        assertEquals("worker", result.get("thread.pool.name"));
    }

    @Test
    void testParseUnsupportedTypeReturnsEmpty() throws IOException {
        String content = "some random content";
        Map<Object, Object> result = handler.parseConfig(content, null); // unsupported type
        assertTrue(result.isEmpty(), "Unsupported config type should return empty map");
    }

    @Test
    void testParseEmptyContentReturnsEmpty() throws IOException {
        Map<Object, Object> result = handler.parseConfig("", ConfigFileTypeEnum.YAML);
        assertTrue(result.isEmpty(), "Empty content should return empty map");

        result = handler.parseConfig(null, ConfigFileTypeEnum.PROPERTIES);
        assertTrue(result.isEmpty(), "Null content should return empty map");
    }
}