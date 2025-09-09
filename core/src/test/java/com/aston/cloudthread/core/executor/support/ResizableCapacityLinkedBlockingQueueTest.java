package com.aston.cloudthread.core.executor.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResizableCapacityLinkedBlockingQueueTest {
    private ResizableCapacityLinkedBlockingQueue<Integer> queue;

    @BeforeEach
    void setUp() {
        queue = new ResizableCapacityLinkedBlockingQueue<>(3);
    }

    @Test
    void testConstructorAndInitialSize() {
        assertEquals(0, queue.size());
        assertEquals(3, queue.remainingCapacity());
    }

    @Test
    void testPutAndTake() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        assertEquals(2, queue.size());
        int val1 = queue.take();
        int val2 = queue.take();
        assertEquals(1, val1);
        assertEquals(2, val2);
        assertEquals(0, queue.size());
    }

    @Test
    void testOfferAndPoll() throws InterruptedException {
        assertTrue(queue.offer(10));
        assertTrue(queue.offer(20));
        assertEquals(2, queue.size());

        Integer val1 = queue.poll();
        Integer val2 = queue.poll();
        assertEquals(10, val1);
        assertEquals(20, val2);
    }

    @Test
    void testOfferWithTimeout() throws InterruptedException {
        assertTrue(queue.offer(1, 1, TimeUnit.SECONDS));
        assertTrue(queue.offer(2, 1, TimeUnit.SECONDS));
        assertTrue(queue.offer(3, 1, TimeUnit.SECONDS));

        // Queue is full now, should return false after timeout
        assertFalse(queue.offer(4, 100, TimeUnit.MILLISECONDS));
    }

    @Test
    void testPeek() throws InterruptedException {
        assertNull(queue.peek());
        queue.put(42);
        assertEquals(42, queue.peek());
        assertEquals(1, queue.size()); // peek does not remove
    }

    @Test
    void testRemove() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        queue.put(3);
        assertTrue(queue.remove(2));
        assertFalse(queue.remove(99));
        assertEquals(2, queue.size());
    }

    @Test
    void testClear() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        queue.clear();
        assertEquals(0, queue.size());
        assertNull(queue.poll());
    }

    @Test
    void testRemainingCapacityAndSetCapacity() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        assertEquals(1, queue.remainingCapacity());

        queue.setCapacity(5);
        assertEquals(3, queue.remainingCapacity());
    }

    @Test
    void testDrainTo() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        queue.put(3);

        List<Integer> list = new ArrayList<>();
        int drained = queue.drainTo(list);
        assertEquals(3, drained);
        assertEquals(List.of(1, 2, 3), list);
        assertEquals(0, queue.size());
    }

    @Test
    void testDrainToWithMaxElements() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        queue.put(3);

        List<Integer> list = new ArrayList<>();
        int drained = queue.drainTo(list, 2);
        assertEquals(2, drained);
        assertEquals(List.of(1, 2), list);
        assertEquals(1, queue.size());
    }

    @Test
    void testIterator() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        queue.put(3);

        List<Integer> iterated = new ArrayList<>();
        for (Integer val : queue) {
            iterated.add(val);
        }
        assertEquals(List.of(1, 2, 3), iterated);
    }

    @Test
    void testIteratorRemove() throws InterruptedException {
        queue.put(1);
        queue.put(2);
        queue.put(3);

        var iterator = queue.iterator();
        while (iterator.hasNext()) {
            Integer val = iterator.next();
            if (val == 2) {
                iterator.remove();
            }
        }
        assertEquals(2, queue.size());
        assertFalse(queue.toArray()[1].equals(2));
    }

    @Test
    void testCapacityValidation() {
        assertThrows(IllegalArgumentException.class, () -> new ResizableCapacityLinkedBlockingQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new ResizableCapacityLinkedBlockingQueue<>(-1));
    }

    @Test
    void testNullInsertions() {
        assertThrows(NullPointerException.class, () -> queue.put(null));
        assertThrows(NullPointerException.class, () -> queue.offer(null));
        assertThrows(NullPointerException.class, () -> queue.offer(null, 1, TimeUnit.SECONDS));
    }
}