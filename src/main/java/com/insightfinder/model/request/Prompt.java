package com.insightfinder.model.request;

import io.opentelemetry.api.internal.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Prompt {

  private String spanId;
  private long startTime;
  private long duration;
  private String prompt;

  public Prompt(String prompt) {
    this.prompt = prompt;
  }

  public boolean isEmpty() {
    return StringUtils.isNullOrEmpty(prompt);
  }
}
