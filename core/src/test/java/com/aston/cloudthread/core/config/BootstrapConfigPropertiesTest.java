package com.aston.cloudthread.core.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BootstrapConfigPropertiesTest {
    private BootstrapConfigProperties properties;

    @BeforeEach
    void setup() {
        properties = new BootstrapConfigProperties();
    }

    @Test
    void testDefaultEnableValue() {
        assertTrue(properties.getEnable(), "Default enable value should be true");
    }


    @Test
    void testSpringCloudConfigSetAndGet() {
        BootstrapConfigProperties.SpringCloudConfig springCloud = new BootstrapConfigProperties.SpringCloudConfig();
        springCloud.setApplicationName("cloudthread-app");
        springCloud.setProfile("dev");
        springCloud.setLabel("main");

        properties.setSpringCloudConfig(springCloud);

        assertEquals("cloudthread-app", properties.getSpringCloudConfig().getApplicationName());
        assertEquals("dev", properties.getSpringCloudConfig().getProfile());
        assertEquals("main", properties.getSpringCloudConfig().getLabel());
    }

    @Test
    void testConsulConfigSetAndGet() {
        BootstrapConfigProperties.ConsulConfig consul = new BootstrapConfigProperties.ConsulConfig();
        consul.setServiceName("cloudthread-service");
        consul.setDatacenter("dc1");
        consul.setKeyPrefix("config/");

        properties.setConsulConfig(consul);

        assertEquals("cloudthread-service", properties.getConsulConfig().getServiceName());
        assertEquals("dc1", properties.getConsulConfig().getDatacenter());
        assertEquals("config/", properties.getConsulConfig().getKeyPrefix());
    }

    @Test
    void testWebThreadPoolExecutorConfigSetAndGet() {
        BootstrapConfigProperties.WebThreadPoolExecutorConfig webConfig = new BootstrapConfigProperties.WebThreadPoolExecutorConfig();
        webConfig.setCorePoolSize(5);
        webConfig.setMaximumPoolSize(10);
        webConfig.setKeepAliveTimeSeconds(300L);

        BootstrapConfigProperties.NotifyConfig notify = new BootstrapConfigProperties.NotifyConfig("user1,user2", 10L);
        webConfig.setNotifyConfig(notify);

        properties.setWebConfig(webConfig);

        assertEquals(5, properties.getWebConfig().getCorePoolSize());
        assertEquals(10, properties.getWebConfig().getMaximumPoolSize());
        assertEquals(300L, properties.getWebConfig().getKeepAliveTimeSeconds());
        assertEquals("user1,user2", properties.getWebConfig().getNotifyConfig().getSubscribers());
        assertEquals(10L, properties.getWebConfig().getNotifyConfig().getIntervals());
    }

    @Test
    void testMonitorConfigDefaultValues() {
        BootstrapConfigProperties.MonitorConfig monitor = properties.getMonitorConfig();
        assertNotNull(monitor, "Monitor config should not be null");
        assertTrue(monitor.getEnable(), "Monitor should be enabled by default");
        assertEquals("micrometer", monitor.getCollectType());
        assertEquals(10L, monitor.getCollectIntervalSeconds());
    }

    @Test
    void testStaticInstanceGetSet() {
        BootstrapConfigProperties.setInstance(properties);
        BootstrapConfigProperties instance = BootstrapConfigProperties.getInstance();
        assertNotNull(instance, "Static instance should not be null");
        assertEquals(properties, instance, "Static instance should return the object set");
    }
}