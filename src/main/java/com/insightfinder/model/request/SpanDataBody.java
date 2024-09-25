package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class SpanDataBody {

  @JSONField(name = "traceID")
  private final String traceID;

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

  @JSONField(name = "childSpans")
  @Singular(value = "childSpan")
  private final Map<String, SpanDataBody> childSpans = new HashMap<>();
}



