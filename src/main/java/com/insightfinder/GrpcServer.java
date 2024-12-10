package com.insightfinder;

import com.insightfinder.config.Config;
import com.insightfinder.interceptor.GrpcServerInterceptor;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.service.GrpcTraceService;
import com.insightfinder.service.JaegerService;
import com.insightfinder.service.UniqueDelayQueueManager;
import com.insightfinder.util.ParseUtil;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import com.insightfinder.worker.TraceWorker;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

@Slf4j
public class GrpcServer {

  private static final Config config = Config.getInstance();

  public static void main(String[] args) throws Exception {
    // Start Workers
    int workerNum = config.getAppTraceWorkerNum();
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    for (int i = 0; i < workerNum; i++) {
      executorService.submit(new TraceWorker(i));
      log.info("Worker {} started", i);
    }

    // Services
    GrpcTraceService traceService = new GrpcTraceService();

    log.info("Starting OTLP Trace Receiver...");
    ServerBuilder serverBuilder = ServerBuilder.forPort(config.getGrpcPort())
        .addService(traceService)
        .intercept(new GrpcServerInterceptor()) // Add the interceptor to store metadata
        .maxInboundMessageSize(config.getGrpcMaxInboundMessageSizeInKB() * 1024);
    if (config.isAppTlsEnabled()) {
      String appCertificateFilePath = config.getAppCertificateFile();
      String appPrivateKeyFilePath = config.getAppPrivateKeyFile();
      if (StringUtils.isNullOrEmpty(appCertificateFilePath) || StringUtils.isNullOrEmpty(
          appPrivateKeyFilePath)) {
        log.error("Certificate file or private key file is not provided.");
        System.exit(1);
      }
      File certChainFile = new File(appCertificateFilePath);
      File privateKeyFile = new File(appPrivateKeyFilePath);
      serverBuilder.useTransportSecurity(certChainFile, privateKeyFile);
    }
    Server grpcServer = serverBuilder.build();
    grpcServer.start();
    log.info("OTLP Trace Receiver (gRPC) started at port {}", config.getGrpcPort());

    // Start HTTP Server
    log.info("Starting OTLP Trace Receiver (HTTP)...");
    org.eclipse.jetty.server.Server httpServer = new org.eclipse.jetty.server.Server(14418);
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    context.setContextPath("/");
    httpServer.setHandler(context);
    context.addServlet(new ServletHolder(new HttpTraceServlet()), "/v1/traces");
    httpServer.start();

    // Await Termination
    grpcServer.awaitTermination();
    httpServer.join();
  }

  public static class HttpTraceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {

        // Log all headers
        var headers = new HashMap<String,String>();
        log.info("Incoming request headers:");
        Enumeration<String> headerNames = req.getHeaderNames();
        if (headerNames != null) {
          while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            headers.put(headerName, headerValue);
          }
        }

        // Get Needed headers
        var ifuser = headers.get("ifuser");
        var iflicensekey = headers.get("iflicensekey");
        var projectName = headers.get("ifproject");


        // Check Content-Type
        String contentType = req.getContentType();
        if (!"application/x-protobuf".equalsIgnoreCase(contentType)) {
          resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
          resp.getWriter().write("Unsupported Content-Type");
          return;
        }

        // Parse Protobuf data from the request body
        InputStream inputStream = req.getInputStream();
        ExportTraceServiceRequest traceRequest = ExportTraceServiceRequest.parseFrom(inputStream);

        // Log received data (for debugging)
        log.info("Received ExportTraceServiceRequest: {}", traceRequest);


        // Save To Jaeger
        JaegerService jaegerService = JaegerService.getInstance();
        jaegerService.saveTraceData(traceRequest);

        // Save to Queue
        UniqueDelayQueueManager uniqueDelayQueueManager = UniqueDelayQueueManager.getInstance();
        for (ResourceSpans resourceSpans : traceRequest.getResourceSpansList()) {
          for (ScopeSpans scopeSpans : resourceSpans.getScopeSpansList()) {
            for (Span rawSpan : scopeSpans.getSpansList()) {
              var traceID = ParseUtil.parseHexadecimalBytes(rawSpan.getTraceId());

              uniqueDelayQueueManager.offerMessage(
                      new TraceInfo(traceID,ifuser , projectName,
                              iflicensekey));
            }
          }
        }


        // Respond to the client
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("OK");
      } catch (Exception e) {
        log.error("Error processing OTLP HTTP request", e);
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }
  }
}
