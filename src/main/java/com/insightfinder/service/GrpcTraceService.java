package com.insightfinder.service;

import static com.insightfinder.GrpcServer.METADATA_KEY;

import com.insightfinder.GrpcServer;
import com.insightfinder.model.message.Message;
import com.insightfinder.util.ParseUtil;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcTraceService extends TraceServiceGrpc.TraceServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcTraceService.class);
  private final JaegerService jaegerService = JaegerService.getInstance();

  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> responseObserver) {

    LOG.info("Received trace data from user {}.",
        ParseUtil.getIfUserFromMetadata(METADATA_KEY.get()));

    // Extract trace data body
    exportSpanData(request);

    // Send Trace to Jaeger
    jaegerService.saveTraceData(request);

    // Send a response back to the client
    ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private void exportSpanData(ExportTraceServiceRequest request) {
    Metadata metadata = METADATA_KEY.get();
    String ifUser = ParseUtil.getIfUserFromMetadata(metadata);
    String ifProject = ParseUtil.getProjectFromMedata(metadata);
    String ifLicenseKey = ParseUtil.getLicenseKeyFromMedata(metadata);

    if (ifUser == null || ifUser.isEmpty()) {
      LOG.error("'ifuser' header of OpenTelemetry exporter.");

    }
    if (ifProject == null || ifProject.isEmpty()) {
      LOG.error("'ifproject' header of OpenTelemetry exporter.");
    }
    if (ifLicenseKey == null || ifLicenseKey.isEmpty()) {
      LOG.error("'iflicenseKey' header of OpenTelemetry exporter.");
    }

    for (ResourceSpans resourceSpans : request.getResourceSpansList()) {
      for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
        for (Span rawSpan : scopeSpans.getSpansList()) {
          var traceID = ParseUtil.parseHexadecimalBytes(rawSpan.getTraceId());

          GrpcServer.queue.offer(new Message(traceID, ifUser, ifProject, ifLicenseKey));
        }
      }
    }
  }
}