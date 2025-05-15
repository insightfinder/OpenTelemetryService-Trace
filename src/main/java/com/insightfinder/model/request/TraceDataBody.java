package com.insightfinder.model.request;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import io.opentelemetry.api.internal.StringUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

  @JSONField(name = "total_tokens")
  private int totalToken;

  @JSONField(name = "spans")
  private Map<String, SpanDataBody> spans = new HashMap<>(); // rootSpanId -> rootSpan

  @JSONField(name = "processes")
  private JSONObject processes;

  @JSONField(name = "username")
  private String username;

  private transient Map<String, Set<SpanDataBody>> childSpans = new HashMap<>(); // parentSpanId -> childSpans

  public void addSpan(SpanDataBody span) {
    var spanTotalTokens = span.getTotalTokens();
    if (spanTotalTokens != null) {
      totalToken += spanTotalTokens;
    }
    var parentSpanId = span.getParentSpanId();
    if (StringUtils.isNullOrEmpty(parentSpanId)) {
      spans.put(span.getSpanID(), span);
    } else {
      var childSpans = this.childSpans.getOrDefault(parentSpanId, new HashSet<>());
      childSpans.add(span);
      this.childSpans.put(parentSpanId, childSpans);
    }
  }

  public void composeSpanRelations() {
    for (SpanDataBody rootSpan : spans.values()) {
      buildSpanRelation(rootSpan);
    }
  }

  private void buildSpanRelation(SpanDataBody rootSpan) {
    if (rootSpan == null || rootSpan.getSpanID() == null) {
      return;
    }
    var childSpans = this.childSpans.get(rootSpan.getSpanID());
    if (childSpans != null) {
      rootSpan.addChildSpans(childSpans);
      for (SpanDataBody childSpan : childSpans) {
        buildSpanRelation(childSpan);
      }
    }
  }

  public boolean isEmpty() {
    return spans.isEmpty();
  }
}
