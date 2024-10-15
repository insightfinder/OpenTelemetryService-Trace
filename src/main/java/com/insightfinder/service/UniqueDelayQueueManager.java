package com.insightfinder.service;

import com.insightfinder.model.message.TraceInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UniqueDelayQueueManager {

  private static final UniqueDelayQueue<TraceInfo> queue = new UniqueDelayQueue<>();
  private static UniqueDelayQueueManager instance;

  private UniqueDelayQueueManager() {
  }

  public static UniqueDelayQueueManager getInstance() {
    if (instance == null) {
      instance = new UniqueDelayQueueManager();
    }
    return instance;
  }

  public TraceInfo takeMessage() {
    try {
      return queue.take();
    } catch (InterruptedException e) {
      log.error("Error taking message from delay queue: {}", e.getMessage());
      return null;
    }
  }

  public boolean offerMessage(TraceInfo traceInfo) {
    return queue.offer(traceInfo);
  }
}
