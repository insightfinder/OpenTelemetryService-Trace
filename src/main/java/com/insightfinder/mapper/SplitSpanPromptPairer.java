package com.insightfinder.mapper;

import com.insightfinder.config.model.SplitSpanPromptConfig;
import com.insightfinder.model.request.ContentData;
import com.insightfinder.model.request.InputPrompt;
import com.insightfinder.model.request.ResponseRecord;
import com.insightfinder.util.ParseUtil;
import com.insightfinder.util.TokenizerUtil;
import io.opentelemetry.api.internal.StringUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Builds prompt/response pairs when the prompt and the response are on different spans of one
 * trace. Spans are sorted by the configured order attribute (then by start time). Each prompt span
 * is paired with the response spans after it, up to the next prompt span.
 */
@Slf4j
public final class SplitSpanPromptPairer {

  private static final String DEFAULT_SEPARATOR = "\n\n";

  enum ResponseMode {
    ALL_JOINED, FIRST, LAST
  }

  private SplitSpanPromptPairer() {
  }

  public static boolean isEnabled(SplitSpanPromptConfig config) {
    return config != null && config.isEnabled()
        && !StringUtils.isNullOrEmpty(config.getTypePath())
        && !StringUtils.isNullOrEmpty(config.getInputType())
        && !StringUtils.isNullOrEmpty(config.getOutputType())
        && !StringUtils.isNullOrEmpty(config.getInputField())
        && !StringUtils.isNullOrEmpty(config.getOutputField());
  }

  /**
   * Returns the prompt/response message on this span, or null when the span is neither a prompt
   * span nor a response span, or has no text.
   */
  public static SplitSpanMessage fromSpan(SplitSpanPromptConfig config,
      Map<String, Object> attributes, String spanId, long startTime, long duration,
      Integer tokenCountForInput, Integer tokenCountForOutput) {
    if (!isEnabled(config) || attributes == null) {
      return null;
    }
    Object type = ParseUtil.getValueInAttrByPath(config.getTypePath(), attributes);
    if (type == null) {
      return null;
    }
    String typeValue = type.toString();
    boolean input;
    String fieldPath;
    if (config.getInputType().equals(typeValue)) {
      input = true;
      fieldPath = config.getInputField();
    } else if (config.getOutputType().equals(typeValue)) {
      input = false;
      fieldPath = config.getOutputField();
    } else {
      return null;
    }
    Object content = ParseUtil.getValueInAttrByPath(fieldPath, attributes);
    if (!(content instanceof String text) || StringUtils.isNullOrEmpty(text)) {
      return null;
    }
    Long order = parseOrder(ParseUtil.getValueInAttrByPath(config.getOrderPath(), attributes));
    return new SplitSpanMessage(input, text, spanId, startTime, duration, order,
        input ? tokenCountForInput : tokenCountForOutput);
  }

  public static List<ContentData> pair(List<SplitSpanMessage> messages,
      SplitSpanPromptConfig config) {
    List<ContentData> pairs = new ArrayList<>();
    if (!isEnabled(config) || messages == null || messages.isEmpty()) {
      return pairs;
    }
    ResponseMode mode = parseMode(config.getResponseMode());
    String separator = config.getResponseSeparator() == null ? DEFAULT_SEPARATOR
        : config.getResponseSeparator();

    List<SplitSpanMessage> sorted = new ArrayList<>(messages);
    sorted.sort(Comparator
        .comparing(SplitSpanMessage::order, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparingLong(SplitSpanMessage::startTime));

    SplitSpanMessage currentInput = null;
    List<SplitSpanMessage> currentOutputs = new ArrayList<>();
    for (SplitSpanMessage message : sorted) {
      if (message.input()) {
        addPair(pairs, currentInput, currentOutputs, mode, separator);
        currentInput = message;
        currentOutputs = new ArrayList<>();
      } else if (currentInput != null) {
        currentOutputs.add(message);
      }
    }
    addPair(pairs, currentInput, currentOutputs, mode, separator);
    return pairs;
  }

  private static void addPair(List<ContentData> pairs, SplitSpanMessage input,
      List<SplitSpanMessage> outputs, ResponseMode mode, String separator) {
    if (input == null || outputs.isEmpty()) {
      return;
    }
    List<SplitSpanMessage> chosen = switch (mode) {
      case FIRST -> List.of(outputs.getFirst());
      case LAST -> List.of(outputs.getLast());
      case ALL_JOINED -> outputs;
    };
    SplitSpanMessage firstOut = chosen.getFirst();
    SplitSpanMessage lastOut = chosen.getLast();

    String responseText = chosen.stream().map(SplitSpanMessage::content)
        .collect(Collectors.joining(separator));

    int promptTokens = input.tokenCount() != null ? input.tokenCount()
        : TokenizerUtil.splitByWhiteSpaceTokenizer(input.content());
    boolean allOutputsHaveTokens = chosen.stream().allMatch(m -> m.tokenCount() != null);
    int responseTokens = allOutputsHaveTokens
        ? chosen.stream().mapToInt(SplitSpanMessage::tokenCount).sum()
        : TokenizerUtil.splitByWhiteSpaceTokenizer(responseText);

    InputPrompt inputPrompt = new InputPrompt(input.content(), promptTokens);
    inputPrompt.setSpanId(input.spanId());
    inputPrompt.setStartTime(input.startTime());
    inputPrompt.setDuration(input.duration());

    ResponseRecord responseRecord = new ResponseRecord(responseText, responseTokens);
    responseRecord.setSpanId(firstOut.spanId());
    responseRecord.setStartTime(firstOut.startTime());
    long responseEnd = lastOut.startTime() + lastOut.duration();
    responseRecord.setDuration(Math.max(0L, responseEnd - firstOut.startTime()));

    pairs.add(ContentData.builder()
        .inputPrompt(inputPrompt)
        .responseRecord(responseRecord)
        .build());
  }

  static ResponseMode parseMode(String raw) {
    if (StringUtils.isNullOrEmpty(raw)) {
      return ResponseMode.ALL_JOINED;
    }
    try {
      return ResponseMode.valueOf(raw.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      log.error("Unknown splitSpan.responseMode '{}', using ALL_JOINED", raw);
      return ResponseMode.ALL_JOINED;
    }
  }

  private static Long parseOrder(Object raw) {
    if (raw instanceof Number number) {
      return number.longValue();
    }
    if (raw instanceof String text && !text.isBlank()) {
      try {
        return Long.parseLong(text.trim());
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }
}
