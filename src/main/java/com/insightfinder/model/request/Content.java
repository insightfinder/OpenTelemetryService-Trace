package com.insightfinder.model.request;

import io.opentelemetry.api.internal.StringUtils;
import lombok.Data;

@Data
public abstract class Content {

  private String spanId;
  private long startTime;
  private long duration;

  public boolean isEmpty() {
    return StringUtils.isNullOrEmpty(getContent());
  }

  abstract String getContent();

  abstract void setContent(String content);
}
