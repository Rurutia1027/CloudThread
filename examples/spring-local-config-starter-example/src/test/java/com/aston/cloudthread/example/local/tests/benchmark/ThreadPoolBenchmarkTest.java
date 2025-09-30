package com.aston.cloudthread.example.local.tests.benchmark;

import com.aston.cloudthread.core.executor.support.BlockingQueueTypeEnum;
import com.aston.cloudthread.core.toolkit.ThreadPoolExecutorBuilder;
import com.aston.cloudthread.example.local.LocalCloudThreadTestApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
@SpringBootTest(
        classes = LocalCloudThreadTestApp.class,
        properties = {
                "spring.config.import=optional:classpath:/application.yaml",
                "spring.profiles.active=test"
        }
)
public class ThreadPoolBenchmarkTest {

    @Autowired
    @Qualifier("withDynamicAnnotation")
    private ThreadPoolExecutor dynamicPool;

    private ThreadFactory threadFactory;

    @BeforeEach
    void setup() {
        threadFactory = Executors.defaultThreadFactory();
    }


    private void runBenchmark(String label, ThreadPoolExecutor executor) throws InterruptedException {
        long start = System.nanoTime();

        IntStream.range(0, 10_000).forEach(i ->
                executor.submit(() -> Math.log(Math.random() * 1000))
        );

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        long elapsed = (System.nanoTime() - start) / 1_000_000;
        System.out.println(label + " benchmark took: " + elapsed + " ms");
    }

    @Test
    void benchmarkStaticVsDynamic() throws Exception {
        // Static (code-defined, baseline)
        ThreadPoolExecutor staticPool = ThreadPoolExecutorBuilder.builder()
                .threadPoolUID("benchmark-static")
                .corePoolSize(9)
                .maximumPoolSize(50)
                .keepAliveTimeSeconds(60)
                .workQueueType(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE)
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .threadFactory(threadFactory)
                .workQueueCapacity(2000)
                .build();

        runBenchmark("Static benchmark", staticPool);

        // Dynamic (loaded from application.yaml)
        runBenchmark("Dynamic benchmark (yaml)", dynamicPool);
    }
}