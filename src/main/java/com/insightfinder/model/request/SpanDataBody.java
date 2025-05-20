package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpanDataBody {

  @JSONField(name = "traceID")
  private String traceID;

  @JSONField(name = "spanID")
  private final String spanID;

  @JSONField(name = "operationName")
  private final String operationName;

  @JSONField(name = "startTime")
  private final long startTime;

  @JSONField(name = "duration")
  private final long duration;

  @JSONField(name = "attributes")
  private final Map<String, Object> attributes;

  @JSONField(name = "parentSpanId")
  private final String parentSpanId;

  @JSONField(name = "error")
  private final boolean error;

  @JSONField(name = "childSpans")
  private final Map<String, SpanDataBody> childSpans = new HashMap<>();

  public void addChildSpans(Set<SpanDataBody> childSpans) {
    childSpans.forEach(childSpan -> {
      var childSpanId = childSpan.getSpanID();
      this.childSpans.put(childSpanId, childSpan);
    });
  }

  public Integer getTotalTokens() {
    try {
      return (Integer) attributes.get("total_tokens");
    } catch (Exception e) {
      return null;
    }
  }
}



