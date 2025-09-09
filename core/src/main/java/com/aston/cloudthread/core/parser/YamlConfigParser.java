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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * YAML config files parser class.
 */
public class YamlConfigParser extends AbstractConfigParser {
    private static final String INDEX_PREFIX = "[";
    private static final String INDEX_SUFFIX = "]";
    private static final String PATH_SEPARATOR = ".";

    @Override
    public Map<Object, Object> doParse(String configuration) {
        return Optional.ofNullable(configuration)
                .filter(StrUtil::isNotEmpty)
                .map(this::parseYamlDocument)
                .map(this::normalizeHierarchy)
                .orElseGet(Collections::emptyMap);
    }

    @Override
    public List<ConfigFileTypeEnum> getConfigFileTypes() {
        return List.of(ConfigFileTypeEnum.YAML, ConfigFileTypeEnum.YML);
    }

    private Map<Object, Object> parseYamlDocument(String content) {
        Object root = new Yaml().load(content);

        Map<Object, Object> result = new LinkedHashMap<>();

        if (root instanceof Map) {
            result = normalizeHierarchy((Map<Object, Object>) root);
        } else if (root instanceof Iterable) {
            // Flatten root list
            processNestedElements(result, root, null);
        }

        return result;
    }

    private Map<Object, Object> normalizeHierarchy(Map<Object, Object> nestedData) {
        Map<Object, Object> flattenedData = new LinkedHashMap<>();
        processNestedElements(flattenedData, nestedData, null);
        return flattenedData;
    }

    private void processNestedElements(Map<Object, Object> target, Object current, String currentPath) {
        if (current instanceof Map) {
            handleMapEntries(target, (Map<?, ?>) current, currentPath);
        } else if (current instanceof Iterable) {
            handleCollectionItems(target, (Iterable<?>) current, currentPath);
        } else {
            persistLeafValue(target, currentPath, current);
        }
    }

    private void handleMapEntries(Map<Object, Object> target, Map<?, ?> entries, String parentPath) {
        entries.forEach((key, value) ->
                processNestedElements(target, value, buildPathSegment(parentPath, key))
        );
    }

    private void handleCollectionItems(Map<Object, Object> target, Iterable<?> items, String basePath) {
        List<?> elements = StreamSupport.stream(items.spliterator(), false)
                .collect(Collectors.toList());
        IntStream.range(0, elements.size())
                .forEach(index -> processNestedElements(
                        target,
                        elements.get(index),
                        createIndexedPath(basePath, index)
                ));
    }

    private String buildPathSegment(String existingPath, Object key) {
        return existingPath == null ?
                key.toString() :
                existingPath + PATH_SEPARATOR + key;
    }

    private String createIndexedPath(String basePath, int index) {
        if (basePath == null || basePath.isEmpty()) {
            return INDEX_PREFIX + index + INDEX_SUFFIX;
        }
        return basePath + INDEX_PREFIX + index + INDEX_SUFFIX;
    }

    private void persistLeafValue(Map<Object, Object> target, String path, Object value) {
        if (path != null) {
            String normalizedPath = path.replace(PATH_SEPARATOR + INDEX_PREFIX, INDEX_PREFIX);
            target.put(normalizedPath, value != null ? value.toString() : null);
        }
    }
}
