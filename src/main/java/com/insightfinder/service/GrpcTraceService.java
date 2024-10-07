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

    log.info("Received trace data from user {}.", METADATA_KEY.get().getUsername());

    // Extract trace data body and add data to the queue
    exportSpanData(request);

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

          uniqueDelayQueueManager.offerMessage(
              new TraceInfo(traceID, metadata.getUsername(), metadata.getProjectName(),
                  metadata.getLicenseKey()));
        }
      }
    }
  }
}