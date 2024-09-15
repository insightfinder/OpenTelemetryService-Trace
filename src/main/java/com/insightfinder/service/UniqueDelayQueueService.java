package com.insightfinder.service;

import java.util.Set;
import java.util.concurrent.*;

public class UniqueDelayQueueService<E extends Delayed> {
    private final DelayQueue<E> delayQueue = new DelayQueue<>();
    private final Set<E> messageSet = ConcurrentHashMap.newKeySet();

    public boolean offer(E e) {
        if (messageSet.add(e)) {
            return delayQueue.offer(e);
        } else {
            // Duplicate message, do not add
            return false;
        }
    }

    public E take() throws InterruptedException {
        E e = delayQueue.take();
        messageSet.remove(e);
        return e;
    }

    // Implement other necessary methods like poll(), peek(), etc.
}