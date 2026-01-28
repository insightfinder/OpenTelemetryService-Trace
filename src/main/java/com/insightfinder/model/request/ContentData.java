package com.insightfinder.model.request;

import com.insightfinder.model.SpanOverwrite;
import com.insightfinder.service.SensitiveDataFilter;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

@Data
@Builder
public class ContentData {

  private String traceId;
  private String username;
  private String instanceName;
  private String entryOperation;
  private InputPrompt inputPrompt;
  private ResponseRecord responseRecord;
  private String sessionId;
  private long traceStartTimestamp;

  public boolean isEmpty() {
    return (inputPrompt == null || inputPrompt.isEmpty()) && (responseRecord == null
        || responseRecord.isEmpty());
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
    if (responseRecord != null && !responseRecord.isEmpty()) {
      responseRecord.setDuration(duration);
    }
  }

  private void setInputPromptStartTime(long startTime) {
    if (inputPrompt != null && !inputPrompt.isEmpty()) {
      inputPrompt.setStartTime(startTime);
    }
  }

  private void setOutputPromptStartTime(long startTime) {
    if (responseRecord != null && !responseRecord.isEmpty()) {
      responseRecord.setStartTime(startTime);
    }
  }

  private void setInputPromptSpanId(String spanId) {
    if (inputPrompt != null && !inputPrompt.isEmpty()) {
      inputPrompt.setSpanId(spanId);
    }
  }

  private void setOutputPromptSpanId(String spanId) {
    if (responseRecord != null && !responseRecord.isEmpty()) {
      responseRecord.setSpanId(spanId);
    }
  }

  public void overWriteSpanId(SpanOverwrite spanOverwrite) {
    if (spanOverwrite == null || !spanOverwrite.needsOverwrite()) {
      return;
    }
    String originalSpanId = spanOverwrite.getOriginalSpanId();
    String overwriteSpanId = spanOverwrite.getOverwriteSpanId();
    if (!inputPrompt.isEmpty() && inputPrompt.getSpanId().equals(originalSpanId)) {
      inputPrompt.setSpanId(overwriteSpanId);
    }
    if (!responseRecord.isEmpty() && responseRecord.getSpanId().equals(originalSpanId)) {
      responseRecord.setSpanId(overwriteSpanId);
    }
  }

  @Nullable
  public String getPromptHash() {
    if (!inputPrompt.isEmpty() && !responseRecord.isEmpty()) {
      return DigestUtils.md5Hex(responseRecord.getContent());
    }
    return null;
  }
}
