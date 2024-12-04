package com.insightfinder.worker;

import com.insightfinder.config.Config;
import com.insightfinder.mapper.TraceDataMapper;
import com.insightfinder.model.DataType;
import com.insightfinder.model.ProjectCloudType;
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
  private final Config config = Config.getInstance();

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

      // Create trace Project IfNotExist and save the result to cache.
      if (!insightFinderService.isProjectCreated(traceInfo.getIfProject(), traceInfo.getIfProject(),
          traceInfo.getIfUser(), traceInfo.getIfLicenseKey(), DataType.DATA_TYPE_TRACE,
          ProjectCloudType.PRIVATE_CLOUD)) {
        continue;
      }

      if (!insightFinderService.isProjectCreated(config.getPromptProjectName(),
          config.getPromptSystemName(), traceInfo.getIfUser(), traceInfo.getIfLicenseKey(),
          DataType.DATA_TYPE_TRACE, ProjectCloudType.PRIVATE_CLOUD)) {
        continue;
      }

      // Get Trace data from Jaeger
      var rawJaegerData = jaegerService.getTraceData(traceInfo.getTraceId());
      if (rawJaegerData == null) {
        continue;
      }

      var parsedTraceInfo = traceDataMapper.fromRawJaegerData(rawJaegerData, traceInfo);
      if (parsedTraceInfo != null) {
        var traceDataBody = parsedTraceInfo.getTraceDataBody();
        if (traceDataBody != null && !traceDataBody.isEmpty()) {
          insightFinderService.sendTraceData(traceDataBody, traceInfo);
        }
        var promptResponsePairs = parsedTraceInfo.getPromptResponsePairs();
        if (promptResponsePairs != null && !promptResponsePairs.isEmpty()) {
          insightFinderService.sendPromptData(promptResponsePairs, traceInfo);
        }
      }
    }
  }
}
