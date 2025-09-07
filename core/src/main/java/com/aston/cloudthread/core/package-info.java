/**
 * Core module of CloudThreadExecutor: cloud-native dynamic thread pool framework.
 *
 * <p>This package provides the essential building blocks for creating, monitoring,
 * and dynamically configuring Java thread pools in cloud-native environments
 * such as Kubernetes or AWS.
 *
 * <p>Main packages and responsibilities:
 *
 * <ul>
 *     <li><b>alarm</b> - Evaluate thread pool health and trigger alarms ({@link com.aston.cloudthread.core.alarm.ThreadPoolAlarmChecker}).</li>
 *     <li><b>config</b> - Application and bootstrap configuration classes ({@link com.aston.cloudthread.core.config.ApplicationProperties}).</li>
 *     <li><b>constant</b> - Global constants used across the framework ({@link com.aston.cloudthread.core.constant.Constants}).</li>
 *     <li><b>executor</b> - Core dynamic executor classes: {@link com.aston.cloudthread.core.executor.CloudThreadExecutor}, {@link com.aston.cloudthread.core.executor.CloudThreadRegistry}, {@link com.aston.cloudthread.core.executor.ThreadPoolExecutorHolder}, {@link com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties}.</li>
 *     <li><b>executor.support</b> - Enums and utilities for queue types and rejection policies.</li>
 *     <li><b>monitor</b> - Runtime metrics and monitoring ({@link com.aston.cloudthread.core.monitor.ThreadPoolMonitor}).</li>
 *     <li><b>notification</b> - Alerting and notification services and DTOs.</li>
 *     <li><b>parser</b> - Configuration parsers for YAML, properties, and other formats.</li>
 *     <li><b>toolkit</b> - Builders for thread factories and thread pool executors.</li>
 * </ul>
 *
 * <p>Developers extending or using CloudThreadExecutor should primarily interact
 * with classes in <code>executor</code>, <code>monitor</code>, <code>notification</code>,
 * and <code>parser</code>.
 */
package com.aston.cloudthread.core;