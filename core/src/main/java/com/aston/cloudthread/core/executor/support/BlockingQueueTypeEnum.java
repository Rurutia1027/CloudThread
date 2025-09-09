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
package com.aston.cloudthread.core.executor.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Enum representing different types of Java BlockingQueues.
 *
 * <p>This enum serves a centralized factory and registry for creating various
 * BlockingQueue implementations, including both bounded and unbounded queues.
 * It supports the following queue types:
 * <ul>
 *     <li>ArrayBlockingQueue</li>
 *     <li>LinkedBlockingQueue</li>
 *     <li>LinkedBlockingDeque</li>
 *     <li>SynchronousQueue</li>
 *     <li>LinkedTransferQueue</li>
 *     <li>PriorityBlockingQueue</li>
 *     <li>ResizableCapacityLinkedBlockingQueue</li>
 * </ul>
 *
 * <p>Each enum constant provides two creation methods</p>
 * <ul>
 *     <li>{@link #of(Integer)} -- create a queue with a specified capacity</li>
 *     <li>{@link #of() -- create a queue with default or unlimited capacity}</li>
 * </ul>
 * <p>Additional features:</p>
 * <ul>
 *     <li>Provides a name-to-enum mapping for dynamic queue creation</li>
 *     <li>Supports centralized management and consistent creation of queues</li>
 *     <li>Throws IllegalArgumentException if an unknown queue type name is used</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code BlockingQueue<String> queue = BlockingQueueTypeEnum.createBlockingQueue("ArrayBlockingQueue", 10);}</pre>
 *
 * <p>This enum is particularly useful in thread pool implementations where the queue type
 * and capacity need to be configurable and consistent across the system.</p>
 * </p>
 */
public enum BlockingQueueTypeEnum {
    /**
     * {@link ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new ArrayBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new ArrayBlockingQueue<>(DEFAULT_CAPACITY);
        }
    },

    /**
     * {@link LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE("LinkedBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedBlockingQueue<>();
        }
    },

    /**
     * {@link LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE("LinkedBlockingDeque") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedBlockingDeque<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedBlockingDeque<>();
        }
    },

    /**
     * {@link SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE("SynchronousQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new SynchronousQueue<>();
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new SynchronousQueue<>();
        }
    },

    /**
     * {@link LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE("LinkedTransferQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedTransferQueue<>();
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedTransferQueue<>();
        }
    },

    /**
     * {@link PriorityBlockingQueue}
     */
    PRIORITY_BLOCKING_QUEUE("PriorityBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new PriorityBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new PriorityBlockingQueue<>();
        }
    },

    /**
     * {@link ResizableCapacityLinkedBlockingQueue}
     */
    RESIZABLE_CAPACITY_LINKED_BLOCKING_QUEUE("ResizableCapacityLinkedBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new ResizableCapacityLinkedBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new ResizableCapacityLinkedBlockingQueue<>();
        }
    };


    private final String name;

    BlockingQueueTypeEnum(String name) {
        this.name = name;
    }

    /**
     * Create the specified implement of BlockingQueue with init capacity.
     * Abstract method, depends on sub override.
     *
     * @param capacity the capacity of the queue
     * @param <T>      the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    abstract <T> BlockingQueue<T> of(Integer capacity);

    /**
     * Create the specified implement of BlockingQueue, has no capacity limit.
     * Abstract method, depends on sub override.
     *
     * @param <T> the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    abstract <T> BlockingQueue<T> of();

    private static final Map<String, BlockingQueueTypeEnum> NAME_TO_ENUM_MAP;

    static {
        final BlockingQueueTypeEnum[] values = BlockingQueueTypeEnum.values();
        NAME_TO_ENUM_MAP = new HashMap<>(values.length);
        for (BlockingQueueTypeEnum value : values) {
            NAME_TO_ENUM_MAP.put(value.name, value);
        }
    }

    /**
     * Creates a BlockingQueue with the given {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * and capacity.
     *
     * @param blockingQueueName {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * @param capacity          the capacity of the BlockingQueue
     * @param <T>               the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     * @throws IllegalArgumentException If no matching queue type is found
     */
    public static <T> BlockingQueue<T> createBlockingQueue(String blockingQueueName, Integer capacity) {
        final BlockingQueue<T> of = of(blockingQueueName, capacity);
        if (of != null) {
            return of;
        }

        throw new IllegalArgumentException("No matching type of blocking queue was found: "
                + blockingQueueName);
    }

    /**
     * Creates a BlockingQueue with the given {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * and capacity.
     *
     * @param blockingQueueName {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * @param capacity          the capacity of the BlockingQueue
     * @param <T>               the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    private static <T> BlockingQueue<T> of(String blockingQueueName, Integer capacity) {
        final BlockingQueueTypeEnum typeEnum = NAME_TO_ENUM_MAP.get(blockingQueueName);
        if (typeEnum == null) {
            return null;
        }

        return Objects.isNull(capacity) ? typeEnum.of() : typeEnum.of(capacity);
    }

    private static final int DEFAULT_CAPACITY = 4096;
}
