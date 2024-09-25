package com.insightfinder.model.request;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class TraceDataBody {

  @JSONField(name = "traceID")
  public String traceID;

  @JSONField(name = "serviceName")
  public String serviceName;

  @JSONField(name = "startTime")
  public long startTime;

  @JSONField(name = "endTime")
  public long endTime;

  @JSONField(name = "duration")
  public long duration;

  @JSONField(name = "spans")
  public Map<String, SpanDataBody> spans;

  @JSONField(name = "processes")
  public JSONObject processes;

  public TraceDataBody() {
    this.spans = new HashMap<>();
  }

  public void addSpan(SpanDataBody span) {
    // TODO: Add the child spans to the end of the parent spans.
    this.spans.put(span.spanID, span);
  }
}
