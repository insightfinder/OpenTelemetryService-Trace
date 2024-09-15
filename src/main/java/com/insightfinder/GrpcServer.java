package com.insightfinder;
import com.insightfinder.model.message.Message;
import com.insightfinder.service.UniqueDelayQueueService;
import com.insightfinder.worker.TraceWorker;
import io.grpc.*;
import io.grpc.Context;
import org.slf4j.*;
import com.insightfinder.service.GrpcTraceService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GrpcServer{

  private static final Logger LOG = LoggerFactory.getLogger(GrpcServer.class);
  public static final Context.Key<Metadata> METADATA_KEY = Context.key("metadata");
  public static UniqueDelayQueueService<Message> queue = new UniqueDelayQueueService<>();
  public static final ConcurrentHashMap<String,Boolean> projectLocalCache = new ConcurrentHashMap<>();

  public static void main(String[] args) throws Exception {


    // Start Workers
    int workerNum = 1;
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    for(int i = 0; i < workerNum; i++){
      executorService.submit(new TraceWorker(i));
      LOG.info("Worker %d started.".formatted(i));
    }


    // Services
    GrpcTraceService traceService = new GrpcTraceService();

    // Interception
    GrpcInterceptionService interceptor = new GrpcInterceptionService();

    LOG.info("Starting OTLP Trace Receiver...");
    Server server = ServerBuilder.forPort(4317)
            .addService(traceService)
            .intercept(interceptor) // Add the interceptor to store metadata
            .maxInboundMessageSize(16 * 1024 * 1024)
            .build();
    server.start();
    LOG.info("OTLP Trace Receiver started at port 4317");

    server.awaitTermination();
  }

  // Interception Service that stores metadata in the context
  static class GrpcInterceptionService  implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
      Context context = Context.current().withValue(METADATA_KEY, metadata);
      return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }
  }
}
