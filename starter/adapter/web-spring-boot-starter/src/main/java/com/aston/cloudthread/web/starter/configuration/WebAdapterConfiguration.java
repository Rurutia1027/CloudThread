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
package com.aston.cloudthread.web.starter.configuration;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * Auto-configuration for CloudThread Web Integration.
 *
 * <p>This class is part of the Web Starter and is responsible for initializing all
 * necessary beans and components to integrate CloudThread dynamic thread pools with
 * web servers such as Tomcat, Jetty, and Undertow.</p>
 *
 * <p>Key responsibilities include:</p>
 * <ul>
 *     <li>Providing thread pool wrapper beans for each supported web server</li>
 *     <li>Binding CloudThread dynamic thread pools to web server executor threads</li>
 *     <li>Ensuring proper lifecycle management of web server threads in conjunction with
 *     Spring Boot's auto-configuration.</li>
 *     <li>Supporting customization and monitoring of web thread pools in production.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <ul>
 *     <li>Simply include the `cloudthread-web-spring-boot-starter` dependency in a Spring
 *     Boot application.</li>
 *     <li>This configuration is automatically picked up by Spring Boot's
 *     auto-configuration mechanisms.</li>
 * </ul>
 */
@Configurable
public class WebAdapterConfiguration {

}