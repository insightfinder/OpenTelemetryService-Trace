package com.insightfinder.worker;

import com.insightfinder.mapper.TraceDataMapper;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.service.InsightFinderService;
import com.insightfinder.service.JaegerService;
import com.insightfinder.service.UniqueDelayQueueManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceWorker implements Runnable {

  private final InsightFinderService insightFinderService = InsightFinderService.getInstance();
  private final JaegerService jaegerService = JaegerService.getInstance();
  private final UniqueDelayQueueManager uniqueDelayQueueManager = UniqueDelayQueueManager.getInstance();
  private final TraceDataMapper traceDataMapper = TraceDataMapper.getInstance();

  public TraceWorker(int threadNum) {
    log.info("Trace Worker thread {} started.", threadNum);
  }

  @Override
  public void run() {
    TraceInfo traceInfo;

    while (true) {
      // Get task from the queue.
      traceInfo = uniqueDelayQueueManager.takeMessage();
      if (traceInfo == null) {
        continue;
      }
      log.info("Prepare to send trace '{}' to project '{}' for user '{}'.",
          traceInfo.getTraceId(), traceInfo.getIfProject(), traceInfo.getIfUser());

      // Create Project IfNotExist and save the result to cache.
      if (!insightFinderService.isProjectCreated(traceInfo.getIfProject(), traceInfo.getIfProject(),
          traceInfo.getIfUser(), traceInfo.getIfLicenseKey())) {
        continue;
      }

      // Get Trace data from Jaeger
      var rawJaegerData = jaegerService.getTraceData(traceInfo.getTraceId());
      if (rawJaegerData == null) {
        continue;
      }

      var traceDataBody = traceDataMapper.fromRawJaegerData(rawJaegerData, traceInfo);
      if (traceDataBody != null && !traceDataBody.isEmpty()) {
        insightFinderService.sendTraceData(traceDataBody, traceInfo);
      }
    }
  }
}
