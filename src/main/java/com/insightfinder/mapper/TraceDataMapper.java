package com.insightfinder.mapper;

import static com.insightfinder.util.ParseUtil.fromMicroSecondToMillis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.config.Config;
import com.insightfinder.config.model.PromptConfig;
import com.insightfinder.config.model.ValueMapping;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.model.request.ContentData;
import com.insightfinder.model.request.InputPrompt;
import com.insightfinder.model.request.ResponseRecord;
import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.SpanDataBody.SpanDataBodyBuilder;
import com.insightfinder.model.request.TraceDataBody;
import com.insightfinder.util.ParseUtil;
import com.insightfinder.util.TokenizerUtil;
import io.opentelemetry.api.internal.StringUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceDataMapper {

  private static TraceDataMapper instance;
  private static final Map<String, ValueMapping> valueMappings = Config.getInstance()
      .getAttrMapping();
  private static final Config config = Config.getInstance();
  private static final String LLM_JUDGE_URI = "/llm-as-judge/";

  private TraceDataMapper() {
  }

  public static TraceDataMapper getInstance() {
    if (instance == null) {
      instance = new TraceDataMapper();
    }
    return instance;
  }

  public com.insightfinder.mapper.TraceInfo fromRawJaegerData(JSONObject rawJaegerData,
      TraceInfo traceInfo) {
    try {
      if (rawJaegerData == null || rawJaegerData.isEmpty()) {
        log.info("Empty Jaeger raw data. Unable to extract prompt / response data.");
        return null;
      }
      var traceDataBody = new TraceDataBody();
      var rawData = rawJaegerData.getJSONArray("data");
      if (rawData == null || rawData.isEmpty()) {
        return null;
      }
      var rawTrace = rawData.getJSONObject(0);
      var rawSpans = rawTrace.getJSONArray("spans");
      Map<String, ContentData> promptPairs = new HashMap<>();
      String entryOperation = rawSpans.stream()
          .min(Comparator.comparing(o -> ((JSONObject) o).getLong("startTime")))
          .map(o -> ((JSONObject) o).getString("operationName").trim()).orElse(null);
      if (!StringUtils.isNullOrEmpty(entryOperation) && entryOperation.contains(LLM_JUDGE_URI)) {
        return null;
      }
      String username = null;
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

        SpanInfo spanInfo = getSpanDataBody(curSpan);
        if (spanInfo != null) {
          var spanDataBody = spanInfo.getSpanDataBody();
          if (spanDataBody != null) {
            traceDataBody.addSpan(spanDataBody);
            String spanUsername = spanInfo.getUsername();
            if (!StringUtils.isNullOrEmpty(spanUsername)) {
              if (StringUtils.isNullOrEmpty(username)) {
                username = spanUsername;
              }
            }
            var promptPair = spanInfo.getContentData();
            if (promptPair != null && !promptPair.isEmpty()) {
              promptPair.setTraceId(traceInfo.getTraceId());
              var key = promptPair.getPromptHash();
              if (key != null) {
                if (promptPairs.containsKey(key)) {
                  var existingPromptPair = promptPairs.get(key);
                  var existingPromptDuration = existingPromptPair.getInputPrompt().getDuration();
                  var newPromptDuration = promptPair.getInputPrompt().getDuration();
                  if (newPromptDuration > existingPromptDuration) {
                    promptPairs.put(key, promptPair);
                  }
                } else {
                  promptPairs.put(key, promptPair);
                }
              }
            }
          }
        }
      }
      var processes = rawTrace.getJSONObject("processes");
      traceDataBody.setProcesses(processes);
      traceDataBody.setInstanceName(processes.getJSONObject("p1").getString("serviceName"));
      traceDataBody.setTraceID(traceInfo.getTraceId());
      traceDataBody.setUsername(username);

      traceDataBody.composeSpanRelations();

      promptPairs.values()
          .forEach(promptPair -> {
            promptPair.setInstanceName(traceDataBody.getInstanceName());
            promptPair.setUsername(traceDataBody.getUsername());
            promptPair.setEntryOperation(entryOperation);
          });

      return com.insightfinder.mapper.TraceInfo.builder()
          .promptResponsePairs(promptPairs.values().stream().toList())
          .traceDataBody(traceDataBody)
          .build();
    } catch (Exception e) {
      log.error("Error mapping Jaeger raw data to IF trace: {}", e.getMessage());
      return null;
    }
  }

  private SpanInfo getSpanDataBody(JSONObject rawSpanData) {
    if (rawSpanData == null || rawSpanData.isEmpty()) {
      return null;
    }

    var rawAttributes = rawSpanData.getJSONArray("tags");
    var attributes = ParseUtil.getAttrMapFromJsonArray(rawAttributes);

    // Remove sensitive attributes
    removeSensitiveAttributes(attributes);

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

    String username = null;
    try {
      username = extractUsername(attributes);
    } catch (Exception e) {
      log.error("Error parsing username: {}", e.getMessage());
    }

    try {
      if (!config.useCustomTokenizer()) {
        extractTokens(attributes);
      }
    } catch (Exception e) {
      log.error("Error parsing total_tokens: {}", e.getMessage());
    }

    ContentData contentData = null;
    try {
      contentData = extractPromptPair(attributes);
    } catch (Exception e) {
      log.error("Error parsing prompt: {}", e.getMessage());
    }

    try {
      extractUnsuccessfulResponseFlag(attributes);
    } catch (Exception e) {
      log.error("Error parsing unsuccessful_response: {}", e.getMessage());
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

    SpanDataBody spanDataBody = spanDataBodyBuilder.build();
    if (contentData != null) {
      contentData.setSpanId(spanDataBody.getSpanID());
      contentData.setDuration(spanDataBody.getDuration());
      contentData.setStartTime(spanDataBody.getStartTime());
    }
    SpanInfo.SpanInfoBuilder spanInfoBuilder = SpanInfo.builder()
        .spanDataBody(spanDataBody)
        .contentData(contentData)
        .username(username);
    return spanInfoBuilder.build();
  }

  private void extractTokens(Map<String, Object> attributes) {
    var promptTokenMapping = valueMappings.get("prompt_token");
    var responseTokenMapping = valueMappings.get("response_token");
    Integer totalTokens = null;
    Integer promptTokens = null;
    Integer responseTokens = null;
    if (promptTokenMapping != null) {
      var promptToken = ParseUtil.getValueInAttrByPath(promptTokenMapping, attributes);
      if (promptToken != null) {
        totalTokens = 0;
        if (promptToken instanceof String) {
          promptTokens = Integer.parseInt((String) promptToken);
        } else if (promptToken instanceof Integer) {
          promptTokens = (Integer) promptToken;
        }
        totalTokens += promptTokens;
      }
    }
    if (responseTokenMapping != null) {
      var responseToken = ParseUtil.getValueInAttrByPath(responseTokenMapping, attributes);
      if (responseToken != null) {
        if (totalTokens == null) {
          totalTokens = 0;
        }
        if (responseToken instanceof String) {
          responseTokens = Integer.parseInt((String) responseToken);
        } else if (responseToken instanceof Integer) {
          responseTokens = (Integer) responseToken;
        }
        totalTokens += responseTokens;
      }
    }
    if (totalTokens != null) {
      attributes.put("total_tokens", totalTokens);
    }
    if (promptTokens != null) {
      attributes.put("prompt_tokens", promptTokens);
    }
    if (responseTokens != null) {
      attributes.put("response_tokens", responseTokens);
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

  private String extractUsername(Map<String, Object> attributes) {
    var usernameFieldValeMapping = valueMappings.get("username");
    if (usernameFieldValeMapping == null) {
      log.info("No username message path provided");
      return null;
    }
    var username = ParseUtil.getValueInAttrByPath(usernameFieldValeMapping, attributes);
    if (username == null) {
      return null;
    } else {
      return (String) username;
    }
  }

  private ContentData extractPromptPair(Map<String, Object> attributes) {
    var promptExtractionConfig = Config.getInstance().getPromptExtraction();
    var processPath = promptExtractionConfig.getProcessPath();
    var processNames = promptExtractionConfig.getProcessNames();
    var processValueMapping = new ValueMapping(List.of(processPath));
    var process = (String) ParseUtil.getValueInAttrByPath(processValueMapping, attributes);
    if (process == null || !processNames.contains(process)) {
      log.warn("Process mapping failed for process.");
      return null;
    }
    var inputPromptMapping = promptExtractionConfig.getPromptConfig().get("input_prompt");
    var outputPromptMapping = promptExtractionConfig.getPromptConfig().get("output_prompt");
    var inputPrompt = extractPrompt(inputPromptMapping, attributes);
    var outputPrompt = extractPrompt(outputPromptMapping, attributes);
    if (!StringUtils.isNullOrEmpty(inputPrompt) && !StringUtils.isNullOrEmpty(outputPrompt)) {
      if (config.useCustomTokenizer()) {
        int promptTokens = TokenizerUtil.splitByWhiteSpaceTokenizer(inputPrompt);
        int responseTokens = TokenizerUtil.splitByWhiteSpaceTokenizer(outputPrompt);
        int totalTokens = promptTokens + responseTokens;
        attributes.put("total_tokens", totalTokens);
        attributes.put("prompt_tokens", promptTokens);
        attributes.put("response_tokens", responseTokens);
      }
      return ContentData.builder()
          .inputPrompt(new InputPrompt(inputPrompt))
          .responseRecord(new ResponseRecord(outputPrompt))
          .build();
    } else {
      return null;
    }
  }

  private String extractPrompt(PromptConfig promptConfig, Map<String, Object> attributes) {
    if (promptConfig == null) {
      return null;
    }
    var promptPath = promptConfig.getFieldPath();
    if (StringUtils.isNullOrEmpty(promptPath)) {
      return null;
    }
    var promptValueMapping = new ValueMapping(List.of(promptPath));
    var inputPromptValue = ParseUtil.getValueInAttrByPath(promptValueMapping, attributes);
    if (!(inputPromptValue instanceof String)) {
      return null;
    }
    if (!StringUtils.isNullOrEmpty(promptConfig.getValuePath())) {
      try {
        var promptJson = JSONObject.parse((String) inputPromptValue);
        return promptJson.getString(promptConfig.getValuePath());
      } catch (Exception e) {
        log.debug("Error parsing prompt JSON {}: {}", inputPromptValue, e.getMessage());
        return null;
      }
    } else {
      return (String) inputPromptValue;
    }
  }

  private void removeSensitiveAttributes(Map<String, Object> attributes) {
    attributes.remove("x-username");
    attributes.remove("x-licensekey");
    attributes.remove("x-trace-project");
  }


  private void extractUnsuccessfulResponseFlag(Map<String, Object> attributes) {
    var unsuccessResponseExtractionConfig = Config.getInstance()
        .getUnsuccessResponseExtractionConfig();
    var processPath = unsuccessResponseExtractionConfig.getProcessPath();
    var processNames = unsuccessResponseExtractionConfig.getProcessNames();
    var processValueMapping = new ValueMapping(List.of(processPath));
    var process = (String) ParseUtil.getValueInAttrByPath(processValueMapping, attributes);
    if (process == null || !processNames.contains(process)) {
      return;
    }
    var config = unsuccessResponseExtractionConfig.getConfig();
    if (config == null) {
      return;
    }
    var pathConfig = config.get("output_prompt");
    String path = pathConfig.getFieldPath();
    if (ParseUtil.pathExistInAttr(path, attributes)) {
      var responseValueMapping = new ValueMapping(List.of(path));
      var responseValue = ParseUtil.getValueInAttrByPath(responseValueMapping, attributes);
      if (responseValue instanceof String response) {
        attributes.put("unsuccessful_response",
            StringUtils.isNullOrEmpty(response) || response.equals("\"\""));
      }
    }
  }
}
