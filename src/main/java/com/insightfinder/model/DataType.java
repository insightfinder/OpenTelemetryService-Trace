package com.insightfinder.model;

import lombok.Getter;

@Getter
public enum DataType {
  DATA_TYPE_TRACE("Trace"),
  DATA_TYPE_LOG("Log");

  private final String name;

  DataType(String name) {
    this.name = name;
  }
}
