package com.insightfinder.model;

import lombok.Getter;

@Getter
public enum ValueType {
  STRING(String.class),
  INT(Integer.class),
  BOOLEAN(Boolean.class),
  UNKNOWN(Object.class);

  private final Class<?> typeClass;

  ValueType(Class<?> typeClass) {
    this.typeClass = typeClass;
  }
}
