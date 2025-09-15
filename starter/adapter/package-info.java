package com.aston.cloudthread.adapter;

/**
 * Adapter package for CloudThread integration with external middleware that heavily rely on
 * Java thread pools (e.g., DB drivers, gRPC Netty, Kafka consumers, HTTP clients).
 *
 * <p>
 * Purpose
 * <ul>
 *     <li>Provide adapters that can <b>intercept or wrap native thread pool usage</b> in
 *     third party libraries (for expose alternate executors) so thread pools become
 *     manageable by CloudThread.</li>
 *     <li>Expose unified <b>monitoring hooks</b> and metrics (through Prometheus /
 *     Micrometer) for middleware-related thread usage: active threads, queue size,
 *     rejection counts, latency.</li>
 *     <li>Enable <b>runtime control</b> of pool parameters (core/max, keepAlive, queue
 *     capacity, rejection policy) through the CloudThread registry and dynamic refresh
 *     mechanisms.</li>
 *     <li>Offer middleware-specific safety guards and anti-overload strategies
 *     (backpressure, shed, circuit-breaker, pause/resume consumers) to avoid cascade
 *     failure when thread pools become saturated. </li>
 * </ul>
 *
 * <p>
 * Responsibilities
 * <ul>
 * <li>Provide adapter implementations for specific middleware {@code grpc}, {@code kafka}
 * , {@code db} etc.</li>
 * <li>Offer a pluggable way to replace or wrap executor creation (e.g., factory hooks,
 * instrumentation, or library-specific hooks) so existing code can use
 * CloudThread-managed executors.</li>
 * <li>Publish event and metrics to the CloudThread control plane so operational tools
 * (Grafana Alerting) and automated policies can acton runtime state. </li>
 * </ul>
 *
 * <p>
 * Design notes and recommendation
 * <ul>
 *     <li>Keep adapters small and focused per middleware. Each adapter should document
 *     where it hooks into the target library (e.g., Netty event loop vs. business
 *     executor for gRPC).</li>
 *     <li>Prefer non-invasive integration: provide factory/bean overrides for
 *     Spring-managed libraries, and use instrumentation (wrappers/interceptor) where
 *     factory overrides are impossible.</li>
 *     <li>Expose adapter configuraiton via Spring properties so users can enable/disable
 *     starters and set safe defaults.</li>
 * </ul>
 * <p>
 * Example packages under this package
 * <pre>
 *     com.aston.cloudthread.web.starter;
 *     com.aston.cloudthread.grpc.starter;
 *     com.aston.cloudthread.db.starter;
 *     com.aston.cloudthread.http.starter;
 * </pre>
 *
 * @see com.aston.cloudthread.core.executor.CloudThreadRegistry
 * @since 0.0.1
 */