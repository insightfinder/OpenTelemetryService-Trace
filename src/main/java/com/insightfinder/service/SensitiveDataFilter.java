package com.insightfinder.service;

import com.insightfinder.config.Config;
import com.insightfinder.config.model.SensitiveDataConfig;
import com.insightfinder.util.regex.JsonStructure;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensitiveDataFilter {

  private static final Logger LOG = LoggerFactory.getLogger(SensitiveDataFilter.class);

  private final boolean enabled;
  private final String replacement;
  private final List<JsonStructure> jsonRules = new ArrayList<>();
  private final List<Pattern> regexRules = new ArrayList<>();

  public SensitiveDataFilter(SensitiveDataConfig cfg) {
    if (cfg == null) {
      this.enabled = false;
      this.replacement = "";
      return;
    }
    this.enabled = cfg.isSensitiveDataFilterEnabled();
    this.replacement = cfg.getReplacement() == null ? "" : cfg.getReplacement();

    List<String> rules = cfg.getSensitiveDataRegex();
    if (rules == null) {
      return;
    }
    for (String r : rules) {
      if (r == null) {
        continue;
      }
      String rule = r.trim();
      if (rule.isEmpty()) {
        continue;
      }
      if (rule.contains("->") && rule.contains("=")) {
        try {
          jsonRules.add(new JsonStructure(rule, true, false));
        } catch (PatternSyntaxException e) {
          LOG.error("Invalid JSON rule regex in '{}': {}", rule, e.getMessage());
        } catch (Exception e) {
          LOG.error("Failed to add JSON rule '{}': {}", rule, e.toString());
        }
      } else {
        try {
          regexRules.add(Pattern.compile(rule));
        } catch (PatternSyntaxException e) {
          LOG.error("Invalid regex rule '{}': {}", rule, e.getMessage());
        }
      }
    }
  }

  public static SensitiveDataFilter getInstance() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    private static final SensitiveDataFilter INSTANCE = create();

    private static SensitiveDataFilter create() {
      Config cfg = Config.getInstance();
      SensitiveDataConfig sdCfg = (cfg != null) ? cfg.getSensitiveDataConfig() : null;
      return new SensitiveDataFilter(sdCfg);
    }
  }

  public String filterString(String content) {
    if (!enabled || content == null || content.isEmpty()) {
      return content;
    }
    String result = content;

    for (JsonStructure js : jsonRules) {
      try {
        result = js.removeFromJsonString(result);
      } catch (Exception ignore) {
        // best-effort; not JSON or rule failed
      }
    }

    for (Pattern p : regexRules) {
      result = maskSensitiveValue(result, p.pattern(), replacement);
    }
    return result;
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
            masked.replace(span[0], span[1], replacement);
          }
          m.appendReplacement(sb, Matcher.quoteReplacement(masked.toString()));
        }
        m.appendTail(sb);
        return sb.toString();
      }
      StringBuffer sb = new StringBuffer();
      while (m.find()) {
        m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
      }
      m.appendTail(sb);
      return sb.toString();
    } catch (Exception ignored) {}
    return text;
  }

  public static String filterString(String content, SensitiveDataFilter filter) {
    if (filter == null) {
      return content;
    }
    return filter.filterString(content);
  }
}