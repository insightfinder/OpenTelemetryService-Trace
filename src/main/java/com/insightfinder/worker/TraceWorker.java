package com.insightfinder.worker;

import com.insightfinder.config.Config;
import com.insightfinder.mapper.TraceDataMapper;
import com.insightfinder.model.DataType;
import com.insightfinder.model.ProjectCloudType;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.model.request.ContentData;
import com.insightfinder.model.request.InputPrompt;
import com.insightfinder.model.request.ResponseRecord;
import com.insightfinder.service.InsightFinderService;
import com.insightfinder.service.JaegerService;
import com.insightfinder.service.SensitiveDataFilter;
import com.insightfinder.service.UniqueDelayQueueManager;
import com.insightfinder.util.TokenizerUtil;
import io.opentelemetry.api.internal.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceWorker implements Runnable {

  private final InsightFinderService insightFinderService = InsightFinderService.getInstance();
  private final JaegerService jaegerService = JaegerService.getInstance();
  private final UniqueDelayQueueManager uniqueDelayQueueManager = UniqueDelayQueueManager.getInstance();
  private final TraceDataMapper traceDataMapper = TraceDataMapper.getInstance();
  private final Config config = Config.getInstance();
  private static final String PROMPT_PROJECT_SUFFIX = "-Prompt";
  private final SensitiveDataFilter sensitiveDataFilter = SensitiveDataFilter.getInstance();


  public TraceWorker(int threadNum) {
    log.info("Trace Worker thread {} started.", threadNum);
  }

  @Override
  public void run() {
    TraceInfo traceInfo;

    while (true) {
      try {
        // Get task from the queue.
        traceInfo = uniqueDelayQueueManager.takeMessage();
        if (traceInfo == null) {
          continue;
        }
        log.info("Prepare to send trace '{}' to project '{}' for user '{}'.",
            traceInfo.getTraceId(), traceInfo.getIfProject(), traceInfo.getIfUser());

        // Create trace Project IfNotExist and save the result to cache.
        if (!insightFinderService.isProjectCreated(traceInfo.getIfProject(),
            traceInfo.getIfSystem(),
            traceInfo.getIfUser(), traceInfo.getIfLicenseKey(), DataType.DATA_TYPE_TRACE,
            ProjectCloudType.PRIVATE_CLOUD)) {
          continue;
        }
        String promptProjectName =
            StringUtils.isNullOrEmpty(config.getPromptProjectName()) ?
                traceInfo.getIfProject() + PROMPT_PROJECT_SUFFIX : config.getPromptProjectName();
        String promptProjectSystemName =
            StringUtils.isNullOrEmpty(config.getPromptSystemName()) ? traceInfo.getIfSystem() :
                config.getPromptSystemName();
        if (!insightFinderService.isProjectCreated(promptProjectName, promptProjectSystemName,
            traceInfo.getIfUser(), traceInfo.getIfLicenseKey(),
            DataType.DATA_TYPE_TRACE, ProjectCloudType.PRIVATE_CLOUD)) {
          continue;
        }
        var promptInfo = new TraceInfo(traceInfo.getTraceId(), traceInfo.getIfUser(),
            promptProjectName,
            promptProjectSystemName, traceInfo.getIfLicenseKey());

        // Get Trace data from Jaeger
        var rawJaegerData = jaegerService.getTraceData(traceInfo.getTraceId());
        if (rawJaegerData == null) {
          continue;
        }

        var parsedTraceInfo = traceDataMapper.fromRawJaegerData(rawJaegerData, traceInfo);
        if (parsedTraceInfo != null) {
          var traceDataBody = parsedTraceInfo.getTraceDataBody();
          List<ContentData> promptResponsePairs = parsedTraceInfo.getPromptResponsePairs();
          if (traceDataBody != null && !traceDataBody.isEmpty()) {
            insightFinderService.sendTraceData(traceDataBody, traceInfo, promptResponsePairs);
          }
          if (promptResponsePairs != null && !promptResponsePairs.isEmpty()) {
            insightFinderService.sendPromptData(promptResponsePairs, promptInfo);
          } else {
            log.warn("Empty prompt prompt / response pairs for trace {}.", traceInfo.getTraceId());
          }
        }
      } catch (Throwable e) {
        log.error("Error processing trace data.", e);
      }
    }
  }

  private List<ContentData> sanitize(List<ContentData> pairs) {
    if (pairs == null || pairs.isEmpty() || sensitiveDataFilter == null) {
      return pairs;
    }
    List<ContentData> sanitized = new ArrayList<>(pairs.size());
    for (ContentData cd : pairs) {
      if (cd == null || cd.getInputPrompt() == null || cd.getResponseRecord() == null) {
        sanitized.add(cd);
        continue;
      }
      String input = Objects.toString(cd.getInputPrompt().getPrompt(), "");
      String output = Objects.toString(cd.getResponseRecord().getResponse(), "");

      String safeInput = SensitiveDataFilter.filterString(input, sensitiveDataFilter);
      String safeOutput = SensitiveDataFilter.filterString(output, sensitiveDataFilter);

      int promptTokens = TokenizerUtil.splitByWhiteSpaceTokenizer(safeInput);
      int responseTokens = TokenizerUtil.splitByWhiteSpaceTokenizer(safeOutput);

      sanitized.add(ContentData.builder()
          .inputPrompt(new InputPrompt(safeInput, promptTokens))
          .responseRecord(new ResponseRecord(safeOutput, responseTokens))
          .build());
    }
    return sanitized;
  }
}
