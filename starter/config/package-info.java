package com.aston.cloudthread.config;

/**
 * Configuration Center integration package for CloudThread.
 *
 * <p>
 * Purpose
 * <ul>
 * <li>Provide adapters and connectors to multiple configuraiton sources (e.g., Consul,
 *     Spring Cloud Config Server, HashiCorp Vault, and Kubernetes ConfigMaps/Secrets). </li>
 * <li>Normalize configuration payloads into a CloudThread bootstrap model (thread pool
 * definitions, notification/subscriber lists, validation rules, etc.).</li>
 * <li>Offer pluggable listeners that trigger {@link CloudThreadPoolConfigUpdateEvent} and
 * safe refresh logic so thread pool adjustment are atomic and validated.
 * </li>
 * </ul>
 *
 * <p>
 * Responsibilities
 * <ul>
 *     <li>Implement a small set of connectors for popular config backends. Each connector
 *     should:
 *     <ul>
 *         <li>Support secure authentication (token/credentials) if required (e.g., Vault)</li>
 *         <li>Support subscription/push or polling-based refresh mechanism.</li>
 *         <li>Provide local caching and sanity checks before triggering live changes. </li>
 *     </ul>
 *     </li>
 *     <li>
 *         Provide a configuration normalization layer that converts backend-specific
 *         formats into the internal {@code BootstrapConfigProperties} model used by
 *         CloudThread
 *     </li>
 *     <li>Provide rate-limiting, validation, and rollback strategies when applying
 *     runtime changes.</li>
 * </ul>
 *
 * <p>
 *     Integration / Fesibility notes (summary):
 * <ul>
 *     <li>Most config centers (Apollo, Nacos, Spring Cloud Config) can push or let
 *     clients poll for changes. Use push/webhook or long-pool when available for
 *     low-latency updates; otherwise fall back to a safe poll interval with change
 *     detection.</li>
 *     <li>Consul and Vault are feasible; Vault should be used primarily for secrets and
 *     short-lived tokens, not bulk config changes.</li>
 *     <li>Kubernetes ConfigMaps/Secrets can be watch (via the API) or integrated via
 *     Spring Cloud Kubernetes.
 *     Watching k8s objects is feasible and often recommended for K8S-native deployments.</li>
 *     <li>Because there are many sources, design connectors as independent modules so
 *     projects can include only the ones they need.
 *     </li>
 * </ul>
 *
 * <p>
 *     Safety and operational best practices
 * <ul>
 *     <li>Always validate remote config against a schema before applying changes to
 *     running thread pools.</li>
 *     <li>Implement rate limits and minimum intervals between successive runtime updates
 *     to avoid flapping.</li>
 *     <li>Log all config changes and provide a simple rollback mechanism to
 *     last-known-good config.</li>
 * </ul>
 *
 * <p>
 * Example modules under this package
 * <pre>
 *     com.aston.cloudthread.aws; --> skip this one
 *     com.aston.cloudthread.k8s;
 *        --> in progress, k8s & consul works togeter provide cloud native
 *        --> microservice cloud thread dynamic config refresh
 *     com.aston.cloudthread.consul; --> in progress
 *     com.aston.cloudthread.vault; --> this not suit will not be impl this one
 *     com.aston.cloudthread.springcloudconfig; --> done
 * </pre>
 *
 * <p>Each sub module provides:
 * <ul>
 *     <li>Connector implementation to fetch & normalize configuration</li>
 *     <li>Event publishing to trigger thread-pool refresh</li>
 *     <li>Optinal runtime validation and safety features</li>
 * </ul>
 */