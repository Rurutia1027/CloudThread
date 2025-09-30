package com.aston.cloudthread.example.local.tests.benchmark;

public enum BenchmarkGroup {
    GROUP1(5, 10, 100, 30),
    GROUP2(10, 20, 500, 60),
    GROUP3(20, 40, 1000, 120),
    GROUP4(50, 100, 2000, 300),
    GROUP5(100, 200, 5000, 600);

    public final int corePoolSize;
    public final int maxPoolSize;
    public final int queueCapacity;
    public final long keepAliveSeconds;

    BenchmarkGroup(int corePoolSize, int maxPoolSize, int queueCapacity, long keepAliveSeconds) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.queueCapacity = queueCapacity;
        this.keepAliveSeconds = keepAliveSeconds;
    }
}