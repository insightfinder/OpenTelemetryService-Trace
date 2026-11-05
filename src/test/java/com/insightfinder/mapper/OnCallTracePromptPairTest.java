package com.insightfinder.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.model.request.ContentData;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * An MCP on-call investigation, as Jaeger returns it, run through the gateway rules
 * (src/test/resources/application.yml). The span shapes mirror the prod on-call traces: the root
 * "chat-prompt-response" span has only the prompt and ends at once; "oncall-task-result" is its
 * child and has the response and the tokens.
 */
class OnCallTracePromptPairTest {

  private static final String TASK_ID = "oncall-ari-abc-0";
  private static final String ROOT_SPAN = "4ee9c7e249e841f4";

  private static JSONObject tag(String key, Object value) {
    JSONObject tag = new JSONObject();
    tag.put("key", key);
    tag.put("value", value);
    return tag;
  }

  private static JSONObject span(String spanId, String operation, long startTime,
      Map<String, Object> tags, String parentSpanId) {
    JSONArray tagArray = new JSONArray();
    tags.forEach((k, v) -> tagArray.add(tag(k, v)));
    JSONArray references = new JSONArray();
    if (parentSpanId != null) {
      JSONObject ref = new JSONObject();
      ref.put("refType", "CHILD_OF");
      ref.put("spanID", parentSpanId);
      references.add(ref);
    }
    JSONObject span = new JSONObject();
    span.put("traceID", "t1");
    span.put("spanID", spanId);
    span.put("operationName", operation);
    span.put("startTime", startTime);
    span.put("duration", 1000L);
    span.put("tags", tagArray);
    span.put("references", references);
    return span;
  }

  private static Map<String, Object> rootTags() {
    Map<String, Object> tags = new LinkedHashMap<>();
    tags.put("span.kind", "internal");
    tags.put("trace.entity", "chat-prompt-response");
    tags.put("chat.id", TASK_ID);
    tags.put("uuid", "session-1");
    tags.put("x-session-id", "session-1");
    tags.put("x-username", "demoAdminProd");
    tags.put("chat.prompt", "Investigate incident 42");
    return tags;
  }

  private static Map<String, Object> resultTags(boolean withPrompt) {
    Map<String, Object> tags = new LinkedHashMap<>();
    tags.put("span.kind", "internal");
    tags.put("trace.entity", "chat-prompt-response");
    tags.put("x-username", "demoAdminProd");
    if (withPrompt) {
      tags.put("chat.prompt", "Investigate incident 42");
      tags.put("uuid", "session-1");
      tags.put("x-session-id", "session-1");
    }
    tags.put("chat.response", "Root cause: disk full");
    tags.put("chat.prompt_tokens", 456863);
    tags.put("chat.completion_tokens", 13578);
    return tags;
  }

  private static TraceInfo map(boolean resultHasPrompt) {
    JSONArray spans = new JSONArray();
    spans.add(span(ROOT_SPAN, "chat-prompt-response", 1_000_000L, rootTags(), null));
    spans.add(span("29b05872c8a56f38", "oncall-task-result", 9_000_000L,
        resultTags(resultHasPrompt), ROOT_SPAN));
    JSONObject process = new JSONObject();
    process.put("serviceName", "mcp-service");
    JSONObject processes = new JSONObject();
    processes.put("p1", process);
    JSONObject trace = new JSONObject();
    trace.put("spans", spans);
    trace.put("processes", processes);
    JSONArray data = new JSONArray();
    data.add(trace);
    JSONObject raw = new JSONObject();
    raw.put("data", data);
    return TraceDataMapper.getInstance().fromRawJaegerData(raw,
        new com.insightfinder.model.message.TraceInfo("t1", "u", "p", "s", "l"));
  }

  @Test
  void resultSpanWithPromptAndResponseGivesOnePromptPair() {
    TraceInfo info = map(true);

    assertThat(info.getPromptResponsePairs()).hasSize(1);
    ContentData pair = info.getPromptResponsePairs().getFirst();
    assertThat(pair.getInputPrompt().getPrompt()).isEqualTo("Investigate incident 42");
    assertThat(pair.getResponseRecord().getResponse()).isEqualTo("Root cause: disk full");
    assertThat(pair.getSessionId()).isEqualTo("session-1");
    assertThat(pair.getTraceId()).isEqualTo(TASK_ID);
  }

  @Test
  void resultSpanTokensBecomeTheTraceTotal() {
    TraceInfo info = map(true);

    assertThat(info.getTraceDataBody().getTraceID()).isEqualTo(TASK_ID);
    assertThat(info.getTraceDataBody().getTotalToken()).isEqualTo(456863 + 13578);
  }

  @Test
  void resultSpanWithoutPromptGivesNoPair() {
    // The shape before the MCP fix: why the trace showed 0 Inputs / 0 Outputs
    TraceInfo info = map(false);

    assertThat(info.getPromptResponsePairs()).isEmpty();
  }
}
