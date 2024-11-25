package com.insightfinder.model.request;

import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@Builder
public class PromptData {

  private String traceId;
  private String instanceName;
  private Prompt inputPrompt;
  private Prompt outputPrompt;

  public boolean isEmpty() {
    return (inputPrompt == null || inputPrompt.isEmpty()) && (outputPrompt == null
        || outputPrompt.isEmpty());
  }

  public void setSpanId(String spanId) {
    setInputPromptSpanId(spanId);
    setOutputPromptSpanId(spanId);
  }

  public void setStartTime(long startTime) {
    setInputPromptStartTime(startTime);
    setOutputPromptStartTime(startTime);
  }

  public void setDuration(long duration) {
    setInputPromptDuration(duration);
    setOutputPromptDuration(duration);
  }

  private void setInputPromptDuration(long duration) {
    if (inputPrompt != null && !inputPrompt.isEmpty()) {
      inputPrompt.setDuration(duration);
    }
  }

  private void setOutputPromptDuration(long duration) {
    if (outputPrompt != null && !outputPrompt.isEmpty()) {
      outputPrompt.setDuration(duration);
    }
  }

  private void setInputPromptStartTime(long startTime) {
    if (inputPrompt != null && !inputPrompt.isEmpty()) {
      inputPrompt.setStartTime(startTime);
    }
  }

  private void setOutputPromptStartTime(long startTime) {
    if (outputPrompt != null && !outputPrompt.isEmpty()) {
      outputPrompt.setStartTime(startTime);
    }
  }

  private void setInputPromptSpanId(String spanId) {
    if (inputPrompt != null && !inputPrompt.isEmpty()) {
      inputPrompt.setSpanId(spanId);
    }
  }

  private void setOutputPromptSpanId(String spanId) {
    if (outputPrompt != null && !outputPrompt.isEmpty()) {
      outputPrompt.setSpanId(spanId);
    }
  }

  @Nullable
  public String getPromptHash() {
    if (!inputPrompt.isEmpty() && !outputPrompt.isEmpty()) {
      return DigestUtils.md5Hex(outputPrompt.getPrompt());
    }
    return null;
  }
}
