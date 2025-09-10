package com.aston.cloudthread.spring.base.enable;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MarkerConfiguration
 *
 * <p>
 * This configuration class defines a "marker" bean for use in the Spring context.
 * The presence of this marker bean can be leveraged as a conditional check to
 * determine whether the dynamic thread pool feature should be enabled.
 *
 * <p>Usage:
 *     <ul>
 *         <li>Inject this marker into other beans or use it with Spring's conditional
 *         annotations such as {@code @ConditionalOnBean(MarkerConfiguration.Marker.class)}.</li>
 *         <li>Provides a centralized way to indicate the activation of dynamic thread
 *         pool support. </li>
 *     </ul>
 *     </p>
 * </p>
 */

@Configuration
public class MarkerConfiguration {
    @Bean
    public Marker dynamicThreadPoolMarkerBean() {
        return new Marker();
    }

    public class Marker {
    }
}
