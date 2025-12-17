package com.insightfinder.util.regex;

import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.base.GlobalConstant;
import com.insightfinder.models.misc.IFRegexPattern;
import com.insightfinder.utility.JsonUtility;
import com.insightfinder.utility.MiscUtility;
import com.insightfinder.utility.RegexUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.Data;
import org.apache.http.util.TextUtils;

@Data
public class JsonStructure {

  private String[] keys;
  private String valueRegex;
  private String jsonPath;
  private transient Pattern valueRegexPattern;
  private static final String JSON_PATH_CONNECTOR = "->";
  private static final String JSON_KEY_VALUE_SPLIT = "=";
  private static final int NO_VALUES = 1;
  private static final int HAS_VALUES = 2;

  public JsonStructure(String jsonStructureStr, boolean parsePattern) {
    String[] keyAndValue = splitKeyAndValue(jsonStructureStr);
    switch (keyAndValue.length) {
      case NO_VALUES:
        this.jsonPath = keyAndValue[0];
        this.keys = getKeys(jsonPath);
        break;
      case HAS_VALUES:
        this.jsonPath = keyAndValue[0];
        this.keys = getKeys(jsonPath);
        this.valueRegex = keyAndValue[1];
        if (parsePattern) {
          this.valueRegexPattern = IFRegexPattern.getPattern(valueRegex, false);
        }
        break;
      default:
        break;
    }
  }

  public JsonStructure(String jsonStructureStr, boolean parsePattern, boolean isMultiline) {
    String[] keyAndValue = splitKeyAndValue(jsonStructureStr);
    switch (keyAndValue.length) {
      case NO_VALUES:
        this.jsonPath = keyAndValue[0];
        this.keys = getKeys(jsonPath);
        break;
      case HAS_VALUES:
        this.jsonPath = keyAndValue[0];
        this.keys = getKeys(jsonPath);
        this.valueRegex = keyAndValue[1];
        if (parsePattern) {
          this.valueRegexPattern = IFRegexPattern.getPattern(valueRegex, isMultiline);
        }
        break;
      default:
        break;
    }
  }

  private String[] getKeys(String keys) {
    return keys.split(JSON_PATH_CONNECTOR);
  }

  public boolean isNoValue() {
    return TextUtils.isEmpty(this.valueRegex);
  }

  public String getFirstMatchedValueFromObject(JSONObject jsonObject) {
    List<String> values = new ArrayList<>(getValueFromObject(jsonObject));
    if (values.isEmpty()) {
      return null;
    }
    return RegexUtility.getFirstMatchedString(values.get(0), valueRegexPattern);
  }

  public Object getObject(JSONObject object) {
    Object valueObject = JsonUtility.getJsonValueObject(object, keys);
    if (valueObject == null) {
      return null;
    }
    if (isNoValue()) {
      return valueObject;
    } else {
      String value =
          RegexUtility.getMatchedGroupedString(valueObject.toString(), valueRegexPattern);
      return value;
    }
  }

  public void getFromObject(JSONObject object, JSONObject filteredObject) {
    Object valueObject = JsonUtility.getJsonValueObject(object, keys);
    if (valueObject == null) {
      return;
    }
    if (isNoValue()) {
      reconstructObject(filteredObject, valueObject);
    } else {
      String value =
          RegexUtility.getMatchedGroupedString(valueObject.toString(), valueRegexPattern);
      if (TextUtils.isEmpty(value)) {
        value = valueObject.toString();
      }
      reconstructObject(filteredObject, value);
    }
  }

  public void reconstructObject(JSONObject filteredObject, Object valueObject) {
    Object object = filteredObject;
    for (int i = 0; i < keys.length; i++) {
      if (i == keys.length - 1) {
        ((JSONObject) object).put(keys[i], valueObject);
        break;
      }
      if (!((JSONObject) object).containsKey(keys[i])) {
        ((JSONObject) object).put(keys[i], new JSONObject());
      }
      object = ((JSONObject) object).get(keys[i]);
    }
  }



  public Set<String> getValueFromObject(JSONObject jsonObject) {
    if (isNoValue()) {
      return JsonUtility.getJsonValueSet(jsonObject, keys);
    }
    return JsonUtility.getJsonValueSetByPattern(jsonObject, keys, valueRegexPattern);
  }

  public String getSingleValueFromObject(JSONObject jsonObject) {
    List<String> values = new ArrayList<>(getValueFromObject(jsonObject));
    if (values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

  public String getSingleValueWithKey(JSONObject jsonObject) {
    String value = getSingleValueFromObject(jsonObject);
    if (jsonPath == null) {
      return null;
    }
    return jsonPath + GlobalConstant.EQUAL_SIGN + value;
  }

  public void removeFromObject(JSONObject object) {
    if (isNoValue()) {
      JsonUtility.removeJsonValueFromObject(object, keys, 0);
    } else {
      Object valueObject = JsonUtility.getJsonValueObject(object, keys);
      String valueStr = valueObject.toString().replaceAll("\\n", "");
      List<String> needToRemove = RegexUtility.getMatchedGroups(valueStr, valueRegexPattern);
      if (MiscUtility.isEmptyList(needToRemove)) {
        return;
      }
      for (String needToRemoveStr : needToRemove) {
        valueStr = valueStr.replace(needToRemoveStr, "");
      }
      reconstructObject(object, valueStr);
    }
  }

  public boolean matchPattern(JSONObject jsonObject) {
    String value = getSingleValueFromObject(jsonObject);
    return JsonUtility.isEmptyRegex(valueRegex, value) || !TextUtils.isEmpty(value);
  }

  public static String[] splitKeyAndValue(String str) {
    if (TextUtils.isEmpty(str)) {
      return null;
    }
    return str.split(JSON_KEY_VALUE_SPLIT, 2);
  }

  /**
   * By the given keys and json object, extract the necessary data and reconstruct the json object
   *
   * @param obj
   * @param keys
   * @return
   */
  public static JSONObject extractData(JSONObject obj, Set<String> keys) {
    JSONObject newObj = new JSONObject();
    for (String key : keys) {
      JsonStructure structure = new JsonStructure(key, false);
      structure.getFromObject(obj, newObj);
    }
    return newObj;
  }

  public static JsonStructure get(String jsonStructureStr, boolean parsePattern) {
    if (TextUtils.isEmpty(jsonStructureStr)) {
      return null;
    }
    return new JsonStructure(jsonStructureStr, parsePattern);
  }

  public void clearValueRegex() {
    this.valueRegex = null;
  }

  public boolean isStartWithKey(String key) {
    if (keys.length != 0) {
      return key.equals(keys[0]);
    }
    return false;
  }

  @Override
  public String toString() {
    return "JsonStructure [keys=" + Arrays.toString(keys) + ", valueRegex=" + valueRegex
        + ", jsonPath=" + jsonPath + "]";
  }
}
