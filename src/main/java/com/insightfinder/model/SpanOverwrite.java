package com.insightfinder.model;

import io.opentelemetry.api.internal.StringUtils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpanOverwrite {

  private final String originalSpanId;
  private final String overwriteSpanId;

  public boolean needsOverwrite() {
    return !StringUtils.isNullOrEmpty(originalSpanId) && !StringUtils.isNullOrEmpty(
        overwriteSpanId);
  }
}
