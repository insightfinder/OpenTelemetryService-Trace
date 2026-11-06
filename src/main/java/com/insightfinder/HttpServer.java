package com.insightfinder;

import static com.insightfinder.util.Constants.METADATA_KEY;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.insightfinder.config.Config;
import com.insightfinder.model.ContextMetadata;
import com.insightfinder.service.TraceExportProcessor;
import com.insightfinder.util.ParseUtil;
import io.grpc.Context;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.CharsetUtil;
import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {

  private static final String TRACES_PATH = "/v1/traces";
  private static final String CONTENT_TYPE_PROTOBUF = "application/x-protobuf";
  private static final String CONTENT_TYPE_JSON = "application/json";

  private final Config config = Config.getInstance();
  private final TraceExportProcessor traceExportProcessor = TraceExportProcessor.getInstance();

  public void start() throws Exception {
    SslContext sslContext = buildSslContext();

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) {
            var pipeline = ch.pipeline();
            if (sslContext != null) {
              pipeline.addLast(sslContext.newHandler(ch.alloc()));
            }
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(config.getHttpMaxInboundMessageSizeInKB() * 1024));
            pipeline.addLast(new OtlpTraceHttpHandler());
          }
        });

    ChannelFuture channelFuture = bootstrap.bind(config.getHttpPort()).sync();
    log.info("OTLP HTTP Trace Receiver started at port {}", config.getHttpPort());
    channelFuture.channel().closeFuture().addListener(future -> {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    });
  }

  private SslContext buildSslContext() throws Exception {
    if (!config.isAppTlsEnabled()) {
      return null;
    }
    String appCertificateFilePath = config.getAppCertificateFile();
    String appPrivateKeyFilePath = config.getAppPrivateKeyFile();
    if (StringUtils.isNullOrEmpty(appCertificateFilePath) || StringUtils.isNullOrEmpty(
        appPrivateKeyFilePath)) {
      log.error("Certificate file or private key file is not provided.");
      System.exit(1);
    }
    File certChainFile = new File(appCertificateFilePath);
    File privateKeyFile = new File(appPrivateKeyFilePath);
    return SslContextBuilder.forServer(certChainFile, privateKeyFile).build();
  }

  private class OtlpTraceHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
      boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
      try {
        handle(ctx, httpRequest, keepAlive);
      } catch (Exception e) {
        log.error("Unexpected error handling OTLP HTTP trace request.", e);
        sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Internal server error", keepAlive);
      }
    }

    private void handle(ChannelHandlerContext ctx, FullHttpRequest httpRequest, boolean keepAlive) {
      String path = stripQuery(httpRequest.uri());
      if (!TRACES_PATH.equals(path)) {
        sendError(ctx, HttpResponseStatus.NOT_FOUND, "Not found", keepAlive);
        return;
      }
      if (!HttpMethod.POST.equals(httpRequest.method())) {
        sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, "Method not allowed", keepAlive);
        return;
      }

      String contentType = normalizeContentType(httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE));
      boolean isJson = CONTENT_TYPE_JSON.equals(contentType);
      boolean isProtobuf = CONTENT_TYPE_PROTOBUF.equals(contentType);
      if (!isJson && !isProtobuf) {
        sendError(ctx, HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE,
            "Unsupported content type. Use application/x-protobuf or application/json", keepAlive);
        return;
      }

      ExportTraceServiceRequest request;
      try {
        if (isProtobuf) {
          byte[] bytes = ByteBufUtil.getBytes(httpRequest.content());
          request = ExportTraceServiceRequest.parseFrom(bytes);
        } else {
          String json = httpRequest.content().toString(CharsetUtil.UTF_8);
          ExportTraceServiceRequest.Builder builder = ExportTraceServiceRequest.newBuilder();
          JsonFormat.parser().ignoringUnknownFields().merge(json, builder);
          request = builder.build();
        }
      } catch (Exception e) {
        sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Malformed request body: " + e.getMessage(), keepAlive);
        return;
      }

      String ifUser = ParseUtil.getIfUserFromHttpHeaders(httpRequest.headers());
      String ifProject = ParseUtil.getProjectFromHttpHeaders(httpRequest.headers());
      String ifSystem = ParseUtil.getSystemFromHttpHeaders(httpRequest.headers());
      String ifLicenseKey = ParseUtil.getLicenseKeyFromHttpHeaders(httpRequest.headers());

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

      ContextMetadata metadata = new ContextMetadata(ifUser, ifProject, ifSystem, ifLicenseKey);
      Context grpcContext = Context.current().withValue(METADATA_KEY, metadata);
      Context previous = grpcContext.attach();
      try {
        traceExportProcessor.process(request);
      } finally {
        grpcContext.detach(previous);
      }

      ExportTraceServiceResponse response = ExportTraceServiceResponse.newBuilder().build();
      if (isProtobuf) {
        sendProtobufResponse(ctx, HttpResponseStatus.OK, response.toByteArray(), keepAlive);
      } else {
        sendJsonResponse(ctx, HttpResponseStatus.OK, response, keepAlive);
      }
    }

    private String stripQuery(String uri) {
      int idx = uri.indexOf('?');
      return idx >= 0 ? uri.substring(0, idx) : uri;
    }

    private String normalizeContentType(String rawContentType) {
      if (rawContentType == null) {
        return null;
      }
      int idx = rawContentType.indexOf(';');
      String type = idx >= 0 ? rawContentType.substring(0, idx) : rawContentType;
      return type.trim().toLowerCase();
    }

    private void sendProtobufResponse(ChannelHandlerContext ctx, HttpResponseStatus status, byte[] body,
        boolean keepAlive) {
      FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
          Unpooled.wrappedBuffer(body));
      httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, CONTENT_TYPE_PROTOBUF);
      writeResponse(ctx, httpResponse, keepAlive);
    }

    private void sendJsonResponse(ChannelHandlerContext ctx, HttpResponseStatus status,
        ExportTraceServiceResponse response, boolean keepAlive) {
      String json;
      try {
        json = JsonFormat.printer().print(response);
      } catch (InvalidProtocolBufferException e) {
        sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Failed to encode response", keepAlive);
        return;
      }
      FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
          Unpooled.copiedBuffer(json, CharsetUtil.UTF_8));
      httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, CONTENT_TYPE_JSON);
      writeResponse(ctx, httpResponse, keepAlive);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String message,
        boolean keepAlive) {
      FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
          Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
      httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
      writeResponse(ctx, httpResponse, keepAlive);
    }

    private void writeResponse(ChannelHandlerContext ctx, FullHttpResponse httpResponse, boolean keepAlive) {
      httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
      HttpUtil.setKeepAlive(httpResponse, keepAlive);
      ChannelFuture future = ctx.writeAndFlush(httpResponse);
      if (!keepAlive) {
        future.addListener(ChannelFutureListener.CLOSE);
      }
    }
  }
}
