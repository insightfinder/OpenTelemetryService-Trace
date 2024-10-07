package com.insightfinder.interceptor;

import static com.insightfinder.util.Constants.METADATA_KEY;

import com.insightfinder.model.ContextMetadata;
import com.insightfinder.util.ParseUtil;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.opentelemetry.api.internal.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcServerInterceptor implements ServerInterceptor {

  /**
   * Interception Service that stores metadata in the context
   */
  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
      Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
    String ifUser = ParseUtil.getIfUserFromMetadata(metadata);
    String ifProject = ParseUtil.getProjectFromMedata(metadata);
    String ifLicenseKey = ParseUtil.getLicenseKeyFromMedata(metadata);

    if (StringUtils.isNullOrEmpty(ifUser)) {
      log.error("'ifuser' header of OpenTelemetry exporter is empty.");
      Status status = Status.FAILED_PRECONDITION.withDescription(
          "'ifuser' header of OpenTelemetry exporter is empty.");
      serverCall.close(status, new Metadata());
      return new ServerCall.Listener<>() {
      };
    }
    if (StringUtils.isNullOrEmpty(ifProject)) {
      log.error("'ifproject' header of OpenTelemetry exporter is empty.");
      Status status = Status.FAILED_PRECONDITION.withDescription(
          "'ifproject' header of OpenTelemetry exporter is empty.");
      serverCall.close(status, new Metadata());
      return new ServerCall.Listener<>() {
      };
    }
    if (StringUtils.isNullOrEmpty(ifLicenseKey)) {
      log.error("'iflicenseKey' header of OpenTelemetry exporter is empty.");
      Status status = Status.FAILED_PRECONDITION.withDescription(
          "'iflicenseKey' header of OpenTelemetry exporter is empty.");
      serverCall.close(status, new Metadata());
      return new ServerCall.Listener<>() {
      };
    }
    Context context = Context.current()
        .withValue(METADATA_KEY, new ContextMetadata(ifUser, ifProject, ifLicenseKey));
    return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
  }
}
