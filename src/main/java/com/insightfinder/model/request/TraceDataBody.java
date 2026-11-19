package com.insightfinder.model.request;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.insightfinder.model.SpanOverwrite;
import io.opentelemetry.api.internal.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class TraceDataBody {

  private static final String TRACE_ENTITY_ATTRIBUTE = "trace.entity";
  private static final String PROMPT_RESPONSE_ENTITY = "chat-prompt-response";
  private static final String AGENT_INVOCATION_ENTITY = "agent-invocation";
  private static final String COORDINATOR_TURN_OPERATION = "coordinator-turn";
  private static final String AGENT_SLUG_ATTRIBUTE = "agent.slug";
  private static final String COORDINATOR_SLUG = "coordinator";
  private static final String ONCALL_TASK_ID_ATTRIBUTE = "oncall.task_id";
  private static final String TOTAL_TOKENS_ATTRIBUTE = "total_tokens";

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
    var parentSpanId = span.getParentSpanId();
    if (StringUtils.isNullOrEmpty(parentSpanId)) {
      // Only root spans contribute to the trace's total — child spans (e.g. per-agent spans in
      // multi-agent turns) can carry the same already-summed token count propagated from the
      // root, and counting them too double-counts a single real turn's tokens.
      var spanTotalTokens = span.getTotalTokens();
      if (spanTotalTokens != null) {
        totalToken += spanTotalTokens;
      }
      spans.put(span.getSpanID(), span);
    } else {
      // Exception: a child span that is itself a prompt/response span holds the real total of
      // its trace. An on-call investigation's root span ends the moment the run starts, so its
      // tokens are only on the "oncall-task-result" child; skipping it left the trace at 0.
      // Agent spans ("agent-invocation") stay excluded, which keeps the double-count fix above.
      if (isPromptResponseSpan(span)) {
        var spanTotalTokens = span.getTotalTokens();
        if (spanTotalTokens != null) {
          totalToken += spanTotalTokens;
        }
      }
      var childSpans = this.childSpans.getOrDefault(parentSpanId, new ArrayList<>());
      childSpans.add(span);
      this.childSpans.put(parentSpanId, childSpans);
    }
  }

  private static boolean isPromptResponseSpan(SpanDataBody span) {
    var attributes = span.getAttributes();
    return attributes != null
        && PROMPT_RESPONSE_ENTITY.equals(attributes.get(TRACE_ENTITY_ATTRIBUTE));
  }

  /**
   * AW-2536: an on-call trace whose "oncall-task-result" span never reached Jaeger has a total of
   * 0, although the spans that did arrive carry real tokens. Like the duration, which is computed
   * from whatever spans arrived, the total then falls back to the tokens those spans carry.
   *
   * <p>Only applied when the root / result-span rule in {@link #addSpan} found nothing, so it is
   * never added on top of that total. The sum is every "coordinator-turn" span plus every sub-agent
   * span (agent-invocation, not the coordinator) that has no sub-agent span above it; on a
   * complete run that equals the result span's total exactly (coordinator turns + sub-agents).
   * Auto-instrumented LLM-call spans are not counted: their tokens are already in their agent's or
   * turn's total. Must run before {@link #composeSpanRelations}, which may rename span ids.
   */
  public void applyOnCallPartialTokenTotal() {
    if (totalToken != 0) {
      return;
    }
    Map<String, SpanDataBody> spansById = new HashMap<>();
    spans.values().forEach(span -> putById(spansById, span));
    childSpans.values().forEach(list -> list.forEach(span -> putById(spansById, span)));
    if (spansById.values().stream().noneMatch(TraceDataBody::isOnCallSpan)) {
      return;
    }
    int partialTotal = 0;
    for (SpanDataBody span : spansById.values()) {
      boolean counted = isCoordinatorTurnSpan(span)
          || (isSubAgentSpan(span) && !hasSubAgentAncestor(span, spansById));
      if (counted) {
        Integer tokens = tokensOf(span);
        if (tokens != null) {
          partialTotal += tokens;
        }
      }
    }
    totalToken = partialTotal;
  }

  private static void putById(Map<String, SpanDataBody> spansById, SpanDataBody span) {
    if (span != null && !StringUtils.isNullOrEmpty(span.getSpanID())) {
      spansById.put(span.getSpanID(), span);
    }
  }

  private static boolean isOnCallSpan(SpanDataBody span) {
    var attributes = span.getAttributes();
    return attributes != null && attributes.get(ONCALL_TASK_ID_ATTRIBUTE) != null;
  }

  private static boolean isCoordinatorTurnSpan(SpanDataBody span) {
    return COORDINATOR_TURN_OPERATION.equals(span.getOperationName());
  }

  private static boolean isSubAgentSpan(SpanDataBody span) {
    var attributes = span.getAttributes();
    return attributes != null
        && AGENT_INVOCATION_ENTITY.equals(attributes.get(TRACE_ENTITY_ATTRIBUTE))
        && !COORDINATOR_SLUG.equals(attributes.get(AGENT_SLUG_ATTRIBUTE));
  }

  /** True when a sub-agent span above this one exists: its tokens already include this span's. */
  private static boolean hasSubAgentAncestor(SpanDataBody span,
      Map<String, SpanDataBody> spansById) {
    Set<String> visited = new HashSet<>();
    String parentId = span.getParentSpanId();
    while (!StringUtils.isNullOrEmpty(parentId) && visited.add(parentId)) {
      SpanDataBody parent = spansById.get(parentId);
      if (parent == null) {
        return false;
      }
      if (isSubAgentSpan(parent)) {
        return true;
      }
      parentId = parent.getParentSpanId();
    }
    return false;
  }

  private static Integer tokensOf(SpanDataBody span) {
    var attributes = span.getAttributes();
    if (attributes == null) {
      return null;
    }
    Object value = attributes.get(TOTAL_TOKENS_ATTRIBUTE);
    if (value instanceof Number number) {
      return number.intValue();
    }
    if (value instanceof String text) {
      try {
        return Integer.parseInt(text.trim());
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
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
