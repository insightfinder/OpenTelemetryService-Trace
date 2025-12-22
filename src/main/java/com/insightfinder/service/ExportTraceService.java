package com.insightfinder.service;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
;
import com.insightfinder.model.request.ContentData;
import com.insightfinder.model.request.InputPrompt;
import com.insightfinder.model.request.ResponseRecord;
import com.insightfinder.util.TokenizerUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExportTraceService {

  private final SensitiveDataFilter sensitiveDataFilter = SensitiveDataFilter.getInstance();

  public void exportSpanData(ExportTraceServiceRequest request) {
    ExportTraceServiceRequest sanitized = sanitizeExportTraceServiceRequest(request);
    // existing logic to export sanitized request to InsightFinder...
    // insightFinderService.export(sanitized);
  }

  private ExportTraceServiceRequest sanitizeExportTraceServiceRequest(ExportTraceServiceRequest req) {
    if (req == null || sensitiveDataFilter == null) {
      return req;
    }

    ExportTraceServiceRequest.Builder b = ExportTraceServiceRequest.builder();

    // Example string fields: adjust based on actual model
    b.traceId(filter(req.getTraceId()));
    b.projectName(filter(req.getProjectName()));
    b.systemName(filter(req.getSystemName()));
    b.userName(filter(req.getUserName()));
    b.licenseKey(filter(req.getLicenseKey()));

    // Span data strings
    b.spanId(filter(req.getSpanId()));
    b.operationName(filter(req.getOperationName()));
    b.serviceName(filter(req.getServiceName()));
    b.spanTags(sanitizeTags(req.getSpanTags()));
    b.spanLogs(sanitizeLogs(req.getSpanLogs()));

    // Prompt/response pairs if present
    b.promptResponsePairs(sanitizeContentPairs(req.getPromptResponsePairs()));

    // Preserve non-string fields
    b.startTime(req.getStartTime());
    b.endTime(req.getEndTime());
    b.duration(req.getDuration());
    b.attributes(req.getAttributes()); // clone if mutable

    return b.build();
  }

  private String filter(String s) {
    if (s == null) return null;
    String filtered = SensitiveDataFilter.filterString(s, sensitiveDataFilter);
    return filtered == null ? "" : filtered;
  }

  private List<String> sanitizeTags(List<String> tags) {
    if (tags == null) return null;
    List<String> out = new ArrayList<>(tags.size());
    for (String t : tags) {
      out.add(filter(t));
    }
    return out;
  }

  private List<String> sanitizeLogs(List<String> logs) {
    if (logs == null) return null;
    List<String> out = new ArrayList<>(logs.size());
    for (String l : logs) {
      out.add(filter(l));
    }
    return out;
  }

  private List<ContentData> sanitizeContentPairs(List<ContentData> pairs) {
    if (pairs == null || pairs.isEmpty()) return pairs;
    List<ContentData> sanitized = new ArrayList<>(pairs.size());
    for (ContentData cd : pairs) {
      if (cd == null || cd.getInputPrompt() == null || cd.getResponseRecord() == null) {
        sanitized.add(cd);
        continue;
      }
      String input = Objects.toString(cd.getInputPrompt().getPrompt(), "");
      String output = Objects.toString(cd.getResponseRecord().getResponse(), "");

      String safeInput = filter(input);
      String safeOutput = filter(output);

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