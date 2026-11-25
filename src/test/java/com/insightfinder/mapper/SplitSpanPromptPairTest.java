package com.insightfinder.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.config.model.SplitSpanPromptConfig;
import com.insightfinder.model.request.ContentData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Prompt on one span, response on another: the flat "one span per chat message" trace shape
 * (trace id = thread id, tags type / seq / content, no parent links).
 */
class SplitSpanPromptPairTest {

  // ---------- helpers ----------

  private static SplitSpanPromptConfig config(String mode) {
    SplitSpanPromptConfig c = new SplitSpanPromptConfig();
    c.setEnabled(true);
    c.setTypePath("tags.type");
    c.setInputType("user_message");
    c.setOutputType("agent_message");
    c.setInputField("tags.content");
    c.setOutputField("tags.content");
    c.setOrderPath("tags.seq");
    c.setResponseMode(mode);
    c.setResponseSeparator("\n\n");
    return c;
  }

  private static Map<String, Object> attrs(int seq, String type, String content) {
    Map<String, Object> tags = new LinkedHashMap<>();
    tags.put("span.kind", "internal");
    tags.put("seq", seq);
    tags.put("type", type);
    if (content != null) {
      tags.put("content", content);
    }
    return tags;
  }

  private static List<SplitSpanMessage> messages(SplitSpanPromptConfig cfg,
      List<Map<String, Object>> spans) {
    List<SplitSpanMessage> out = new ArrayList<>();
    for (Map<String, Object> s : spans) {
      int seq = (Integer) s.get("seq");
      SplitSpanMessage m = SplitSpanPromptPairer.fromSpan(cfg, s, "span-" + seq, seq * 1000L,
          1000L, null, null);
      if (m != null) {
        out.add(m);
      }
    }
    return out;
  }

  /** Two turns, tool calls in between, several agent messages in turn 2, then an end span. */
  private static List<Map<String, Object>> twoTurnThread() {
    return List.of(
        attrs(1, "user_message", "Q1"),
        attrs(2, "tool_call", null),
        attrs(3, "tool_call", null),
        attrs(4, "agent_message", "A1"),
        attrs(5, "user_message", "Q2"),
        attrs(6, "agent_message", "A2-interim"),
        attrs(7, "tool_call", null),
        attrs(8, "agent_message", "A2-middle"),
        attrs(9, "agent_message", "A2-final"),
        attrs(10, "end", null));
  }

  // ---------- pairer ----------

  @Test
  void allJoinedPairsEachPromptWithAllRepliesUntilNextPrompt() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    List<ContentData> pairs = SplitSpanPromptPairer.pair(messages(cfg, twoTurnThread()), cfg);

    assertThat(pairs).hasSize(2);
    assertThat(pairs.get(0).getInputPrompt().getPrompt()).isEqualTo("Q1");
    assertThat(pairs.get(0).getResponseRecord().getResponse()).isEqualTo("A1");
    assertThat(pairs.get(1).getInputPrompt().getPrompt()).isEqualTo("Q2");
    assertThat(pairs.get(1).getResponseRecord().getResponse())
        .isEqualTo("A2-interim\n\nA2-middle\n\nA2-final");
  }

  @Test
  void firstAndLastModesPickOneReply() {
    SplitSpanPromptConfig first = config("FIRST");
    SplitSpanPromptConfig last = config("last");

    assertThat(SplitSpanPromptPairer.pair(messages(first, twoTurnThread()), first).get(1)
        .getResponseRecord().getResponse()).isEqualTo("A2-interim");
    assertThat(SplitSpanPromptPairer.pair(messages(last, twoTurnThread()), last).get(1)
        .getResponseRecord().getResponse()).isEqualTo("A2-final");
  }

  @Test
  void promptAndResponseKeepTheirOwnSpans() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    ContentData turn2 = SplitSpanPromptPairer.pair(messages(cfg, twoTurnThread()), cfg).get(1);

    assertThat(turn2.getInputPrompt().getSpanId()).isEqualTo("span-5");
    assertThat(turn2.getInputPrompt().getStartTime()).isEqualTo(5000L);
    assertThat(turn2.getResponseRecord().getSpanId()).isEqualTo("span-6");
    assertThat(turn2.getResponseRecord().getStartTime()).isEqualTo(6000L);
    // from start of first reply (6000) to end of last reply (9000 + 1000)
    assertThat(turn2.getResponseRecord().getDuration()).isEqualTo(4000L);
  }

  @Test
  void otherSpanTypesAreNotMessages() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    assertThat(messages(cfg, twoTurnThread())).extracting(SplitSpanMessage::content)
        .containsExactly("Q1", "A1", "Q2", "A2-interim", "A2-middle", "A2-final");
  }

  @Test
  void spansOutOfOrderArePairedBySeq() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    List<SplitSpanMessage> shuffled = new ArrayList<>(messages(cfg, twoTurnThread()));
    shuffled.sort(Comparator.comparing(SplitSpanMessage::content).reversed());

    List<ContentData> pairs = SplitSpanPromptPairer.pair(shuffled, cfg);
    assertThat(pairs.get(0).getResponseRecord().getResponse()).isEqualTo("A1");
    assertThat(pairs.get(1).getResponseRecord().getResponse())
        .isEqualTo("A2-interim\n\nA2-middle\n\nA2-final");
  }

  @Test
  void orderFallsBackToStartTimeWhenOrderPathMissing() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    cfg.setOrderPath(null);
    List<SplitSpanMessage> msgs = new ArrayList<>(messages(cfg, twoTurnThread()));
    java.util.Collections.reverse(msgs);

    List<ContentData> pairs = SplitSpanPromptPairer.pair(msgs, cfg);
    assertThat(pairs).hasSize(2);
    assertThat(pairs.get(0).getInputPrompt().getPrompt()).isEqualTo("Q1");
    assertThat(pairs.get(0).getResponseRecord().getResponse()).isEqualTo("A1");
  }

  @Test
  void promptWithoutReplyGivesNoPair() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    List<Map<String, Object>> spans = List.of(
        attrs(1, "user_message", "Q1"),
        attrs(2, "agent_message", "A1"),
        attrs(3, "user_message", "Q2 not answered yet"));

    List<ContentData> pairs = SplitSpanPromptPairer.pair(messages(cfg, spans), cfg);
    assertThat(pairs).hasSize(1);
    assertThat(pairs.getFirst().getInputPrompt().getPrompt()).isEqualTo("Q1");
  }

  @Test
  void repliesBeforeFirstPromptAreIgnored() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    List<Map<String, Object>> spans = List.of(
        attrs(1, "agent_message", "greeting"),
        attrs(2, "user_message", "Q1"),
        attrs(3, "agent_message", "A1"));

    List<ContentData> pairs = SplitSpanPromptPairer.pair(messages(cfg, spans), cfg);
    assertThat(pairs).hasSize(1);
    assertThat(pairs.getFirst().getResponseRecord().getResponse()).isEqualTo("A1");
  }

  @Test
  void spanTokenAttributesAreUsedWhenPresent() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    List<SplitSpanMessage> msgs = List.of(
        SplitSpanPromptPairer.fromSpan(cfg, attrs(1, "user_message", "Q1"), "s1", 1000, 1000,
            11, null),
        SplitSpanPromptPairer.fromSpan(cfg, attrs(2, "agent_message", "A1"), "s2", 2000, 1000,
            null, 20),
        SplitSpanPromptPairer.fromSpan(cfg, attrs(3, "agent_message", "A2"), "s3", 3000, 1000,
            null, 30));

    ContentData pair = SplitSpanPromptPairer.pair(msgs, cfg).getFirst();
    assertThat(pair.getInputPrompt().getTokenCount()).isEqualTo(11);
    assertThat(pair.getResponseRecord().getTokenCount()).isEqualTo(50);
  }

  @Test
  void tokenizerIsUsedWhenNoTokenAttributes() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    List<Map<String, Object>> spans = List.of(
        attrs(1, "user_message", "three word prompt"),
        attrs(2, "agent_message", "two words"));

    ContentData pair = SplitSpanPromptPairer.pair(messages(cfg, spans), cfg).getFirst();
    assertThat(pair.getInputPrompt().getTokenCount()).isGreaterThan(0);
    assertThat(pair.getResponseRecord().getTokenCount()).isGreaterThan(0);
  }

  @Test
  void disabledOrMissingConfigGivesNothing() {
    SplitSpanPromptConfig disabled = config("ALL_JOINED");
    disabled.setEnabled(false);
    SplitSpanPromptConfig enabled = config("ALL_JOINED");
    List<SplitSpanMessage> msgs = messages(enabled, twoTurnThread());

    assertThat(SplitSpanPromptPairer.fromSpan(disabled, attrs(1, "user_message", "Q1"), "s", 0,
        0, null, null)).isNull();
    assertThat(SplitSpanPromptPairer.fromSpan(null, attrs(1, "user_message", "Q1"), "s", 0, 0,
        null, null)).isNull();
    assertThat(SplitSpanPromptPairer.pair(msgs, disabled)).isEmpty();
    assertThat(SplitSpanPromptPairer.pair(msgs, null)).isEmpty();
  }

  @Test
  void tagNamesAndValuesComeFromConfig() {
    SplitSpanPromptConfig cfg = config("ALL_JOINED");
    cfg.setTypePath("tags.role");
    cfg.setInputType("human");
    cfg.setOutputType("ai");
    cfg.setInputField("tags.question");
    cfg.setOutputField("tags.answer");
    cfg.setOrderPath("tags.n");

    Map<String, Object> q = new LinkedHashMap<>(Map.of("role", "human", "question", "Q", "n", "1"));
    Map<String, Object> a = new LinkedHashMap<>(Map.of("role", "ai", "answer", "A", "n", "2"));
    List<SplitSpanMessage> msgs = List.of(
        SplitSpanPromptPairer.fromSpan(cfg, a, "a", 0, 1, null, null),
        SplitSpanPromptPairer.fromSpan(cfg, q, "q", 0, 1, null, null));

    ContentData pair = SplitSpanPromptPairer.pair(msgs, cfg).getFirst();
    assertThat(pair.getInputPrompt().getPrompt()).isEqualTo("Q");
    assertThat(pair.getResponseRecord().getResponse()).isEqualTo("A");
  }

  // ---------- whole mapper, test application.yml (splitSpan enabled, ALL_JOINED) ----------

  private static JSONObject jaegerSpan(String spanId, long startMicros, Map<String, Object> tags) {
    JSONArray tagArray = new JSONArray();
    tags.forEach((k, v) -> {
      JSONObject tag = new JSONObject();
      tag.put("key", k);
      tag.put("value", v);
      tagArray.add(tag);
    });
    JSONObject span = new JSONObject();
    span.put("traceID", "e00d75a695d9");
    span.put("spanID", spanId);
    span.put("operationName", (String) tags.get("type"));
    span.put("startTime", startMicros);
    span.put("duration", 1_000_000L);
    span.put("tags", tagArray);
    span.put("references", new JSONArray()); // flat trace: every span is a root
    return span;
  }

  @Test
  void flatChatThreadTraceGivesOnePairPerUserMessage() {
    JSONArray spans = new JSONArray();
    for (Map<String, Object> tags : twoTurnThread()) {
      int seq = (Integer) tags.get("seq");
      spans.add(jaegerSpan("s" + seq, seq * 1_000_000L, tags));
    }
    JSONObject process = new JSONObject();
    process.put("serviceName", "evenup-agent");
    JSONObject processes = new JSONObject();
    processes.put("p1", process);
    JSONObject trace = new JSONObject();
    trace.put("spans", spans);
    trace.put("processes", processes);
    JSONArray data = new JSONArray();
    data.add(trace);
    JSONObject raw = new JSONObject();
    raw.put("data", data);

    TraceInfo info = TraceDataMapper.getInstance().fromRawJaegerData(raw,
        new com.insightfinder.model.message.TraceInfo("e00d75a695d9", "u", "p", "s", "l"));

    assertThat(info).isNotNull();
    List<ContentData> pairs = new ArrayList<>(info.getPromptResponsePairs());
    pairs.sort(Comparator.comparing(p -> p.getInputPrompt().getPrompt()));
    assertThat(pairs).hasSize(2);
    assertThat(pairs.get(0).getInputPrompt().getPrompt()).isEqualTo("Q1");
    assertThat(pairs.get(0).getInputPrompt().getSpanId()).isEqualTo("s1");
    assertThat(pairs.get(0).getResponseRecord().getResponse()).isEqualTo("A1");
    assertThat(pairs.get(0).getResponseRecord().getSpanId()).isEqualTo("s4");
    assertThat(pairs.get(1).getInputPrompt().getPrompt()).isEqualTo("Q2");
    assertThat(pairs.get(1).getResponseRecord().getResponse())
        .isEqualTo("A2-interim\n\nA2-middle\n\nA2-final");
    assertThat(pairs).allSatisfy(p -> {
      assertThat(p.getTraceId()).isEqualTo("e00d75a695d9");
      assertThat(p.getInstanceName()).isEqualTo("evenup-agent");
    });
  }
}
