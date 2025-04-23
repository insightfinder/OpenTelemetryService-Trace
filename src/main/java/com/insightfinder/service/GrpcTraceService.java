package com.insightfinder.service;

import static com.insightfinder.util.Constants.METADATA_KEY;

import com.insightfinder.model.ContextMetadata;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.util.ParseUtil;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcTraceService extends TraceServiceGrpc.TraceServiceImplBase {

  private final UniqueDelayQueueManager uniqueDelayQueueManager = UniqueDelayQueueManager.getInstance();
  private final JaegerService jaegerService = JaegerService.getInstance();

  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> responseObserver) {

    // Extract trace data body and add data to the queue
    exportSpanData(request);

    log.info("Received trace data from user {}.", METADATA_KEY.get().getUsername());

    // Send Trace to Jaeger
    jaegerService.saveTraceData(request);

    // Send a response back to the client
    ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
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