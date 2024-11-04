# OpenTelemetryService-Trace

OpenTelemetry OTLP service that accept Trace data and Send to InsightFinder.

# Prerequisites

1. Java JRE 21 or higher
2. OpenTelemetry Collector

## Setup OpenTelemetry Collector

### Installation

InsightFinder requires a “OpenTelemetry Collector” to be set up to stream data.

1. Download the OpenTelemetry release from GitHub release page.
2. Extract the downloaded tarball:

```bash
tar -xvzf otelcol-<platform>-amd64.tar.gz
```

3. Run the collector with the configuration file:

```bash
./otelcol --config=config.yaml
```

### Configuration

Here is the example of an OpenTelemetry collector that collects data and send to InsightFinder
server:

```yaml
receivers:
  otlp:
    protocols:
      http:
        endpoint: 0.0.0.0:4418
processors:
  batch:
exporters:
  otlp/insightfinder:
    endpoint: 127.0.0.1:4317
    tls:
      insecure: true
    headers:
      ifuser: "user"
      iflicenseKey: "your-licence-key"
      ifproject: "project-to-send-trace-data"
service:
  telemetry:
    metrics:
      address: 0.0.0.0:8889
  pipelines:
    traces:
      receivers: [ otlp ]
      processors: [ batch ]
      exporters: [ otlp/insightfinder ]
```

### Configuration Explanation

- `receivers.otlp.protocols.http.endpoint`: The endpoint on which the OpenTelemetry Collector will
  listen for trace data.
- `exporters` A list of exporters the collector can use.
    - `otlp/insightfinder`: The exporter name. Exporter name should always start with `otlp/`.
        - `endpoint`: The trace agent endpoint.
        - `tls.insecure`: A boolean indicating whether to use insecure connection.
        - `headers.ifuser`: The InsightFinder username.
        - `headers.iflicenseKey`: The InsightFinder license key.
        - `headers.ifproject`: The InsightFinder project name.
- `pipelines.traces.exporters`: Specifies the exporters to use for traces. The exporter names should
  match the names in the `exporters` section.

# Run Trace Agent

1. Setup JDK 21 or above.
2. Start OpenTelemetry Collector
3. Put application.yml in the same directory as the jar file.
4. Run the following command to start the application.

```bash
java -jar <path>/traceserver-1.0.0-SNAPSHOT.jar
```

# Configuration File Explanation

The name of the configuration file must be `application.yml`.

## Configuration File Example

```yaml
grpc:
  port: 4317
  maxInboundMessageSizeInKB: 16384

insightFinder:
  serverUrl: https://stg.insightfinder.com
  serverUri: /api/v1/customprojectrawdata
  checkAndCreateUri: /api/v1/check-and-add-custom-project

jaeger:
  serverName: localhost
  tlsEnabled: false
  grpcPort: 4317
  uiPort: 16686

app:
  traceWorkerNum: 5
  traceDelayInMillis: 60000
  tls:
    enabled: false
    # Path to full-chain certificate file (PEM)
    certificateFile: ""
    # Path to the private key file.
    privateKeyFile: ""

data:
  attrMapping: # expect the field is in tags
    prompt_response:
      fieldPaths:
        - "tags.recommendations prompt"
    prompt_token:
      fieldPaths:
        - "tags.prompt_token_usage"
        - "tags.token_usage"
    response_token:
      fieldPaths:
        - "tags.output_token_usage"
    error:
      fieldPaths:
        - "tags.error_messages"
```

### `grpc`

This section configures the trace agent gRPC server settings.

- `port`: The port on which the trace agent gRPC server will listen.
- `maxInboundMessageSizeInKB`: The maximum size of inbound messages in kilobytes, default is
  16,384 (16 MB).

### `insightFinder`

This section contains the configuration for connecting to InsightFinder.

- `serverUrl`: The InsightFinder domain URL.
- `serverUri`: The InsightFinder endpoint to receive trace data.
- `checkAndCreateUri`: The InsightFinder endpoint for checking and creating custom projects.

### `jaeger`

This section configures the Jaeger server settings.

- `serverName`: The server domain or IP address of the Jaeger server.
- `tlsEnabled`: A boolean indicating whether TLS is enabled.
- `grpcPort`: The gRPC port the Jaeger server listens.
- `uiPort`: The UI port for Jaeger.

### `app`

This section contains trace-agent-specific settings.

- `traceWorkerNum`: The number of trace workers (threads).
- `traceDelayInMillis`: The time (in milliseconds) agent will wait to collect all spans of a trace
  before sending them to InsightFinder.
- `tls`: Configuration for trace agent TLS settings.
    - `enabled`: A boolean indicating whether TLS is enabled.
    - `certificateFile`: Path to the full-chain certificate file (PEM).
    - `privateKeyFile`: Path to the private key file.

### `data`

This section configures data attribute mappings. All the field paths are expected to be in the
`tags`. If multiple paths are provided, the first one that has value will be used.

- `attrMapping.prompt_response.fieldPaths`: JSON fields to extract prompt response.
- `attrMapping.prompt_token.fieldPaths`: JSON fields to extract prompt token. The value should be
  either String or Integer.
- `attrMapping.response_token.fieldPaths`: JSON fields to extract response token. The value should
  be either String or Integer.
- `attrMapping.error.fieldPaths`: JSON fields to extract error messages. The value should be either
  Boolean, JSONObject or JSONArray.
