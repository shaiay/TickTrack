package com.theflopguyproductions.ticktrack.counter;

import java.time.Instant;
import java.util.ArrayDeque;

public class TimestampFifoQueue {

    private final int maxSize;
    private final ArrayDeque<Instant> queue;

    public TimestampFifoQueue(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new ArrayDeque<>(maxSize);
    }

    public synchronized void addTimestamp(Instant timestamp) {
        if (queue.size() == maxSize) {
            queue.poll(); // Remove oldest timestamp if queue is full
        }
        queue.offer(timestamp);
    }

    public synchronized Instant processNextTimestamp() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public Iterable<Instant> getAsIterable() {
        return queue;
    }
}