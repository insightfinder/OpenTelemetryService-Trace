package com.insightfinder.model.request;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class TraceDataBody {

  @JSONField(name = "traceID")
  private String traceID;

  @JSONField(name = "instanceName")
  private String instanceName;

  @JSONField(name = "startTime")
  private long startTime;

  @JSONField(name = "endTime")
  private long endTime;

  @JSONField(name = "duration")
  private long duration;

  @JSONField(name = "spans")
  private Map<String, SpanDataBody> spans = new HashMap<>();

  @JSONField(name = "processes")
  private JSONObject processes;

  public void addSpan(SpanDataBody span) {
    this.spans.put(span.getSpanID(), span);
    var parentSpanId = span.getParentSpanId();
  }
}
