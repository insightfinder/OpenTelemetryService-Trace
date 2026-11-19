package com.insightfinder.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * AW-2536: an on-call trace whose "oncall-task-result" span never reached Jaeger must show the
 * tokens of the spans that did arrive instead of 0, without ever counting a token twice.
 *
 * <p>Numbers are the prod traces: 172c7 (complete, result span 222655 = coordinator turns 7784 +
 * 10266 + 5339 + IT Infrastructure Agent 199266), 6cf87 (result and coordinator span lost, IT
 * agent 54176) and fc0ff (result lost, IT agent 179252).
 */
class OnCallPartialTokenTotalTest {

  private static final String TASK_ID = "oncall-ari-6cf8705bf779fdb7406d1af76700487a947836ff-0";
  private static final String ROOT = "a000000000000001";
  private static final String COORDINATOR = "a000000000000002";
  private static final String IT_AGENT = "a000000000000003";

  private final List<JSONObject> spans = new ArrayList<>();
  private long clock = 1_000_000L;

  // ── Span builders (Jaeger /api/traces shape) ──────────────────────────────────────────────

  private static JSONObject tag(String key, Object value) {
    JSONObject tag = new JSONObject();
    tag.put("key", key);
    tag.put("value", value);
    return tag;
  }

  private void span(String spanId, String operation, Map<String, Object> tags,
      String parentSpanId) {
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
    span.put("startTime", clock);
    span.put("duration", 1000L);
    span.put("tags", tagArray);
    span.put("references", references);
    clock += 1000L;
    spans.add(span);
  }

  private static Map<String, Object> tokens(Map<String, Object> tags, int input, int output) {
    tags.put("chat.prompt_tokens", input);
    tags.put("chat.completion_tokens", output);
    tags.put("prompt_tokens", input);
    tags.put("response_tokens", output);
    tags.put("total_tokens", input + output);
    return tags;
  }

  private static Map<String, Object> base() {
    Map<String, Object> tags = new LinkedHashMap<>();
    tags.put("span.kind", "internal");
    tags.put("x-username", "demoAdminProd");
    return tags;
  }

  private void root(boolean onCall) {
    Map<String, Object> tags = base();
    tags.put("trace.entity", "chat-prompt-response");
    tags.put("chat.id", TASK_ID);
    tags.put("uuid", "session-1");
    tags.put("x-session-id", "session-1");
    tags.put("chat.prompt", "Investigate this production incident");
    if (onCall) {
      tags.put("oncall.task_id", TASK_ID);
    }
    span(ROOT, "chat-prompt-response", tags, null);
  }

  private void coordinatorSpan() {
    Map<String, Object> tags = base();
    tags.put("trace.entity", "agent-invocation");
    tags.put("agent.slug", "coordinator");
    tags.put("agent.display_name", "Coordinator Agent");
    tags.put("oncall.task_id", TASK_ID);
    span(COORDINATOR, "agent:Coordinator Agent", tags, ROOT);
  }

  private void coordinatorTurn(String spanId, int input, int output) {
    Map<String, Object> tags = tokens(base(), input, output);
    tags.put("agent.slug", "coordinator");
    span(spanId, "coordinator-turn", tags, COORDINATOR);
    // The auto-instrumented LLM call of that turn: its usage is already in the turn's total
    llmCall("c" + spanId.substring(1), spanId, input + output);
  }

  private void subAgent(String spanId, String slug, String name, Integer input, Integer output,
      String parent) {
    Map<String, Object> tags = base();
    if (input != null) {
      tokens(tags, input, output);
    }
    tags.put("trace.entity", "agent-invocation");
    tags.put("agent.slug", slug);
    tags.put("agent.display_name", name);
    span(spanId, "agent:" + name, tags, parent);
  }

  private void llmCall(String spanId, String parent, int total) {
    Map<String, Object> tags = new LinkedHashMap<>();
    tags.put("span.kind", "client");
    tags.put("llm.usage.total_tokens", total);
    tags.put("gen_ai.usage.prompt_tokens", total - 10);
    tags.put("gen_ai.usage.completion_tokens", 10);
    span(spanId, "ChatAnthropic.chat", tags, parent);
  }

  private void resultSpan(int input, int output) {
    Map<String, Object> tags = tokens(base(), input, output);
    tags.put("trace.entity", "chat-prompt-response");
    tags.put("chat.prompt", "Investigate this production incident");
    tags.put("chat.response", "Root cause: connection pool exhausted");
    tags.put("oncall.agents_used", "insightfinder,arioncall");
    span("a0000000000000ff", "oncall-task-result", tags, ROOT);
  }

  private int total() {
    JSONObject process = new JSONObject();
    process.put("serviceName", "mcp-service");
    JSONObject processes = new JSONObject();
    processes.put("p1", process);
    JSONObject trace = new JSONObject();
    trace.put("spans", new JSONArray(spans));
    trace.put("processes", processes);
    JSONArray data = new JSONArray();
    data.add(trace);
    JSONObject raw = new JSONObject();
    raw.put("data", data);
    TraceInfo info = TraceDataMapper.getInstance().fromRawJaegerData(raw,
        new com.insightfinder.model.message.TraceInfo("t1", "u", "p", "s", "l"));
    assertThat(info).isNotNull();
    return info.getTraceDataBody().getTotalToken();
  }

  /** The 172c7 run up to (not including) its result span. */
  private void completeRunWithoutResult() {
    root(true);
    coordinatorSpan();
    coordinatorTurn("b000000000000001", 7409, 375);
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 194931, 4335, COORDINATOR);
    llmCall("d000000000000001", IT_AGENT, 24536);
    coordinatorTurn("b000000000000002", 9201, 1065);
    subAgent("a000000000000004", "arioncall", "ARI On-Call Agent", null, null, COORDINATOR);
    coordinatorTurn("b000000000000003", 4913, 426);
  }

  // ── Tests ─────────────────────────────────────────────────────────────────────────────────

  @Test
  void completeRunKeepsTheResultSpanTotalAndAddsNothing() {
    completeRunWithoutResult();
    resultSpan(216454, 6201);

    assertThat(total()).isEqualTo(222655);
  }

  @Test
  void runWithoutResultSpanSumsTurnsAndSubAgentsToTheSameTotal() {
    completeRunWithoutResult();

    // 7784 + 10266 + 5339 + 199266: what the result span would have carried
    assertThat(total()).isEqualTo(222655);
  }

  @Test
  void partialSendThenFullSendEndsWithTheResultTotalNotTheirSum() {
    root(true);
    coordinatorSpan();
    coordinatorTurn("b000000000000001", 7409, 375);
    assertThat(total()).isEqualTo(7784);

    // Each send re-reads the whole trace from Jaeger and recomputes; UIE overwrites the row
    spans.clear();
    clock = 1_000_000L;
    completeRunWithoutResult();
    resultSpan(216454, 6201);
    assertThat(total()).isEqualTo(222655);
  }

  @Test
  void trace6cf87WithCoordinatorAndResultLostShowsTheItAgentTokens() {
    root(true);
    // parent COORDINATOR never reached Jaeger
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 53796, 380, COORDINATOR);
    llmCall("d000000000000001", IT_AGENT, 26205);
    llmCall("d000000000000002", IT_AGENT, 27971);

    assertThat(total()).isEqualTo(54176);
  }

  @Test
  void traceFc0ffWithResultLostShowsTheItAgentTokens() {
    root(true);
    coordinatorSpan();
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 175000, 4252, COORDINATOR);

    assertThat(total()).isEqualTo(179252);
  }

  @Test
  void onCallMarkerOnTheCoordinatorSpanAloneIsEnough() {
    root(false);
    coordinatorSpan();
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 1000, 200, COORDINATOR);

    assertThat(total()).isEqualTo(1200);
  }

  @Test
  void subAgentNestedInAnotherSubAgentIsCountedOnce() {
    root(true);
    coordinatorSpan();
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 900, 100, COORDINATOR);
    subAgent("a000000000000005", "github", "Code Agent", 250, 50, IT_AGENT);

    assertThat(total()).isEqualTo(1000);
  }

  @Test
  void nestedSubAgentCountedOnceEvenWhenTheSpanBetweenIsLost() {
    root(true);
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 900, 100, COORDINATOR);
    Map<String, Object> taskTags = base();
    span("a000000000000006", "tools.task", taskTags, IT_AGENT);
    subAgent("a000000000000005", "github", "Code Agent", 250, 50, "a000000000000006");

    assertThat(total()).isEqualTo(1000);
  }

  @Test
  void llmCallSpanCarryingTotalTokensIsNeverCounted() {
    root(true);
    coordinatorSpan();
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 900, 100, COORDINATOR);
    Map<String, Object> tags = tokens(new LinkedHashMap<>(), 500, 20);
    tags.put("span.kind", "client");
    span("d000000000000009", "ChatAnthropic.chat", tags, IT_AGENT);

    assertThat(total()).isEqualTo(1000);
  }

  @Test
  void stringTokenValueIsCounted() {
    root(true);
    coordinatorSpan();
    Map<String, Object> tags = base();
    tags.put("trace.entity", "agent-invocation");
    tags.put("agent.slug", "insightfinder");
    tags.put("total_tokens", "54176");
    span(IT_AGENT, "agent:IT Infrastructure Agent", tags, COORDINATOR);

    assertThat(total()).isEqualTo(54176);
  }

  @Test
  void runThatFailedBeforeAnyAgentShowsTheCoordinatorTurns() {
    root(true);
    coordinatorSpan();
    coordinatorTurn("b000000000000001", 7409, 375);

    assertThat(total()).isEqualTo(7784);
  }

  @Test
  void onCallTraceWithNoTokenSpansStaysZero() {
    root(true);
    coordinatorSpan();

    assertThat(total()).isZero();
  }

  @Test
  void nonOnCallTraceIsUnchanged() {
    // A chat trace whose total is 0 keeps 0: the fallback is for on-call traces only
    root(false);
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 900, 100, ROOT);

    assertThat(total()).isZero();
  }

  @Test
  void nonOnCallTraceWithRootTotalIsUnchanged() {
    Map<String, Object> tags = tokens(base(), 400, 100);
    tags.put("trace.entity", "chat-prompt-response");
    tags.put("chat.prompt", "hello");
    span(ROOT, "chat-prompt-response", tags, null);
    subAgent(IT_AGENT, "insightfinder", "IT Infrastructure Agent", 300, 50, ROOT);

    assertThat(total()).isEqualTo(500);
  }
}
