package com.insightfinder.models.misc;

import java.util.regex.Pattern;

public class IFRegexPattern {

  /**
   * Generate the regeX Pattern with multi line and ignore line flag if the multiLineFlag is true
   *
   * @param regex
   * @param multiLineFlag
   * @return
   */
  public static Pattern getPattern(String regex, boolean multiLineFlag) {
    if (multiLineFlag) {
      return Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
    }
    return Pattern.compile(regex);
  }
}
