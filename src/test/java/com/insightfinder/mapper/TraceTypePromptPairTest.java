package com.insightfinder.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.insightfinder.model.request.ContentData;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * AW-2555: the root span's optional "type" attribute (e.g. "news") travels on every prompt pair, so
 * UIE can skip span-level evaluations for news. src/test/resources/application.yml has no
 * attrMapping.trace_type, so the default (the "type" attribute) is used.
 */
class TraceTypePromptPairTest {

  private static JSONObject span(String spanId, long startTime, Map<String, Object> tags,
      String parentSpanId) {
    JSONArray tagArray = new JSONArray();
    tags.forEach((k, v) -> {
      JSONObject tag = new JSONObject();
      tag.put("key", k);
      tag.put("value", v);
      tagArray.add(tag);
    });
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
    span.put("operationName", "news");
    span.put("startTime", startTime);
    span.put("duration", 1000L);
    span.put("tags", tagArray);
    span.put("references", references);
    return span;
  }

  private static Map<String, Object> newsTags(String type) {
    Map<String, Object> tags = new LinkedHashMap<>();
    tags.put("span.kind", "internal");
    tags.put("x-session-id", "news-ad626b36");
    tags.put("chat.prompt", "Nvidia value climbs by almost $1tn after Jensen Huang returns");
    tags.put("chat.response", "Nvidia's market value rose by nearly $1 trillion.");
    if (type != null) {
      tags.put("type", type);
    }
    return tags;
  }

  private static TraceInfo map(JSONObject... spanList) {
    JSONArray spans = new JSONArray();
    for (JSONObject s : spanList) {
      spans.add(s);
    }
    JSONObject process = new JSONObject();
    process.put("serviceName", "news-ingest");
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
  void rootTypeTravelsOnThePromptPair() {
    TraceInfo info = map(span("news-00046", 1_000_000L, newsTags("news"), null));

    assertThat(info.getPromptResponsePairs()).hasSize(1);
    ContentData pair = info.getPromptResponsePairs().getFirst();
    assertThat(pair.getTraceType()).isEqualTo("news");
    assertThat(pair.getSessionId()).isEqualTo("news-ad626b36");
  }

  @Test
  void noTypeIsNullAndNothingElseChanges() {
    TraceInfo info = map(span("s1", 1_000_000L, newsTags(null), null));

    ContentData pair = info.getPromptResponsePairs().getFirst();
    assertThat(pair.getTraceType()).isNull();
    assertThat(pair.getInputPrompt().getPrompt())
        .isEqualTo("Nvidia value climbs by almost $1tn after Jensen Huang returns");
  }

  @Test
  void onlyTheRootSpansTypeCounts() {
    Map<String, Object> root = new LinkedHashMap<>(newsTags(null));
    Map<String, Object> child = new LinkedHashMap<>();
    child.put("span.kind", "internal");
    child.put("type", "llm");
    TraceInfo info = map(span("child", 2_000_000L, child, "root"),
        span("root", 1_000_000L, root, null));

    assertThat(info.getPromptResponsePairs().getFirst().getTraceType()).isNull();
  }
}
