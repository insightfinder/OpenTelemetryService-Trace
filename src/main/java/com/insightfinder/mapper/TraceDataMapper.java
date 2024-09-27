package com.insightfinder.mapper;

import static com.insightfinder.util.ParseUtil.fromMicroSecondToMillis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.config.Config;
import com.insightfinder.config.model.ValueMapping;
import com.insightfinder.model.ValueType;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.SpanDataBody.SpanDataBodyBuilder;
import com.insightfinder.model.request.TraceDataBody;
import com.insightfinder.util.ParseUtil;
import io.opentelemetry.api.internal.StringUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceDataMapper {

  private static TraceDataMapper instance;
  private static final Map<String, ValueMapping> valueMappings = Config.getInstance()
      .getAttrMapping();

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
          long startTime = fromMicroSecondToMillis(curSpan.getLong("startTime"));
          traceDataBody.setStartTime(startTime);
        }
        if (i == rawSpans.size() - 1) {
          long endTime = fromMicroSecondToMillis(
              curSpan.getLong("startTime") + curSpan.getLong("duration"));
          traceDataBody.setEndTime(endTime);
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

      traceDataBody.composeSpanRelations();

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

    var rawAttributes = rawSpanData.getJSONArray("tags");
    var attributes = ParseUtil.getAttrMapFromJsonArray(rawAttributes);
    var spanDataBodyBuilder = SpanDataBody.builder()
        .spanID(rawSpanData.getString("spanID"))
        .traceID(rawSpanData.getString("traceID"))
        .operationName(rawSpanData.getString("operationName"))
        .startTime(fromMicroSecondToMillis(rawSpanData.getLong("startTime")))
        .duration(fromMicroSecondToMillis(rawSpanData.getLong("duration")))
        .attributes(attributes);

    try {
      extractErrorFlag(attributes, spanDataBodyBuilder);
    } catch (Exception e) {
      log.error("Error parsing error messages: {}", e.getMessage());
      spanDataBodyBuilder.error(false);
    }

    try {
      var promptResponseValueMapping = valueMappings.get("prompt_response");
      var prompt = ParseUtil.getValueInAttrByPath(promptResponseValueMapping, attributes);
      if (prompt != null) {
        attributes.put("prompt_response", prompt);
      }
    } catch (Exception e) {
      log.error("Error parsing prompt_response: {}", e.getMessage());
    }

    try {
      extractTotalTokens(attributes);
    } catch (Exception e) {
      log.error("Error parsing total_tokens: {}", e.getMessage());
    }

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

  private void extractTotalTokens(Map<String, Object> attributes) {
    var totalTokenValueMapping = valueMappings.get("total_tokens");
    if (totalTokenValueMapping == null) {
      return;
    }
    var totalToken = ParseUtil.getValueInAttrByPath(totalTokenValueMapping, attributes);
    if (totalToken != null) {
      if (totalTokenValueMapping.getRawDataType().equals(ValueType.STRING)) {
        var totalTokens = Integer.parseInt((String) totalToken);
        attributes.put("total_tokens", totalTokens);
      } else if (totalTokenValueMapping.getRawDataType().equals(ValueType.INT)) {
        var totalTokens = (Integer) totalToken;
        attributes.put("total_tokens", totalTokens);
      }
    }
  }

  private void extractErrorFlag(Map<String, Object> attributes,
      SpanDataBodyBuilder spanDataBodyBuilder) {
    var errorFieldValeMapping = valueMappings.get("error");
    if (errorFieldValeMapping == null) {
      return;
    }
    var valueType = errorFieldValeMapping.getRawDataType();
    var error = ParseUtil.getValueInAttrByPath(errorFieldValeMapping,
        attributes);
    if (error != null) {
      if (valueType.equals(ValueType.STRING)) {
        var errorMessages = (String) error;
        try {
          var errorMessageList = JSON.parseArray(errorMessages, String.class);
          var hasErrorMessage = errorMessageList != null && !errorMessageList.isEmpty();
          spanDataBodyBuilder.error(hasErrorMessage);
        } catch (JSONException e) {
          var hasErrorMessage = StringUtils.isNullOrEmpty(errorMessages);
          spanDataBodyBuilder.error(hasErrorMessage);
        }
      } else if (valueType.equals(ValueType.BOOLEAN)) {
        spanDataBodyBuilder.error((Boolean) error);
      }
    }
  }
}