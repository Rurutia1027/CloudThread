package com.aston.cloudthread.example.test;

import com.aston.cloudthread.core.config.BootstrapConfigProperties;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to validate the initialization of thread pool properties.
 *
 * <p>
 * All configuration values for the thread pools (cloudthread-producer and
 * cloudthread-consumer)
 * are loaded from the `application.yaml` file in the example module under the
 * `cloudthread.executors` section.
 * This ensures that BootstrapConfigProperties correctly binds YAML configuration into
 * Java objects.
 * </p>
 */
@Slf4j
@Component
public class BootstrapConfigExecutorParamsTest {
    // BootstrapConfigProperties instance already initialized via the AutoConfig
    // Here we just import the already initialized instance to current context to test inner
    // parameters match with the parameters defined in the application.yaml
    @Autowired
    private BootstrapConfigProperties bootstrapConfigProperties;

    @PostConstruct
    public void testBootstrapConfigPropInitOK() {
        log.info("boot strap config instance cannot be null {}",
                (bootstrapConfigProperties != null));
    }

    @PostConstruct
    public void testBootStrapConfigPropInnerParamsInitOK() {
        assertNotNull(bootstrapConfigProperties, "BootstrapConfigProperties should not be " +
                "null!");
        log.info("Bootstrap config instance loaded: {}", bootstrapConfigProperties != null);

        List<ThreadPoolExecutorProperties> executors =
                bootstrapConfigProperties.getExecutors();
        assertNotNull(executors, "Executor properties list should not be null");
        assertEquals(2, executors.size());

        // Validate cloudthread-producer thread pool inner parameters
        Optional<ThreadPoolExecutorProperties> producerOpt = executors.stream()
                .filter(e -> "cloudthread-producer".equals(e.getThreadPoolUID()))
                .findFirst();
        assertTrue(producerOpt.isPresent(), "cloudthread-producer should exist");
        ThreadPoolExecutorProperties producer = producerOpt.get();
        assertEquals(5, producer.getCorePoolSize(), "Producer corePoolSize mismatch");
        assertEquals(10, producer.getMaximumPoolSize(), "Producer maximumPoolSize mismatch");
    }
}
