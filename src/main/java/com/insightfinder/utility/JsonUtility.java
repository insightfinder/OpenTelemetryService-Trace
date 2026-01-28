package com.insightfinder.utility;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.base.GlobalConstant;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.http.util.TextUtils;

public class JsonUtility {
  private static final String JSONARRAY_INDEX_REGEX = "\\[\\d+\\]$";
  private static final String INDEX_STR_PREFIX = "\\";
  private static final String EMPTY_REGEX = "^\\s*$";


  /**
   * Get the object by the given json path
   *
   * @param jsonObject
   * @param keys
   * @return
   */
  public static Object getJsonValueObject(JSONObject jsonObject, String[] keys) {
    Object valueObj = jsonObject;
    for (int i = 0; i < keys.length; i++) {
      String curKey = keys[i];
      if (valueObj instanceof JSONObject) {
        JSONObject obj = JSONObject.parseObject(valueObj.toString());
        if (obj.containsKey(curKey)) {
          valueObj = obj.get(curKey);
        } else {
          return null;
        }
      } else if (valueObj instanceof JSONArray) {
        // TODO
        break;
      }
    }
    return valueObj;
  }

  public static Set<String> getJsonValueSet(JSONObject jsonObject, String jsonPath) {
    String[] keys = jsonPath.split(GlobalConstant.JSON_PATH_CONNECTOR);
    return getJsonValueSet(jsonObject, keys);
  }

  /**
   * Get all the values by the given json path, the value of the key might be a json array, so that
   * we need to go through all the object in the json array
   *
   * @param jsonObject
   * @param keys
   * @return
   */
  public static Set<String> getJsonValueSet(JSONObject jsonObject, String[] keys) {
    Set<String> values = new HashSet<>();
    Object valueObj = jsonObject;
    boolean isFound = false;
    for (int i = 0; i < keys.length; i++) {
      String curKey = keys[i];
      if (valueObj instanceof JSONObject) {
        JSONObject obj = JSONObject.parseObject(valueObj.toString());
        if (obj.containsKey(curKey)) {
          valueObj = obj.get(curKey);
          if (i == keys.length - 1) {
            isFound = true;
          }
        } else {
          // no such key, just break
          break;
        }
      } else if (valueObj instanceof JSONArray) {
        JSONArray array = JSONArray.parseArray(valueObj.toString());
        values = getJsonValueSetFromJsonArray(array, keys, i);
        break;
      }
    }
    if (isFound) {
      values = addIntoValues(values, valueObj);
    }
    return values;
  }

  /**
   * Get a list of values by the given json array, json keys and the start key index
   *
   * @param jsonArray
   * @param keys
   * @param keyIndex
   * @return
   */
  private static Set<String> getJsonValueSetFromJsonArray(JSONArray jsonArray, String[] keys,
      int keyIndex) {
    Set<String> values = new HashSet<>();
    boolean isFound = false;
    for (int i = 0; i < jsonArray.size(); i++) {
      Object valueObj = jsonArray.get(i);
      for (int j = keyIndex; j < keys.length; j++) {
        String curKey = keys[j];
        if (valueObj instanceof JSONObject) {
          JSONObject obj = JSONObject.parseObject(valueObj.toString());
          if (obj.containsKey(curKey)) {
            valueObj = obj.get(curKey);
            if (j == keys.length - 1) {
              isFound = true;
            }
          } else {
            // no such key, just break
            break;
          }
        } else if (valueObj instanceof JSONArray) {
          JSONArray array = JSONArray.parseArray(valueObj.toString());
          values.addAll(getJsonValueSetFromJsonArray(array, keys, j));
        }
        if (isFound) {
          values = addIntoValues(values, valueObj);
        }
      }
    }
    return values;
  }

  /**
   * Add json array result to the values
   *
   * @param values
   * @param valueObj
   * @return
   */
  private static Set<String> addIntoValues(Set<String> values, Object valueObj) {
    if (valueObj instanceof JSONArray) {
      JSONArray array = JSONArray.parseArray(valueObj.toString());
      for (int i = 0; i < array.size(); i++) {
        values.add(array.get(i).toString());
      }
    } else {
      String valueObjStr = valueObj.toString();
      if (valueObjStr != null && valueObjStr.trim().isEmpty()) {
        values.add(valueObjStr);
      } else {
        values.add(valueObjStr.trim());
      }
    }
    return values;
  }

  public static Set<String> getJsonValueSetByPattern(JSONObject jsonObject, String[] keys,
      Pattern pattern) {
    Set<String> extractedJsonValueSet = new HashSet<>();
    Set<String> jsonValueSet = getJsonValueSet(jsonObject, keys);
    for (String value : jsonValueSet) {
      String extractedValue = RegexUtility.getAllMatchedConcatenatedStringByPattern(value, pattern);
      if (isEmptyRegex(pattern.toString(), value) || !TextUtils.isEmpty(extractedValue)){
        extractedJsonValueSet.add(extractedValue);
      }
    }
    return extractedJsonValueSet;
  }

  public static boolean isEmptyRegex(String regexString, String value) {
    if (!TextUtils.isEmpty(regexString) && value != null) {
      return regexString.equals(EMPTY_REGEX) && value.equals(GlobalConstant.EMPTY_STRING);
    }
    return false;
  }
  /**
   * Recursive function to remove the key by the given json path
   *
   * @param valueObject
   * @param keys
   * @param index       - the start index of the json key to search
   */
  public static void removeJsonValueFromObject(Object valueObject, String[] keys, int index) {
    for (int i = index; i < keys.length; i++) {
      String key = keys[i];
      if (valueObject instanceof JSONObject) {
        JSONObject obj = (JSONObject) valueObject;
        if (i == keys.length - 1 && obj.containsKey(key)) {
          obj.remove(key);
        } else if (obj.containsKey(key)) {
          valueObject = obj.get(key);
        } else {
          break;
        }
      } else if (valueObject instanceof JSONArray) {
        JSONArray array = (JSONArray) valueObject;
        for (int j = 0; j < array.size(); j++) {
          Object obj = array.get(j);
          if (i == keys.length - 1 && obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.containsKey(key)) {
              jsonObject.remove(key);
            }
          } else {
            removeJsonValueFromObject(obj, keys, i);
          }
        }
      } else {
        break;
      }
    }
  }
}
