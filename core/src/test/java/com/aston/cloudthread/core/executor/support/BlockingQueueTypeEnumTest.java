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

import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTypeEnumTest {
    @Test
    void testCreateArrayBlockingQueue() {
        BlockingQueue<Integer> queue = BlockingQueueTypeEnum.createBlockingQueue("ArrayBlockingQueue", 5);
        assertNotNull(queue);
        assertEquals(0, queue.size());
        queue.offer(1);
        assertEquals(1, queue.size());
    }
    @Test
    void testCreateLinkedBlockingQueueWithCapacity() {
        BlockingQueue<String> queue = BlockingQueueTypeEnum.createBlockingQueue("LinkedBlockingQueue", 3);
        assertNotNull(queue);
        queue.offer("a");
        queue.offer("b");
        queue.offer("c");
        assertFalse(queue.offer("d")); // queue is full
    }

    @Test
    void testCreateLinkedBlockingQueueWithoutCapacity() {
        BlockingQueue<String> queue = BlockingQueueTypeEnum.createBlockingQueue("LinkedBlockingQueue", null);
        assertNotNull(queue);
        // we can append no limited elements to the queue
        for (int i = 0; i < 1000; i++) {
            queue.offer("item-" + i);
        }
        assertEquals(1000, queue.size());
    }

    @Test
    void testCreateLinkedBlockingDeque() {
        BlockingQueue<Integer> queue = BlockingQueueTypeEnum.createBlockingQueue("LinkedBlockingDeque", 4);
        assertNotNull(queue);
        queue.offer(1);
        queue.offer(2);
        assertEquals(2, queue.size());
    }

    @Test
    void testCreateSynchronousQueue() throws InterruptedException {
        BlockingQueue<Integer> queue = BlockingQueueTypeEnum.createBlockingQueue("SynchronousQueue", null);
        assertNotNull(queue);
        // SynchronousQueue no capacity, offer return false immediately
        assertFalse(queue.offer(1));
    }

    @Test
    void testCreateLinkedTransferQueue() {
        BlockingQueue<Integer> queue = BlockingQueueTypeEnum.createBlockingQueue("LinkedTransferQueue", null);
        assertNotNull(queue);
        queue.offer(1);
        queue.offer(2);
        assertEquals(2, queue.size());
    }

    @Test
    void testCreatePriorityBlockingQueue() {
        BlockingQueue<Integer> queue = BlockingQueueTypeEnum.createBlockingQueue("PriorityBlockingQueue", null);
        assertNotNull(queue);
        queue.offer(5);
        queue.offer(1);
        queue.offer(3);
        assertEquals(3, queue.size());
        assertEquals(1, queue.peek());
    }

    @Test
    void testCreateResizableCapacityLinkedBlockingQueue() {
        BlockingQueue<Integer> queue = BlockingQueueTypeEnum.createBlockingQueue("ResizableCapacityLinkedBlockingQueue", 10);
        assertNotNull(queue);
        queue.offer(1);
        queue.offer(2);
        assertEquals(2, queue.size());
    }

    @Test
    void testInvalidQueueName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> BlockingQueueTypeEnum.createBlockingQueue("UnknownQueue", 5)
        );
        assertTrue(exception.getMessage().contains("No matching type of blocking queue was found"));
    }
}