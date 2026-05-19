package com.insightfinder.service;

import com.insightfinder.config.Config;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveDataFilterV2 {
  private static final Config config = Config.getInstance();

  public static ExportTraceServiceRequest deepSanitizeRequest(ExportTraceServiceRequest request) {
    ExportTraceServiceRequest.Builder reqBuilder = request.toBuilder();
    reqBuilder.clearResourceSpans();

    for (ResourceSpans rs : request.getResourceSpansList()) {
      ResourceSpans.Builder rsBuilder = rs.toBuilder();
      rsBuilder.clearScopeSpans();

      for (ScopeSpans ss : rs.getScopeSpansList()) {
        ScopeSpans.Builder ssBuilder = ss.toBuilder();
        ssBuilder.clearSpans();

        for (Span span : ss.getSpansList()) {
          ssBuilder.addSpans(sanitizeSpanStrings(span));
        }

        rsBuilder.addScopeSpans(ssBuilder.build());
      }

      reqBuilder.addResourceSpans(rsBuilder.build());
    }

    return reqBuilder.build();
  }

  private static Span sanitizeSpanStrings(Span span) {
    Span.Builder sb = span.toBuilder();

    // Sanitize span name
    sb.setName(sanitize(sb.getName()));

    // Sanitize attributes
    sb.clearAttributes();
    for (KeyValue kv : span.getAttributesList()) {
      sb.addAttributes(sanitizeKeyValue(kv));
    }

    // Sanitize event attributes
    sb.clearEvents();
    for (Span.Event e : span.getEventsList()) {
      Span.Event.Builder eb = e.toBuilder();
      eb.clearAttributes();
      for (KeyValue kv : e.getAttributesList()) {
        eb.addAttributes(sanitizeKeyValue(kv));
      }
      sb.addEvents(eb.build());
    }

    return sb.build();
  }

  private static KeyValue sanitizeKeyValue(KeyValue kv) {
    KeyValue.Builder kvb = kv.toBuilder();
    if (kv.getValue().getValueCase() == AnyValue.ValueCase.STRING_VALUE) {
      // When the attribute key itself matches a sensitive pattern, mask the entire value.
      // Otherwise apply text-based pattern matching to the value string.
      String value = isKeySensitive(kv.getKey())
          ? config.getReplacement()
          : sanitize(kv.getValue().getStringValue());
      kvb.setValue(kv.getValue().toBuilder().setStringValue(value).build());
    }
    return kvb.build();
  }

  private static boolean isKeySensitive(String key) {
    if (key == null || key.isEmpty()) return false;
    for (String pattern : config.getSensitiveDataRegex()) {
      try {
        if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(key).find()) {
          return true;
        }
      } catch (Exception ignored) {}
    }
    return false;
  }

  private static String sanitize(String input) {
    if (input == null) return null;
    String sanitized = input;
    for (String pattern : config.getSensitiveDataRegex()) {
      sanitized = maskSensitiveValue(sanitized, pattern, config.getReplacement());
    }
    return sanitized;
  }

  // Tries to match "keyword=value" or "keyword:value" and replace only the value part.
  // Falls back to replacing whatever the raw pattern matches.
  private static String maskSensitiveValue(String text, String rawPattern, String replacement) {
    try {
      Pattern withValue = Pattern.compile("(?i)(?:" + rawPattern + ")(\\s*[=:]\\s*)(\\S+)");
      Matcher m = withValue.matcher(text);
      if (m.find()) {
        StringBuffer sb = new StringBuffer();
        m.reset();
        while (m.find()) {
          int keywordEnd = m.start(1) - m.start();
          m.appendReplacement(sb, Matcher.quoteReplacement(
              m.group().substring(0, keywordEnd) + m.group(1) + replacement));
        }
        m.appendTail(sb);
        return sb.toString();
      }
    } catch (Exception ignored) {}
    return text.replaceAll(rawPattern, replacement);
  }

}
