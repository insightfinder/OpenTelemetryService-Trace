package com.insightfinder;

import com.insightfinder.config.Config;
import com.insightfinder.model.message.Message;
import com.insightfinder.service.GrpcTraceService;
import com.insightfinder.service.UniqueDelayQueueService;
import com.insightfinder.worker.TraceWorker;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcServer {

  public static final Context.Key<Metadata> METADATA_KEY = Context.key("metadata");
  public static UniqueDelayQueueService<Message> queue = new UniqueDelayQueueService<>();
  public static final ConcurrentHashMap<String, Boolean> projectLocalCache = new ConcurrentHashMap<>();
  private static final Config config = Config.getInstance();


  public static void main(String[] args) throws Exception {
    // Start Workers
    int workerNum = config.getAppTraceWorkerNum();
    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < workerNum; i++) {
        executorService.submit(new TraceWorker(i));
        log.info("Worker {} started", i);

        // Services
        GrpcTraceService traceService = new GrpcTraceService();

        // Interception
        GrpcInterceptionService interceptor = new GrpcInterceptionService();

        log.info("Starting OTLP Trace Receiver...");
        Server server = ServerBuilder.forPort(config.getGrpcPort())
            .addService(traceService)
            .intercept(interceptor) // Add the interceptor to store metadata
            .maxInboundMessageSize(config.getGrpcMaxInboundMessageSizeInKB() * 1024)
            .build();
        server.start();
        log.info("OTLP Trace Receiver started at port {}", config.getGrpcPort());

        server.awaitTermination();
      }
    }
  }

  // Interception Service that stores metadata in the context
  static class GrpcInterceptionService implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
        Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
      Context context = Context.current().withValue(METADATA_KEY, metadata);
      return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }
  }
}
