# CloudThread | [![Java CI](https://github.com/Rurutia1027/cloudthread/actions/workflows/ci-pipeline.yaml/badge.svg)](https://github.com/Rurutia1027/cloudthread/actions/workflows/ci-pipeline.yaml)

_Cloud-Native, Dynamic, and Observable Java Thread Pools_

CloudThread is a cloud-native, dynamic, and observable Java thread pool framework, built for Kubernetes, microservices,
and modern DevOps workflows.

Unlike traditional Java thread pools, it supports **real-time reconfiguration**, **observability**, and **adaptive
scaling**, allowing high-concurrency distributed systems to adjust automatically to changing workloads.

## Production Pain Points & CloudThread Approach

### Pooling & Resource Management

- **Pain Point**: Frequent thread creation/destruction, high overhead
- **Approach**: Maintain thread pool with lifecycle management & task-worker decoupling
- **Benefit**: Lower CPU/memory overhead, predictable resource utilization

### Dynamic Parameterization

- **Pain Point**: Static thread pool leads to `RejectedExecutionException` or task backlog
- **Approach**: Expose `corePoolSize`, `maxPoolSize`, `queueCapacity` via config center, runtime adjustment
- **Benefit**: Reduce MTTR, faster recovery from overload

### Task Observability

- **Pain Point**: No visibility into task execution or backlog
- **Approach**: Multi-level monitoring: pool-level, thread-level, task-level transactions
- **Benefit**: Proactive alerting, better SLA compliance

### Cloud Native Ready

- **Pain Point**: Hard to integrate with containerized microservices.
- **Approach**:
  - Out-of-box support for **Java standalone applications**.
  - **Spring Boot auto-configuration** && annotation-driven enablement.
  - Native integration with **Prometheus & Grafana** for monitoring/alerting.
  - **Kubernetes-friendly design**: ConfigMap/Secret for dynamic configuration, Horizontal Pod Autoscaler (HPA)
    integration for scaling.
  - Cloud platform readiness: easily pluggable with **AWS Parameter Store / AppConfig**, **GCP Config Controller**, *
    *Azure App Configuration**.
- **Benefit**: Seamless adoption in microservices, strong cloud native affinity across Kubernetes and major cloud
  providers, no boilerplate code.

## Key Features

- **Dynamic Configuration**: Update pool size, queue capacity, and rejection policies at runtime.
- **Observability**: Metrics exported to Prometheus; pre-built dashboards for Grafana.
- **Cloud-Native Ready**: Optimized for Kubernetes and multi-pod deployments.
- **Service Mesh Friendly**: Compatible with Envoy/Istio for traffic-aware thread management.
- **Spring Starter + Annotations**: Easily enable with `@EnableDynamicThread` and configuration properties.

## Supported Configuration Backends

### Spring Cloud Config + GitOps

Centralized Git repository as configuration source. Version-controlled, auditable changes with auto-refresh of pool
parameters.

### Kubernetes ConfigMap + Operator

Store pool parameters in ConfigMap. Watch for updates and reload without restarting applications.

### etcd + Kubernetes CRD

Define `ThreadPoolConfig` CRD in Kubernetes. Operator watches CRDs and updates thread pools dynamically.

### Consul + Envoy/Istio

Thread pool settings stored in Consul KV. Automatic refresh on KV changes and integration refresh for service mesh-aware
routing.

## Observability

- **Pre-configured Prometheus metrics for:**
    - Pool size, active threads, queue length;
    - Rejection count, task completion rate;

- **Ready-to-use Grafana dashboards include with the starter library.**
- **Full support for pre-thread metrics across multi-pod deployments.**

## Example Usage

```java

@SpringBootApplication
@EnableDynamicThread
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

    @Bean
    public Executor myExecutor(DynamicThreadPoolRegistry registry) {
        return registry.createThreadPool("my-service-executor");
    }
}
```

- Each executor can be configured via `application.yaml` or a central config center.
- Metrics are automatically exported to Prometheus and visualized in Grafana dashboards.
- Annotation-based and starter-enabled: minimal boilerplate wiring required.

## Benefits in Cloud-Native Environments

- **Reduced latency**: Thread pools auto-adjust to workload.
- **Optimized resource usage**: Avoids over-provisioning.
- **Zero-downtime updates**: Runtime reconfiguration without restarts.
- **Multi-pod consistency**: Centralized configuration ensures uniform behavior.
- **Actionable insights**: Pre-built dashboards and metrics for DevOps teams.

## Roadmap

- Kubernetes Operator support.
- Prometheus/Grafana integration.
- Spring Starter + annotation auto-configuration.
- Adaptive autoscaling policies per thread pool.
- Service Mesh telemetry integration with Istio. 
