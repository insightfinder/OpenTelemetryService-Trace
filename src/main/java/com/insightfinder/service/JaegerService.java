package com.insightfinder.service;
import com.insightfinder.util.ParseUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import com.alibaba.fastjson2.JSONObject;
import java.io.IOException;

public class JaegerService {
    private static final Logger LOG = LoggerFactory.getLogger(JaegerService.class);
    private final ManagedChannel channel;
    private final TraceServiceGrpc.TraceServiceBlockingStub traceServiceStub;
    private final OkHttpClient httpClient;

    private final String grpcUrl;
    private final String queryUrl;


    // TODO: Create configuration files to let user configure the Jaeger backend.s
    public JaegerService(String servername, boolean isTLS, int grpcPort, int uiPort) {

        // Init HTTP query endpoint
        String protocol = isTLS ? "https" : "http";
        String baseURL = "%s://%s:%d".formatted(protocol,servername,uiPort);
        this.queryUrl = "%s/api/traces/".formatted(baseURL);
        this.httpClient = new OkHttpClient();
        

        // Init gRPC client
        this.grpcUrl = "%s:%d".formatted(servername,grpcPort);
        this.channel = ManagedChannelBuilder.forTarget(this.grpcUrl).usePlaintext().build();
        this.traceServiceStub = TraceServiceGrpc.newBlockingStub(channel);
    }

    public void saveTraceData(ExportTraceServiceRequest request) {

        String traceID = ParseUtil.parseHexadecimalBytes(request.getResourceSpans(0).getScopeSpans(0).getSpans(0).getTraceId());

        try {
            ExportTraceServiceResponse response = traceServiceStub.export(request);
            LOG.info("Successfully save spans for trace {} to Jaeger",traceID);
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
