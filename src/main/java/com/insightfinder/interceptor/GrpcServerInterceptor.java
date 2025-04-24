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
    String ifSystem = ParseUtil.getSystemFromMedata(metadata);
    String ifLicenseKey = ParseUtil.getLicenseKeyFromMedata(metadata);

    if (StringUtils.isNullOrEmpty(ifUser)) {
      log.warn("'ifuser' header of OpenTelemetry exporter is empty.");
    }
    if (StringUtils.isNullOrEmpty(ifProject)) {
      log.warn("'ifproject' header of OpenTelemetry exporter is empty.");
    }
    if (StringUtils.isNullOrEmpty(ifSystem)) {
      log.warn("'ifsystem' header of OpenTelemetry exporter is empty.");
    }
    if (StringUtils.isNullOrEmpty(ifLicenseKey)) {
      log.warn("'iflicenseKey' header of OpenTelemetry exporter is empty.");
    }
    Context context = Context.current()
        .withValue(METADATA_KEY, new ContextMetadata(ifUser, ifProject, ifSystem, ifLicenseKey));
    return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
  }
}
