package model.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.TraceDataBody;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class TraceDataBodyTest {

  private SpanDataBody getExampleSpan(String id, String parentId, String prompt) {
    return SpanDataBody.builder()
        .spanID(id)
        .parentSpanId(parentId)
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
    Map<String, Set<SpanDataBody>> expectedChildSpans = new HashMap<>();
    expectedChildSpans.put("r1", Set.of(childSpan1, childSpan2));
    expectedChildSpans.put("r2", Set.of(childSpan3));
    expectedChildSpans.put("c3", Set.of(childSpan4));
    assertThat(trace.getSpans()).containsExactlyEntriesOf(expectedSpan);
    assertThat(trace.getChildSpans()).containsExactlyEntriesOf(expectedChildSpans);
    assertThat(trace.getTotalToken()).isEqualTo(38);
  }

  @Test
  void testBuildSpanTreeNoChildSpan() {
    SpanDataBody root1 = getExampleSpan("r1", null, "parent1");
    SpanDataBody root2 = getExampleSpan("r2", null, "parent2");
    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(root1);
    trace.addSpan(root2);

    trace.composeSpanRelations();

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

    trace.composeSpanRelations();

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
    expectedChildSpan4.addChildSpans(Set.of(expectedChildSpan6, expectedChildSpan8));
    expectedChildSpan1.addChildSpans(
        Set.of(expectedChildSpan4, expectedChildSpan5, expectedChildSpan9));
    expectedChildSpan2.addChildSpans(Set.of(expectedChildSpan7));
    expectedRoot.addChildSpans(Set.of(expectedChildSpan1, expectedChildSpan2, expectedChildSpan3));
    expectedSpan.put("r1", expectedRoot);

    assertThat(trace.getSpans()).containsExactlyEntriesOf(expectedSpan);
  }
}
