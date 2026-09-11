package com.insightfinder.service;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcTraceService extends TraceServiceGrpc.TraceServiceImplBase {

  private final TraceExportProcessor traceExportProcessor = TraceExportProcessor.getInstance();

  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> responseObserver) {
    traceExportProcessor.process(request);

    // Send a response back to the client
    ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
