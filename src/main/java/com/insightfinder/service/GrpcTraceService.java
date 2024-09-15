package com.insightfinder.service;

import com.insightfinder.GrpcServer;
import com.insightfinder.model.message.Message;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.*;
import io.opentelemetry.proto.trace.v1.*;
import org.slf4j.*;
import com.insightfinder.util.ParseUtil;

import static com.insightfinder.GrpcServer.METADATA_KEY;

public class GrpcTraceService extends TraceServiceGrpc.TraceServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcTraceService.class);

  @Override
  public void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> responseObserver) {

    LOG.info("Received trace data from user {}.", ParseUtil.getIfUserFromMetadata(METADATA_KEY.get()));

    // Extract trace data body
    exportSpanData(request);

    // Send Trace to Jaeger
    JaegerService jaegerService = new JaegerService("111.111.111.111",false,4317,16686);
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

    if(ifUser == null || ifUser.isEmpty()){
      LOG.error("'ifuser' header of OpenTelemetry exporter.");

    }
    if(ifProject == null || ifProject.isEmpty()){
      LOG.error("'ifproject' header of OpenTelemetry exporter.");
    }
    if(ifLicenseKey == null || ifLicenseKey.isEmpty()){
      LOG.error("'iflicenseKey' header of OpenTelemetry exporter.");
    }

    for (ResourceSpans resourceSpans : request.getResourceSpansList()) {
      for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
        for (Span rawSpan : scopeSpans.getSpansList()) {
          var traceID = ParseUtil.parseHexadecimalBytes(rawSpan.getTraceId());

          GrpcServer.queue.offer(new Message(traceID,ifUser,ifProject,ifLicenseKey));
        }
      }
    }
  }
}