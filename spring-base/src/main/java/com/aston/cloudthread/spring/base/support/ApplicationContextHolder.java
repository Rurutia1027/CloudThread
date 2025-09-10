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
package com.aston.cloudthread.spring.base.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * ApplicationContextHolder provides a static accessor to the Spring ApplicationContext,
 * allowing retrieval of Beans in non-Spring-managed environments.
 */
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.CONTEXT = applicationContext;
    }

    /**
     * Retrieve a Bean by its type from the Spring context
     *
     * @param clazz Bean type
     * @param <T>   Generic type
     * @return Bean instance
     */
    public static <T> T getBean(Class<T> clazz) {
        return CONTEXT.getBean(clazz);
    }

    /**
     * Retrieve a Bean by its name and type from the Spring context
     *
     * @param name  Bean name
     * @param clazz Bean type
     * @param <T>   Generic type
     * @return Bean instance
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return CONTEXT.getBean(name, clazz);
    }

    /**
     * Retrieve all Beans of the given type from the context.
     *
     * @param clazz Bean type
     * @param <T>   Generic type
     * @return Map of bean name to instance
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return CONTEXT.getBeansOfType(clazz);
    }

    /**
     * Find a specific annotation on a given Bean
     *
     * @param beanName Bean name
     * @param annotationType Annotation type
     * @param <A> Annotation generic
     *
     * @return Annotation instance, or null if not present
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        return CONTEXT.findAnnotationOnBean(beanName, annotationType);
    }

    /**
     * Publish a Spring event
     *
     * @param event Spring event
     */
    public static void publishEvent(ApplicationEvent event) {
        CONTEXT.publishEvent(event);
    }

    /**
     * Get the current ApplicationContext instance
     *
     * @return Spring application context
     */
    public static ApplicationContext getInstance() {
        return CONTEXT;
    }

}
