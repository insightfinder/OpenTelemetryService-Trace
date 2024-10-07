package com.insightfinder.mapper;

import static com.insightfinder.util.ParseUtil.fromMicroSecondToMillis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.config.Config;
import com.insightfinder.config.model.ValueMapping;
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
      var spanError = rawSpanData.get("error");
      var attrError = attributes.get("error");
      if (spanError != null || attrError != null) {
        var error = spanError != null ? spanError : attrError;
        if (error instanceof String errorStr) {
          if (errorStr.equalsIgnoreCase("true")) {
            spanDataBodyBuilder.error(true);
          } else if (errorStr.equalsIgnoreCase("false")) {
            spanDataBodyBuilder.error(false);
          } else {
            spanDataBodyBuilder.error(!errorStr.isEmpty());
          }
        } else if (error instanceof Boolean) {
          spanDataBodyBuilder.error((Boolean) error);
        } else {
          log.info("unsupported span error type: {}", error.getClass().getName());
          spanDataBodyBuilder.error(false);
        }
      } else {
        extractErrorFlag(attributes, spanDataBodyBuilder);
      }
    } catch (Exception e) {
      log.error("Error parsing error messages: {}, span: {}", e.getMessage(), rawSpanData);
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
    var promptTokenMapping = valueMappings.get("prompt_token");
    var responseTokenMapping = valueMappings.get("response_token");
    Integer totalTokens = null;
    if (promptTokenMapping != null) {
      var promptToken = ParseUtil.getValueInAttrByPath(promptTokenMapping, attributes);
      if (promptToken != null) {
        totalTokens = 0;
        if (promptToken instanceof String) {
          var promptTokenNum = Integer.parseInt((String) promptToken);
          totalTokens += promptTokenNum;
        } else if (promptToken instanceof Integer) {
          totalTokens += (Integer) promptToken;
        }
      }
    }
    if (responseTokenMapping != null) {
      var responseToken = ParseUtil.getValueInAttrByPath(responseTokenMapping, attributes);
      if (responseToken != null) {
        if (totalTokens == null) {
          totalTokens = 0;
        }
        if (responseToken instanceof String) {
          var responseTokenNum = Integer.parseInt((String) responseToken);
          totalTokens += responseTokenNum;
        } else if (responseToken instanceof Integer) {
          totalTokens += (Integer) responseToken;
        }
      }
    }
    if (totalTokens != null) {
      attributes.put("total_tokens", totalTokens);
    }
  }

  private void extractErrorFlag(Map<String, Object> attributes,
      SpanDataBodyBuilder spanDataBodyBuilder) {
    var errorFieldValeMapping = valueMappings.get("error");
    if (errorFieldValeMapping == null) {
      log.info("No error message path provided");
      spanDataBodyBuilder.error(false);
      return;
    }
    var error = ParseUtil.getValueInAttrByPath(errorFieldValeMapping, attributes);
    if (error != null) {
      if (error instanceof String errorMessages) {
        try {
          var errorMessage = JSON.parse(errorMessages);
          if (errorMessage instanceof JSONObject errorMessageObj) {
            spanDataBodyBuilder.error(!errorMessageObj.isEmpty());
          } else if (errorMessage instanceof JSONArray errorMessageList) {
            spanDataBodyBuilder.error(!errorMessageList.isEmpty());
          } else {
            log.info("unsupported span attribute error type: {}", error);
            spanDataBodyBuilder.error(false);
          }
        } catch (JSONException e) {
          var hasErrorMessage = StringUtils.isNullOrEmpty(errorMessages);
          spanDataBodyBuilder.error(hasErrorMessage);
        }
      } else if (error instanceof Boolean) {
        spanDataBodyBuilder.error((Boolean) error);
      } else {
        log.info("unsupported span attribute error type: {}", error.getClass().getName());
        spanDataBodyBuilder.error(false);
      }
    } else {
      spanDataBodyBuilder.error(false);
    }
  }
}
