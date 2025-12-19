// java
package com.insightfinder.service;

import com.insightfinder.config.Config;
import com.insightfinder.config.model.SensitiveDataConfig;
import com.insightfinder.util.regex.JsonStructure;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SensitiveDataFilter {

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
    for (String rule : rules) {
      if (rule == null || rule.isEmpty()) {
        continue;
      }
      // Heuristic: treat as JsonStructure spec if it contains both '->' and '='
      if (rule.contains("->") && rule.contains("=")) {
        // JsonStructure(String spec, boolean removeMatched, boolean multiline)
        // If your ctor differs, adjust accordingly.
        jsonRules.add(new JsonStructure(rule, true, false));
      } else {
        // Plain regex rule
        regexRules.add(Pattern.compile(rule));
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

    // Apply JSON rules first (best-effort; skip if not JSON)
    for (JsonStructure js : jsonRules) {
      try {
        result = js.removeFromJsonString(result);
      } catch (Exception ignore) {
        // not a JSON object string; continue
      }
    }

    // Apply plain regex replacements
    for (Pattern p : regexRules) {
      result = p.matcher(result).replaceAll(replacement);
    }
    return result;
  }

  public static String filterString(String content, SensitiveDataFilter filter) {
    if (filter == null) {
      return content;
    }
    return filter.filterString(content);
  }
}