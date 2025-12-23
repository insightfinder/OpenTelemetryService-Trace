// java
package com.insightfinder.utility;

import java.util.List;

public class MiscUtility {

  public static <T> boolean isEmptyList(List<T> list) {
    return list == null || list.isEmpty();
  }
}