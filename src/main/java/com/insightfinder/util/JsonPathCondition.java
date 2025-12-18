package com.insightfinder.util;

import java.util.regex.Pattern;

public class JsonPathCondition {
  private final String[] keys;
  private final Pattern valuePattern;

  public JsonPathCondition(String spec) {
    if (spec == null || spec.isEmpty()) {
      this.keys = null;
      this.valuePattern = null;
      return;
    }
    String[] parts = spec.split("=", 2);
    String path = parts[0];
    this.keys = path.split("->");
    this.valuePattern = (parts.length == 2 && !parts[1].isEmpty()) ? Pattern.compile(parts[1]) : null;
  }

  public boolean isConfigured() {
    return keys != null && keys.length > 0 && valuePattern != null;
  }

  public String[] keys() { return keys; }
  public Pattern valuePattern() { return valuePattern; }
}