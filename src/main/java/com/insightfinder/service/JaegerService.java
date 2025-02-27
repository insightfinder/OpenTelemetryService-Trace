package com.insightfinder.service;

import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.config.Config;
import com.insightfinder.util.ParseUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class JaegerService {

  private static JaegerService instance;
  private final TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub;
  private final OkHttpClient httpClient;
  private final String queryUrl;

  private JaegerService() {
    // Init HTTP query endpoint
    Config config = Config.getInstance();
    String protocol = config.isJaegerTlsEnabled() ? "https" : "http";
    String baseURL = "%s://%s:%d".formatted(protocol, config.getJaegerServerName(),
        config.getJaegerUiPort());
    this.queryUrl = "%s/api/traces/".formatted(baseURL);
    this.httpClient = new OkHttpClient();

    // Init gRPC client
    String grpcUrl = "%s:%d".formatted(config.getJaegerServerName(), config.getJaegerGrpcPort());
    ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcUrl).usePlaintext()
        .maxInboundMessageSize(Integer.MAX_VALUE)
        .build();
    this.traceServiceStub = TraceServiceGrpc.newBlockingStub(channel)
        .withMaxOutboundMessageSize(Integer.MAX_VALUE)
        .withMaxInboundMessageSize(Integer.MAX_VALUE);
  }

  public static JaegerService getInstance() {
    if (instance == null) {
      instance = new JaegerService();
    }
    return instance;
  }

  public void saveTraceData(ExportTraceServiceRequest request) {
    String traceID = ParseUtil.parseHexadecimalBytes(
        request.getResourceSpans(0).getScopeSpans(0).getSpans(0).getTraceId());

    try {
      ExportTraceServiceResponse response = traceServiceStub.export(request);
      log.info("Successfully save spans for trace {} to Jaeger", traceID);
    } catch (Exception e) {
      log.error("Error sending trace data to Jaeger", e);
    }
  }

  public JSONObject getTraceData(String traceID) {
    String queryUrl = this.queryUrl + traceID;
    Request request = new Request.Builder()
        .url(queryUrl)
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("Unexpected code {}", response);
        return null;
      }
      if (response.body() == null) {
        log.warn("Received a null trace data from Jaeger");
        return null;
      }
      String responseBody = response.body().string();
      return JSONObject.parseObject(responseBody);
    } catch (IOException e) {
      log.error("Error querying trace data from Jaeger", e);
      return null;
    }
  }
}
