package com.insightfinder.service;

import com.insightfinder.config.Config;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;

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
      kvb.setValue(kv.getValue().toBuilder()
          .setStringValue(sanitize(kv.getValue().getStringValue()))
          .build());
    }
    return kvb.build();
  }

  private static String sanitize(String input) {
    if (input == null) return null;
    String sanitized = input;
    for (String pattern : config.getSensitiveDataRegex()) {
      sanitized = sanitized.replaceAll(pattern, "");
    }
    return sanitized;
  }

}
