package com.insightfinder.service;

import static com.insightfinder.util.Constants.METADATA_KEY;

import com.insightfinder.model.ContextMetadata;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.util.ParseUtil;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceExportProcessor {

  private static final TraceExportProcessor instance = new TraceExportProcessor();

  private final UniqueDelayQueueManager uniqueDelayQueueManager = UniqueDelayQueueManager.getInstance();
  private final JaegerService jaegerService = JaegerService.getInstance();

  private TraceExportProcessor() {
  }

  public static TraceExportProcessor getInstance() {
    return instance;
  }

  /**
   * Transport-agnostic processing of an OTLP ExportTraceServiceRequest. Relies on the caller
   * having already attached the request's tenant metadata to the current {@link
   * com.insightfinder.util.Constants#METADATA_KEY} context.
   */
  public void process(ExportTraceServiceRequest request) {
    logIncomingTokenAttributes(request);
    request = SensitiveDataFilterV2.deepSanitizeRequest(request);

    // Extract trace data body and add data to the queue
    exportSpanData(request);

    log.info("Received trace data from user {}.", METADATA_KEY.get().getUsername());

    // Send Trace to Jaeger
    jaegerService.saveTraceData(request);
  }

  private void logIncomingTokenAttributes(ExportTraceServiceRequest request) {
    for (ResourceSpans resourceSpans : request.getResourceSpansList()) {
      for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
        for (Span span : scopeSpans.getSpansList()) {
          for (KeyValue kv : span.getAttributesList()) {
            if (kv.getKey().equals("prompt_tokens") || kv.getKey().equals("chat.prompt_tokens")
                || kv.getKey().equals("response_tokens") || kv.getKey().equals("chat.completion_tokens")) {
              log.info(
                  "[TokenDebugOtelTrace] GrpcTraceService.export (pre-sanitize) operationName={} key={} "
                      + "valueCase={} value={}",
                  span.getName(), kv.getKey(), kv.getValue().getValueCase(), describeAnyValue(kv.getValue()));
            }
          }
        }
      }
    }
  }

  private String describeAnyValue(AnyValue value) {
    return switch (value.getValueCase()) {
      case STRING_VALUE -> "STRING:" + value.getStringValue();
      case INT_VALUE -> "INT:" + value.getIntValue();
      case DOUBLE_VALUE -> "DOUBLE:" + value.getDoubleValue();
      case BOOL_VALUE -> "BOOL:" + value.getBoolValue();
      default -> "OTHER:" + value.getValueCase() + ":" + value;
    };
  }

  private void exportSpanData(ExportTraceServiceRequest request) {
    ContextMetadata metadata = METADATA_KEY.get();
    for (ResourceSpans resourceSpans : request.getResourceSpansList()) {
      for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
        for (Span rawSpan : scopeSpans.getSpansList()) {
          var traceID = ParseUtil.parseHexadecimalBytes(rawSpan.getTraceId());

          // Try to get insightfinder settings from the header
          var username = metadata.getUsername();
          var projectName = metadata.getProjectName();
          var systemName = metadata.getSystemName();
          var licenseKey = metadata.getLicenseKey();

          // Try to get insightfinder settings from the span attributes if any of then is missing in the header.
          if (username == null || projectName == null || licenseKey == null) {
            log.info("InsightFinder metadata is not provided. Trying to get them from the span attributes.");
            var attrsMap = ParseUtil.parseAttrsMapFromAttributeList(rawSpan.getAttributesList());

            if (username == null){
              username = attrsMap.get("x-username");
            }
            if (projectName == null){
              projectName = attrsMap.get("x-trace-project");
            }

            if (licenseKey == null){
              licenseKey = attrsMap.get("x-licensekey");
            }

            if (systemName == null){
              systemName = attrsMap.get("x-system-name") == null ? "" : attrsMap.get("x-system-name");
            }

          }

          if (username != null && projectName != null && licenseKey != null) {
            uniqueDelayQueueManager.offerMessage(
                    new TraceInfo(traceID, username, projectName,
                            systemName, licenseKey));
            }
          else {
            log.error("InsightFinder settings are missing. Trace ID: {}", traceID);
          }
        }
      }
    }
  }

}
