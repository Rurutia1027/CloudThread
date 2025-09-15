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
package com.aston.cloudthread.web.starter.core.executor;

import com.aston.cloudthread.spring.base.support.ApplicationContextHolder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executor;

/**
 * Abstract base class for binding CloudThread with web servlet container thread pools.
 *
 * <p>This class provides a unified abstraction to access and manage the underlying
 * thread pools of different web servers (e.g., Tomcat, Jetty, Undertow).
 * Subclasses should implement container-specific logic for fetching and
 * manipulating their respective thread pool executors.</p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Hold a reference to the underlying web container’s thread pool executor.</li>
 *   <li>Provide an abstract hook to retrieve the container’s internal executor.</li>
 *   <li>Integrate with Spring’s {@link ApplicationRunner} to initialize the executor
 *       reference during application startup.</li>
 *   <li>Offer default runtime context status reporting, with extension points
 *       for container-specific implementations.</li>
 *   <li>Resolve the active {@link WebServer} instance from Spring’s application context
 *       using {@link ApplicationContextHolder}, a lightweight wrapper that implements
 *       {@code ApplicationContextAware} for global access.</li>
 * </ul>
 *
 * <p>Usage: Extend this class for each supported servlet container, implement
 * {@link #getWebServerInnerExecutor(WebServer)}, and expose monitoring/dynamic
 * reconfiguration capabilities for its thread pool.</p>
 */
public abstract class AbstractWebThreadPoolService implements WebThreadPoolService, ApplicationRunner {
    /**
     * Reference to the underlying thread pool executor of the web container.
     *
     * <p>This is populated at runtime by fetching the container's executor through
     * {@link #getWebServerInnerExecutor(WebServer)} inside the
     * {@link #run(ApplicationArguments)} lifecycle callback.</p>
     */
    protected Executor webExecutorRef;

    /**
     * Retrieve the underlying web server container’s thread pool executor.
     *
     * <p>Subclasses must implement container-specific logic here, since the
     * way to obtain the executor differs between Tomcat, Jetty, Undertow, etc.</p>
     *
     * @param webServer the active web server instance
     * @return the underlying {@link Executor} used by the web server
     */
    protected abstract Executor getWebServerInnerExecutor(WebServer webServer);

    @Override
    public String getRuntimeContextStatus() {
        return "Running context status";
    }

    /**
     * Resolve the active web server instance from the Spring application context.
     *
     * <p>Uses {@link ApplicationContextHolder} to fetch the global Spring context,
     * which is then cast to {@link WebServerApplicationContext}. The web server
     * instance is stored internally as a member of this context and can be retrieved
     * directly via {@link WebServerApplicationContext#getWebServer()}.</p>
     *
     * @return the current {@link WebServer} instance (e.g., TomcatWebServer, JettyWebServer)
     */
    public WebServer getWebServer() {
        ApplicationContext applicationContext = ApplicationContextHolder.getInstance();
        return ((WebServerApplicationContext) applicationContext).getWebServer();
    }

    @Override
    public void run(ApplicationArguments args) {
        // At startup, resolve the active web server instance and fetch its thread pool executor.
        // Delegates to the abstract method so subclasses can implement container-specific logic.
        Executor webServerInnerExecutor = getWebServerInnerExecutor(getWebServer());
        this.webExecutorRef = webServerInnerExecutor;
    }
}
