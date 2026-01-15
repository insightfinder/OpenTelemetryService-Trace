package com.insightfinder.model.request;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.insightfinder.model.SpanOverwrite;
import io.opentelemetry.api.internal.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

  @JSONField(name = "total_tokens")
  private int totalToken;

  @JSONField(name = "spans")
  private Map<String, SpanDataBody> spans = new HashMap<>(); // rootSpanId -> rootSpan

  @JSONField(name = "processes")
  private JSONObject processes;

  @JSONField(name = "username")
  private String username;

  private transient Map<String, List<SpanDataBody>> childSpans = new HashMap<>(); // parentSpanId -> childSpans

  public void addSpan(SpanDataBody span) {
    var spanTotalTokens = span.getTotalTokens();
    if (spanTotalTokens != null) {
      totalToken += spanTotalTokens;
    }
    var parentSpanId = span.getParentSpanId();
    if (StringUtils.isNullOrEmpty(parentSpanId)) {
      spans.put(span.getSpanID(), span);
    } else {
      var childSpans = this.childSpans.getOrDefault(parentSpanId, new ArrayList<>());
      childSpans.add(span);
      this.childSpans.put(parentSpanId, childSpans);
    }
  }

  public void composeSpanRelations(SpanOverwrite spanOverwrite) {
    String overwriteTraceId = null;
    if (spanOverwrite != null && spanOverwrite.needsOverwrite()) {
      overwriteSpanId(spanOverwrite);
      overwriteTraceId = spanOverwrite.getOverwriteSpanId();
    }
    int seqID = 0;
    for (SpanDataBody rootSpan : spans.values()) {
      buildSpanRelation(rootSpan, overwriteTraceId, seqID++);
    }
  }

  private void overwriteSpanId(SpanOverwrite spanOverwrite) {
    String originalSpanId = spanOverwrite.getOriginalSpanId();
    String overwriteSpanId = spanOverwrite.getOverwriteSpanId();
    SpanDataBody span = spans.remove(originalSpanId);
    if (span != null) {
      span.setSpanID(overwriteSpanId);
      spans.put(overwriteSpanId, span);
    }
    List<SpanDataBody> childSpans = this.childSpans.remove(originalSpanId);
    if (childSpans != null) {
      this.childSpans.put(overwriteSpanId, childSpans);
    }
  }

  private void buildSpanRelation(SpanDataBody rootSpan, String overwriteTraceId, int seqID) {
    if (rootSpan == null || rootSpan.getSpanID() == null) {
      return;
    }
    if (!StringUtils.isNullOrEmpty(overwriteTraceId)) {
      rootSpan.setTraceID(overwriteTraceId);
    }
    if (!StringUtils.isNullOrEmpty(rootSpan.getOverwriteSpanId())) {
      rootSpan.setSpanID(rootSpan.getOverwriteSpanId());
    }

    // Set the seqID for the current span
    rootSpan.setSeqID(seqID);

    var childSpans = this.childSpans.get(rootSpan.getSpanID());
    if (childSpans != null) {
      rootSpan.addChildSpans(childSpans);
      // Assign seqID to each child based on their position in the list
      for (int i = 0; i < childSpans.size(); i++) {
        buildSpanRelation(childSpans.get(i), overwriteTraceId, i);
      }
    }
  }

  public boolean isEmpty() {
    return spans.isEmpty();
  }
}
