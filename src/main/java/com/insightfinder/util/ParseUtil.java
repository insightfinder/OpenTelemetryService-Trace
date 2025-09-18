package com.insightfinder.util;

import com.alibaba.fastjson2.JSONArray;
import com.google.protobuf.ByteString;
import com.insightfinder.config.model.ValueMapping;
import io.grpc.Metadata;
import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.proto.common.v1.KeyValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseUtil {

  public static String parseHexadecimalBytes(ByteString byteString) {
    var result = new StringBuilder();
    for (Byte b : byteString) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

  public static String getIfUserFromMetadata(Metadata metadata) {
    return metadata.get(Metadata.Key.of("ifuser", Metadata.ASCII_STRING_MARSHALLER));
  }

  public static String getLicenseKeyFromMedata(Metadata metadata) {
      var result = metadata.get(Metadata.Key.of("iflicenseKey", Metadata.ASCII_STRING_MARSHALLER));
      if(StringUtils.isNullOrEmpty(result)){
          result = metadata.get(Metadata.Key.of("iflicensekey", Metadata.ASCII_STRING_MARSHALLER));
      }
    return result;
  }

  public static String getProjectFromMedata(Metadata metadata) {
    return metadata.get(Metadata.Key.of("ifproject", Metadata.ASCII_STRING_MARSHALLER));
  }

  public static String getSystemFromMedata(Metadata metadata) {
    return metadata.get(Metadata.Key.of("ifsystem", Metadata.ASCII_STRING_MARSHALLER));
  }

  public static Map<String, Object> getAttrMapFromJsonArray(JSONArray attrMap) {

    var result = new HashMap<String, Object>();

    for (int j = 0; j < attrMap.size(); j++) {
      var curAttr = attrMap.getJSONObject(j);
      result.put(curAttr.getString("key"), curAttr.get("value"));
    }

    return result;
  }

  public static Object getValueInAttrByPath(String path, Map<String, Object> attrMap) {
    if (path == null) {
      return null;
    }
    ValueMapping valueMapping = new ValueMapping(List.of(path));
    return getValueInAttrByPath(valueMapping, attrMap);
  }

  public static Object getValueInAttrByPath(ValueMapping valueMapping,
      Map<String, Object> attrMap) {
    var fieldPaths = valueMapping.getFieldPaths();
    if (fieldPaths == null || fieldPaths.isEmpty()) {
      return null;
    }
    var attrPrefix = "tags.";
    Object value = null;
    for (var fieldPath : fieldPaths) {
      var trimmedFieldPath = fieldPath.trim();
      if (trimmedFieldPath.startsWith(attrPrefix)) {
        var attrName = fieldPath.substring(attrPrefix.length());
        value = attrMap.get(attrName);
      } else {
        value = attrMap.get(trimmedFieldPath);
      }
    }
    return value;
  }

  public static boolean pathExistInAttr(String fieldPath, Map<String, Object> attrMap) {
    if (fieldPath == null) {
      return false;
    }
    var attrPrefix = "tags.";
    var trimmedFieldPath = fieldPath.trim();
    if (trimmedFieldPath.startsWith(attrPrefix)) {
      var attrName = fieldPath.substring(attrPrefix.length());
      return attrMap.containsKey(attrName);
    } else {
      return attrMap.containsKey(trimmedFieldPath);
    }
  }

  public static long fromMicroSecondToMillis(long microSecond) {
    return microSecond / 1000L;
  }

  public static Map<String,String> parseAttrsMapFromAttributeList(List<KeyValue> attrList) {
    var result = new HashMap<String,String>();
    for (var attr: attrList){
      result.put(attr.getKey(),attr.getValue().getStringValue());
    }
    return result;
  }
}

