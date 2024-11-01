package com.insightfinder;

import com.insightfinder.config.Config;
import com.insightfinder.interceptor.GrpcServerInterceptor;
import com.insightfinder.service.GrpcTraceService;
import com.insightfinder.worker.TraceWorker;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.opentelemetry.api.internal.StringUtils;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

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
    Server server = serverBuilder.build();
    server.start();
    log.info("OTLP Trace Receiver started at port {}", config.getGrpcPort());

    server.awaitTermination();
  }
}
