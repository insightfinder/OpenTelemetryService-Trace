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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaegerService {

  private static final Logger LOG = LoggerFactory.getLogger(JaegerService.class);
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
    ManagedChannel channel = ManagedChannelBuilder.forTarget(grpcUrl).usePlaintext().build();
    this.traceServiceStub = TraceServiceGrpc.newBlockingStub(channel);
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
      LOG.info("Successfully save spans for trace {} to Jaeger", traceID);
    } catch (Exception e) {
      LOG.error("Error sending trace data to Jaeger", e);
    }
  }

  public JSONObject getTraceData(String traceID) {
    String queryUrl = this.queryUrl + traceID;
    Request request = new Request.Builder()
        .url(queryUrl)
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        LOG.error("Unexpected code {}", response);
        return null;
      }
      String responseBody = response.body().string();
      return JSONObject.parseObject(responseBody);
    } catch (IOException e) {
      LOG.error("Error querying trace data from Jaeger", e);
      return null;
    }
  }
}
