package com.insightfinder.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.TraceDataBody;
import com.insightfinder.util.ParseUtil;
import java.util.HashMap;
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
        traceDataBody.startTime = curSpan.getLong("startTime");
      }
      if (i == rawSpans.size() - 1) {
        traceDataBody.endTime = curSpan.getLong("startTime") + curSpan.getLong("duration");
        traceDataBody.duration = traceDataBody.endTime - traceDataBody.startTime;
      }

      SpanDataBody spanBody = getSpanDataBody(curSpan);
      if (spanBody != null) {
        traceDataBody.addSpan(spanBody);
      }
    }
    var processes = rawTrace.getJSONObject("processes");
    traceDataBody.processes = processes;
    traceDataBody.serviceName = processes.getJSONObject("p1").getString("serviceName");
    traceDataBody.traceID = traceInfo.traceId;
    return traceDataBody;
  }

  private SpanDataBody getSpanDataBody(JSONObject rawSpanData) {
    if (rawSpanData == null || rawSpanData.isEmpty()) {
      return null;
    }
    var spanBody = new SpanDataBody();
    spanBody.spanID = rawSpanData.getString("spanID");
    spanBody.traceID = rawSpanData.getString("traceID");
    spanBody.operationName = rawSpanData.getString("operationName");
    spanBody.startTime = rawSpanData.getLong("startTime");
    spanBody.duration = rawSpanData.getLong("duration");
    spanBody.childSpans = new HashMap<>();
    spanBody.attributes = ParseUtil.getAttrMapFromJsonArray(rawSpanData.getJSONArray("tags"));
    // Save the span to correct parent span in the Trace.
    var references = rawSpanData.getJSONArray("references");
    if (!references.isEmpty()) {
      var reference = references.getJSONObject(0);
      if (reference.getString("refType").equals("CHILD_OF")) {
        spanBody.parentSpanId = reference.getString("spanID");
      } else {
        spanBody.parentSpanId = "";
      }
    }
    return spanBody;
  }
}
