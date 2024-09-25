package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.Map;
import lombok.Data;

@Data
public class SpanDataBody {

  @JSONField(name = "traceID")
  public String traceID;

  @JSONField(name = "spanID")
  public String spanID;

  @JSONField(name = "operationName")
  public String operationName;

  @JSONField(name = "startTime")
  public long startTime;

  @JSONField(name = "duration")
  public long duration;

  @JSONField(name = "attributes")
  public Map<String, Object> attributes;

  @JSONField(name = "parentSpanId")
  public String parentSpanId;

  @JSONField(name = "childSpans")
  public Map<String, SpanDataBody> childSpans;
}



