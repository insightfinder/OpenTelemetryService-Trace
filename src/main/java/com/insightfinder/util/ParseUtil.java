package com.insightfinder.util;

import com.alibaba.fastjson2.JSONArray;
import com.google.protobuf.ByteString;
import com.insightfinder.config.model.ValueMapping;
import io.grpc.Metadata;
import java.util.HashMap;
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
    return metadata.get(Metadata.Key.of("iflicenseKey", Metadata.ASCII_STRING_MARSHALLER));
  }

  public static String getProjectFromMedata(Metadata metadata) {
    return metadata.get(Metadata.Key.of("ifproject", Metadata.ASCII_STRING_MARSHALLER));
  }

  public static Map<String, Object> getAttrMapFromJsonArray(JSONArray attrMap) {

    var result = new HashMap<String, Object>();

    for (int j = 0; j < attrMap.size(); j++) {
      var curAttr = attrMap.getJSONObject(j);
      result.put(curAttr.getString("key"), curAttr.get("value"));
    }

    return result;
  }

  public static Object getValueInAttrByPath(ValueMapping valueMapping,
      Map<String, Object> attrMap) {
    var fieldPaths = valueMapping.getFieldPaths();
    if (fieldPaths == null || fieldPaths.isEmpty()) {
      return null;
    }
    var attrPrefix = "tags.";
    for (var fieldPath : fieldPaths) {
      var trimmedFieldPath = fieldPath.trim();
      if (trimmedFieldPath.startsWith(attrPrefix)) {
        var attrName = fieldPath.substring(attrPrefix.length());
        return attrMap.get(attrName);
      } else {
        return attrMap.get(trimmedFieldPath);
      }
    }
    return null;
  }

  public static long fromMicroSecondToMillis(long microSecond) {
    return microSecond / 1000L;
  }
}
