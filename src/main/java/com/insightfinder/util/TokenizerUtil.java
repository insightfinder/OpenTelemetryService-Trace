package com.insightfinder.util;

import io.opentelemetry.api.internal.StringUtils;

public class TokenizerUtil {

  public static int splitByWhiteSpaceTokenizer(String str) {
    if (StringUtils.isNullOrEmpty(str)) {
      return 0;
    }
    String[] tokens = str.split("\\s+");
    return tokens.length;
  }
}
