package com.insightfinder.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.util.TextUtils;
import com.insightfinder.base.GlobalConstant;

public class RegexUtility {

  private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

  /**
   * Extract the numeric value from the string, if not available return null
   *
   * @param str
   * @param clazz
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number> T extractNumericString(String str, Class<T> clazz) {
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    String extractedValue = getMatchedString(str, NUMERIC_PATTERN);
    if (isStringNumeric(extractedValue)) {
      if (clazz == Integer.class) {
        return (T) Integer.valueOf(extractedValue);
      } else if (clazz == Long.class) {
        return (T) Long.valueOf(extractedValue);
      } else if (clazz == Double.class) {
        return (T) Double.valueOf(extractedValue);
      } else if (clazz == Float.class) {
        return (T) Float.valueOf(extractedValue);
      } else if (clazz == Short.class) {
        return (T) Short.valueOf(extractedValue);
      }
    }
    return null;
  }

  /**
   * Match a number with optional '-' and decimal.
   */
  public static boolean isStringNumeric(String str) {
    if (TextUtils.isEmpty(str)) {
      return false;
    }
    if (str.matches("-?\\d+(\\.\\d+)?")) {
      return true;
    }
    try {
      Double.valueOf(str);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean isSingleWord(String str) {
    if (TextUtils.isEmpty(str)) {
      return false;
    }
    if (str.matches("^[0-9a-zA-Z]+$")) {
      return true;
    }
    return false;
  }

  /**
   * Given a string and the regex expression find one matched substring
   *
   * @param string
   * @param pattern
   * @return
   */
  public static String getMatchedGroupedString(String string, Pattern pattern) {
    if (pattern == null) {
      return null;
    }
    Matcher matcher = pattern.matcher(string);
    while (matcher.find()) {
      String extractedValue = null;
      if (matcher.groupCount() == 1) {
        extractedValue = matcher.group(1);
      }
      if (TextUtils.isEmpty(extractedValue)) {
        extractedValue = matcher.group();
      }
      return extractedValue;
    }
    return GlobalConstant.EMPTY_STRING;
  }

  public static List<String> getMatchedGroups(String string, Pattern pattern) {
    List<String> matchedGroups = new ArrayList<>();
    if (pattern == null) {
      return matchedGroups;
    }
    Matcher matcher = pattern.matcher(string);
    while (matcher.find()) {
      for (int i = 1; i <= matcher.groupCount(); i++) {
        matchedGroups.add(matcher.group(i));
      }
    }
    return matchedGroups;
  }

  /**
   * Given a string and the regex expression find one matched substring
   *
   * @param string
   * @param pattern
   * @return
   */
  public static String getMatchedString(String string, Pattern pattern) {
    if (pattern == null) {
      return null;
    }
    Matcher matcher = pattern.matcher(string);
    while (matcher.find()) {
      return matcher.group();
    }
    return GlobalConstant.EMPTY_STRING;
  }

  /**
   * Given a String and the regex expression, find all matched substring
   *
   * @param string
   * @param regex
   * @return
   */
  public static List<String> getAllMatchedString(String string, String regex) {
    List<String> matchedStrings = new ArrayList<>();
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(string);
    while (matcher.find()) {
      String matchedString = matcher.group();
      matchedStrings.add(matchedString);
    }
    return matchedStrings;
  }

  public static List<String> getAllMatchedStringByPattern(String string, Pattern pattern) {
    List<String> matchedStrings = new ArrayList<>();
    Matcher matcher = pattern.matcher(string);
    while (matcher.find()) {
      String matchedString = matcher.group();
      if (matchedString.matches("\\s+") || TextUtils.isEmpty(matchedString)) {
        continue;
      }
      matchedStrings.add(matchedString);
    }
    return matchedStrings;
  }

  /**
   * Given a String and the regex expression, find a concatenated substring composed by all matched
   * substring
   *
   * @param string
   * @param regex
   * @return
   */
  public static String getAllMatchedConcatenatedString(String string, String regex) {
    StringBuilder concatenatedStringBuilder = new StringBuilder();
    List<String> matchedStringList = getAllMatchedString(string, regex);
    for (int i = 0; i < matchedStringList.size(); i++) {
      concatenatedStringBuilder.append(matchedStringList.get(i));
      if (i < matchedStringList.size() - 1) {
        concatenatedStringBuilder.append(GlobalConstant.SPACE);
      }
    }
    return concatenatedStringBuilder.toString();
  }

  public static String getAllMatchedConcatenatedStringByPattern(String string, Pattern pattern) {
    StringBuilder concatenatedStringBuilder = new StringBuilder();
    List<String> matchedStringList = getAllMatchedStringByPattern(string, pattern);
    for (int i = 0; i < matchedStringList.size(); i++) {
      concatenatedStringBuilder.append(matchedStringList.get(i));
      if (i < matchedStringList.size() - 1) {
        concatenatedStringBuilder.append(GlobalConstant.SPACE);
      }
    }
    return concatenatedStringBuilder.toString();
  }

  /**
   * Get all the string in the regex in (), like for string - "project@user_instance_inasta_insta"
   * and regex - "(.*)@(.*?)_", it will return [project, user]
   *
   * @param str
   * @param pattern
   * @return
   */
  public static List<String> getAllGroupedStr(String str, Pattern pattern) {
    Matcher matcher = pattern.matcher(str);
    List<String> allGroupedStr = new ArrayList<>();
    while (matcher.find()) {
      for (int i = 1; i <= matcher.groupCount(); i++) {
        allGroupedStr.add(matcher.group(i));
      }
    }
    return allGroupedStr;
  }

  /**
   * Given a string and the regex with (.*) to find the specified string you want
   *
   * @param string
   * @param pattern
   * @return
   */
  public static String getFirstMatchedString(String string, Pattern pattern) {
    Matcher matcher = pattern.matcher(string);
    try {
      if (matcher.find()) {
        if (matcher.groupCount() >= 1) {
          return matcher.group(1);
        }
      }
    } catch (Exception e) {
      return null;
    }
    return null;
  }

  /**
   * Given the string and pattern, check if the string has the pattern
   *
   * @param string
   * @param pattern
   * @return
   */
  public static boolean hasRegexPattern(String string, Pattern pattern) {
    if (string == null) {
      return false;
    }
    Matcher matcher = pattern.matcher(string);
    return matcher.find();
  }

  public static boolean hasRegexPattern(String string, List<Pattern> regex) {
    for (Pattern pattern : regex) {
      boolean match = hasRegexPattern(string, pattern);
      if (match) {
        return true;
      }
    }
    return false;
  }
}
