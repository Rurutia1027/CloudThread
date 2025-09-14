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
package com.aston.cloudthread.config.common.starter.refresher;

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.parser.ConfigParserHandler;
import com.aston.cloudthread.spring.base.support.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;


/**
 * Abstract base class for CloudThread thread pool configuration refreshers.
 *
 * <p>
 * This class provides a common template for automatically initializing and injecting
 * CloudThread-related configurations, including thread pool properties, bootstrap
 * configuration, and notification settings. Subclasses should implement the specific
 * logic for registering listeners to handle dynamic configuration changes.
 * </p>
 *
 * <p>The lifecycle flow is:</p>
 * <ol>
 *     <li>{@link #beforeRegister()} -- optional pre-registration hook</li>
 *     <li>{@link #registerListener()} -- mandatory listener registration logic</li>
 *     <li>{@ink #afterRegister()} -- optional post-registration hook</li>
 * </ol>
 * <p>It also provide a utility method {@link #refreshThreadPoolProperties(String)}
 * to parse configuration updates and publish {@link CloudThreadPoolConfigUpdateEvent}
 * to all registered listeners.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCloudThreadPoolRefresher implements ApplicationRunner {
    /**
     * Autoconfigured CloudThread bootstrap properties, shared across all subclasses.
     * Includes thread pool definitions, configuration options, and notification settings.
     */
    protected final BootstrapConfigProperties props;

    /**
     * Register listeners to handle CloudThread configuration changes.
     * Subclasses must implement this method to provide the actual listener registration
     * logic.
     */
    protected abstract void registerListener() throws Exception;

    /**
     * Optional hook executed before listener registration.
     * Subclasses can override to perform pre-registration logic.
     */
    protected void beforeRegister() {
    }

    /**
     * Optional hook executed after listener registration.
     * Subclasses can override to perform post-registration logic.
     */
    protected void afterRegister() {

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        beforeRegister();
        registerListener();
        afterRegister();
    }

    /**
     * Parses the given configuration string and publishes a {@link CloudThreadPoolConfigUpdateEvent}.
     *
     * <p>The published event triggers all registered listeners to compare and refresh
     * thread pool properties. Current supported listeners include:
     * <ul>
     *
     *     <li>
     *         TODO: not impl yet
     *         {@link com.aston.cloudthread.config.common.starter.refresher.DynamicThreadPoolRefreshListener}
     *     </li>
     *     <li>
     *         TODO: not impl yet
     *         {@link com.aston.cloudthread.web.starter.core.WebThreadPoolRefreshListener}
     *     </li>
     * </ul>
     *
     * @param configInfo Raw configuration string (e.g., YAML or properties format)
     */
    @SneakyThrows
    public void refreshThreadPoolProperties(String configInfo) {
        Map<Object, Object> configInfoMap =
                ConfigParserHandler.getInstance().parseConfig(configInfo,
                        props.getConfigFileType());
        ConfigurationPropertySource sources =
                new MapConfigurationPropertySource(configInfoMap);
        Binder binder = new Binder(sources);
        BootstrapConfigProperties refresherProperties =
                binder.bind(BootstrapConfigProperties.PREFIX, Bindable.ofInstance(props)).get();

        // Publish event to notify all CloudThread listeners of configuration changes
        ApplicationContextHolder.publishEvent(new CloudThreadPoolConfigUpdateEvent(this, refresherProperties));
    }
}
