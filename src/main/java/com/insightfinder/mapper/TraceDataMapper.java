package com.insightfinder.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.TraceDataBody;
import com.insightfinder.util.ParseUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceDataMapper {

  private static TraceDataMapper instance;

  private TraceDataMapper() {
  }

  public static TraceDataMapper getInstance() {
    if (instance == null) {
      instance = new TraceDataMapper();
    }
    return instance;
  }

  public TraceDataBody fromRawJaegerData(JSONObject rawJaegerData, TraceInfo traceInfo) {
    try {
      if (rawJaegerData == null || rawJaegerData.isEmpty()) {
        return null;
      }
      var traceDataBody = new TraceDataBody();
      var rawData = rawJaegerData.getJSONArray("data");
      if (rawData == null || rawData.isEmpty()) {
        return null;
      }
      var rawTrace = rawData.getJSONObject(0);
      var rawSpans = rawTrace.getJSONArray("spans");

      for (int i = 0; i < rawSpans.size(); i++) {
        var curSpan = rawSpans.getJSONObject(i);

        // Set some properties based on first and last span
        if (i == 0) {
          traceDataBody.setStartTime(curSpan.getLong("startTime"));
        }
        if (i == rawSpans.size() - 1) {
          traceDataBody.setEndTime(curSpan.getLong("startTime") + curSpan.getLong("duration"));
          traceDataBody.setDuration(traceDataBody.getEndTime() - traceDataBody.getStartTime());
        }

        SpanDataBody spanBody = getSpanDataBody(curSpan);
        if (spanBody != null) {
          traceDataBody.addSpan(spanBody);
        }
      }
      var processes = rawTrace.getJSONObject("processes");
      traceDataBody.setProcesses(processes);
      traceDataBody.setInstanceName(processes.getJSONObject("p1").getString("serviceName"));
      traceDataBody.setTraceID(traceInfo.getTraceId());
      return traceDataBody;
    } catch (Exception e) {
      log.error("Error mapping Jaeger raw data to IF trace: {}", e.getMessage());
      return null;
    }
  }

  private SpanDataBody getSpanDataBody(JSONObject rawSpanData) {
    if (rawSpanData == null || rawSpanData.isEmpty()) {
      return null;
    }

    var spanDataBodyBuilder = SpanDataBody.builder()
        .spanID(rawSpanData.getString("spanID"))
        .traceID(rawSpanData.getString("traceID"))
        .operationName(rawSpanData.getString("operationName"))
        .startTime(rawSpanData.getLong("startTime"))
        .duration(rawSpanData.getLong("duration"))
        .attributes(ParseUtil.getAttrMapFromJsonArray(rawSpanData.getJSONArray("tags")));

    // Save the span to correct parent span in the Trace.
    var references = rawSpanData.getJSONArray("references");
    if (!references.isEmpty()) {
      var reference = references.getJSONObject(0);
      if (reference.getString("refType").equals("CHILD_OF")) {
        spanDataBodyBuilder.parentSpanId(reference.getString("spanID"));
      } else {
        spanDataBodyBuilder.parentSpanId("");
      }
    }
    return spanDataBodyBuilder.build();
  }
}
