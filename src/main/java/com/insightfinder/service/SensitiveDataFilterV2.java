package com.insightfinder.service;

import com.insightfinder.config.Config;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.ResourceSpans;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import io.opentelemetry.proto.trace.v1.Span;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveDataFilterV2 {
  private static final Config config = Config.getInstance();
  private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SensitiveDataFilterV2.class);

  // Simple keyword patterns matched against attribute key names only.
  // Separate from the full value-masking patterns (which require keyword+value context).
  private static final List<Pattern> SENSITIVE_KEY_PATTERNS = Arrays.asList(
      Pattern.compile("(?i)ssn|social.?security"),
      Pattern.compile("(?i)password|passwd|secret"),
      Pattern.compile("(?i)dob|date.?of.?birth|birthdate"),
      Pattern.compile("(?i)nino|national.?insurance"),
      Pattern.compile("(?i)\\bsin\\b|social.?insurance"),
      Pattern.compile("(?i)mrn|medical.?record"),
      Pattern.compile("(?i)credit.?card|ccnum|cvv"),
      Pattern.compile("(?i)passport"),
      Pattern.compile("(?i)bank.?account|routing|\\biban\\b"),
      Pattern.compile("(?i)\\bphone\\b|telephone|mobile"),
      Pattern.compile("(?i)\\bemail\\b"),
      Pattern.compile("(?i)\\baddress\\b|zipcode|postal"),
      Pattern.compile("(?i)\\bvin\\b|vehicle.?id"),
      Pattern.compile("(?i)blood.?type"),
      Pattern.compile("(?i)voter.?id"),
      Pattern.compile("(?i)student.?id|student.?num"),
      Pattern.compile("(?i)coordinates|latitude|longitude"),
      Pattern.compile("(?i)member.?id|subscriber|policy"),
      Pattern.compile("(?i)prescription|\\brx\\b"),
      Pattern.compile("(?i)diagnosis|\\bicd\\b|\\bcpt\\b"),
      Pattern.compile("(?i)ethnicity|tribe|clan")
  );

  static {
    LOG.info("SensitiveDataFilterV2 initialized with {} pattern(s), enabled={}",
        config.getSensitiveDataRegex().size(), config.isSensitiveDataFilterEnabled());
  }

  public static ExportTraceServiceRequest deepSanitizeRequest(ExportTraceServiceRequest request) {
    if (!config.isSensitiveDataFilterEnabled()) return request;
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
    for (Pattern p : SENSITIVE_KEY_PATTERNS) {
      if (p.matcher(key).find()) return true;
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

  // When the pattern has capture groups, replaces only the captured portions (preserving
  // keyword prefixes). When there are no capture groups, replaces the full match.
  private static String maskSensitiveValue(String text, String rawPattern, String replacement) {
    try {
      Pattern p = Pattern.compile(rawPattern);
      Matcher m = p.matcher(text);
      if (p.matcher("").groupCount() > 0) {
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
          List<int[]> spans = new ArrayList<>();
          for (int i = 1; i <= m.groupCount(); i++) {
            if (m.group(i) != null) {
              spans.add(new int[]{m.start(i) - m.start(), m.end(i) - m.start()});
            }
          }
          spans.sort((a, b) -> b[0] - a[0]);
          StringBuilder masked = new StringBuilder(m.group());
          for (int[] span : spans) {
            masked.replace(span[0], span[1], "*".repeat(span[1] - span[0]));
          }
          m.appendReplacement(sb, Matcher.quoteReplacement(masked.toString()));
        }
        m.appendTail(sb);
        return sb.toString();
      }
      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        m.appendReplacement(sb, Matcher.quoteReplacement("*".repeat(m.group().length())));
      }
      m.appendTail(sb);
      return sb.toString();
    } catch (Exception e) {
      LOG.warn("Pattern '{}' failed during masking, field left unmasked: {}", rawPattern, e.getMessage());
    }
    return text;
  }

}
