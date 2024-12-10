package com.insightfinder.model;

import lombok.Getter;

@Getter
public enum ProjectCloudType {
  LOG("Log"),
  TRACE("Trace"),
  PRIVATE_CLOUD("PrivateCloud");

  private final String name;

  ProjectCloudType(String name) {
    this.name = name;
  }
}
