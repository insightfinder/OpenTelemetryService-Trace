package model.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.TraceDataBody;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class TraceDataBodyTest {

  private SpanDataBody getExampleSpan(String id, String parentId, String prompt) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("prompt_response", prompt);
    attributes.put("total_tokens", prompt.length());
    return SpanDataBody.builder()
        .spanID(id)
        .parentSpanId(parentId)
        .attributes(attributes)
        .build();
  }

  @Test
  void testAddSpanToTrace() {
    SpanDataBody root1 = getExampleSpan("r1", null, "parent1");
    SpanDataBody root2 = getExampleSpan("r2", null, "parent2");
    SpanDataBody childSpan1 = getExampleSpan("c1", "r1", "child1");
    SpanDataBody childSpan2 = getExampleSpan("c2", "r1", "child2");
    SpanDataBody childSpan3 = getExampleSpan("c3", "r2", "child3");
    SpanDataBody childSpan4 = getExampleSpan("c4", "c3", "child4");
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(root1);
    trace.addSpan(root2);
    trace.addSpan(childSpan1);
    trace.addSpan(childSpan2);
    trace.addSpan(childSpan3);
    trace.addSpan(childSpan4);
    Map<String, SpanDataBody> expectedSpan = new HashMap<>();
    expectedSpan.put("r1", root1);
    expectedSpan.put("r2", root2);
    Map<String, List<SpanDataBody>> expectedChildSpans = new HashMap<>();
    expectedChildSpans.put("r1", List.of(childSpan1, childSpan2));
    expectedChildSpans.put("r2", List.of(childSpan3));
    expectedChildSpans.put("c3", List.of(childSpan4));
    assertThat(trace.getSpans()).containsExactlyEntriesOf(expectedSpan);
    assertThat(trace.getChildSpans()).containsExactlyEntriesOf(expectedChildSpans);
    // Only root spans (r1=7, r2=7) contribute to the trace total now — child spans no longer
    // count, since they can carry an already-summed value duplicated from their root (the bug
    // this method fixes for multi-agent turns, where a child agent span repeats the root's total).
    assertThat(trace.getTotalToken()).isEqualTo(14);
  }

  @Test
  void testBuildSpanTreeNoChildSpan() {
    SpanDataBody root1 = getExampleSpan("r1", null, "parent1");
    SpanDataBody root2 = getExampleSpan("r2", null, "parent2");
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(root1);
    trace.addSpan(root2);

    trace.composeSpanRelations(null);

    Map<String, SpanDataBody> expectedSpan = new HashMap<>();
    expectedSpan.put("r1", root1);
    expectedSpan.put("r2", root2);
    assertThat(trace.getSpans()).containsExactlyEntriesOf(expectedSpan);
  }

  // @formatter:off
  /**
   *                        r1
   *                 /      |      \
   *                c1      c2     c3
   *              / | \     |
   *            c4  c5 c9   c7
   *           / \
   *          c6  c8
   */
  // @formatter:on
  @Test
  void testBuildSpanTreeOneTree() {
    SpanDataBody root1 = getExampleSpan("r1", null, "parent1");
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(root1);
    SpanDataBody childSpan1 = getExampleSpan("c1", "r1", "child1");
    SpanDataBody childSpan2 = getExampleSpan("c2", "r1", "child2");
    SpanDataBody childSpan3 = getExampleSpan("c3", "r1", "child3");
    SpanDataBody childSpan4 = getExampleSpan("c4", "c1", "child4");
    SpanDataBody childSpan5 = getExampleSpan("c5", "c1", "child5");
    SpanDataBody childSpan6 = getExampleSpan("c6", "c4", "child6");
    SpanDataBody childSpan7 = getExampleSpan("c7", "c2", "child7");
    SpanDataBody childSpan8 = getExampleSpan("c8", "c4", "child8");
    SpanDataBody childSpan9 = getExampleSpan("c9", "c1", "child9");
    trace.addSpan(childSpan1);
    trace.addSpan(childSpan2);
    trace.addSpan(childSpan3);
    trace.addSpan(childSpan4);
    trace.addSpan(childSpan5);
    trace.addSpan(childSpan6);
    trace.addSpan(childSpan7);
    trace.addSpan(childSpan8);
    trace.addSpan(childSpan9);

    trace.composeSpanRelations(null);

    Map<String, SpanDataBody> expectedSpan = new HashMap<>();
    SpanDataBody expectedRoot = getExampleSpan("r1", null, "parent1");
    SpanDataBody expectedChildSpan1 = getExampleSpan("c1", "r1", "child1");
    SpanDataBody expectedChildSpan2 = getExampleSpan("c2", "r1", "child2");
    SpanDataBody expectedChildSpan3 = getExampleSpan("c3", "r1", "child3");
    SpanDataBody expectedChildSpan4 = getExampleSpan("c4", "c1", "child4");
    SpanDataBody expectedChildSpan5 = getExampleSpan("c5", "c1", "child5");
    SpanDataBody expectedChildSpan6 = getExampleSpan("c6", "c4", "child6");
    SpanDataBody expectedChildSpan7 = getExampleSpan("c7", "c2", "child7");
    SpanDataBody expectedChildSpan8 = getExampleSpan("c8", "c4", "child8");
    SpanDataBody expectedChildSpan9 = getExampleSpan("c9", "c1", "child9");
    expectedChildSpan4.addChildSpans(List.of(expectedChildSpan6, expectedChildSpan8));
    expectedChildSpan1.addChildSpans(
        List.of(expectedChildSpan4, expectedChildSpan5, expectedChildSpan9));
    expectedChildSpan2.addChildSpans(List.of(expectedChildSpan7));
    expectedRoot.addChildSpans(List.of(expectedChildSpan1, expectedChildSpan2, expectedChildSpan3));
    expectedSpan.put("r1", expectedRoot);

    assertThat(trace.getSpans()).containsExactlyEntriesOf(expectedSpan);
  }

  private SpanDataBody getTokenSpan(String id, String parentId, String traceEntity,
      Integer totalTokens) {
    Map<String, Object> attributes = new HashMap<>();
    if (traceEntity != null) {
      attributes.put("trace.entity", traceEntity);
    }
    if (totalTokens != null) {
      attributes.put("total_tokens", totalTokens);
    }
    return SpanDataBody.builder().spanID(id).parentSpanId(parentId).attributes(attributes)
        .build();
  }

  @Test
  void onCallResultChildSpanTokensAreCounted() {
    // On-call: the root ends at once with no tokens; the totals are on "oncall-task-result"
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(getTokenSpan("root", null, "chat-prompt-response", null));
    trace.addSpan(getTokenSpan("result", "root", "chat-prompt-response", 470441));
    assertThat(trace.getTotalToken()).isEqualTo(470441);
  }

  @Test
  void agentInvocationChildSpanTokensAreNotCounted() {
    // Keeps the double-count fix: agent spans repeat tokens already in the trace total
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(getTokenSpan("root", null, "chat-prompt-response", 100));
    trace.addSpan(getTokenSpan("agent", "root", "agent-invocation", 100));
    assertThat(trace.getTotalToken()).isEqualTo(100);
  }

  @Test
  void childSpanWithoutTraceEntityIsNotCounted() {
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(getTokenSpan("root", null, null, 50));
    trace.addSpan(getTokenSpan("llm", "root", null, 30));
    assertThat(trace.getTotalToken()).isEqualTo(50);
  }

  @Test
  void rootAndPromptResponseChildAreSummed() {
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(getTokenSpan("root", null, "chat-prompt-response", 10));
    trace.addSpan(getTokenSpan("result", "root", "chat-prompt-response", 20));
    assertThat(trace.getTotalToken()).isEqualTo(30);
  }

  @Test
  void promptResponseChildWithoutTokensAddsNothing() {
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(getTokenSpan("root", null, "chat-prompt-response", 10));
    trace.addSpan(getTokenSpan("result", "root", "chat-prompt-response", null));
    assertThat(trace.getTotalToken()).isEqualTo(10);
  }

  @Test
  void childSpanWithNullAttributesIsNotCounted() {
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(getTokenSpan("root", null, "chat-prompt-response", 10));
    trace.addSpan(SpanDataBody.builder().spanID("c").parentSpanId("root").attributes(null)
        .build());
    assertThat(trace.getTotalToken()).isEqualTo(10);
  }
}
