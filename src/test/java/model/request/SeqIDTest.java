package model.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.TraceDataBody;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class SeqIDTest {

  private SpanDataBody getExampleSpan(String id, String parentId, String operationName) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("operation", operationName);
    return SpanDataBody.builder()
        .spanID(id)
        .parentSpanId(parentId)
        .operationName(operationName)
        .startTime(0)
        .duration(0)
        .attributes(attributes)
        .error(false)
        .build();
  }

  @Test
  void testSeqIDAssignment() {
    // Create trace structure:
    // trace_1
    //   ├── span_1 (seqID: 0)
    //   │   ├── span_1_1 (seqID: 0)
    //   │   ├── span_1_2 (seqID: 1)
    //   │   └── span_1_3 (seqID: 2)
    //   └── span_2 (seqID: 1)

    SpanDataBody span1 = getExampleSpan("span_1", null, "operation1");
    SpanDataBody span2 = getExampleSpan("span_2", null, "operation2");
    SpanDataBody span1_1 = getExampleSpan("span_1_1", "span_1", "operation1_1");
    SpanDataBody span1_2 = getExampleSpan("span_1_2", "span_1", "operation1_2");
    SpanDataBody span1_3 = getExampleSpan("span_1_3", "span_1", "operation1_3");

    TraceDataBody trace = new TraceDataBody();
    trace.addSpan(span1);
    trace.addSpan(span2);
    trace.addSpan(span1_1);
    trace.addSpan(span1_2);
    trace.addSpan(span1_3);

    trace.composeSpanRelations(null);

    // Verify seqID assignments
    assertThat(span1.getSeqID()).isEqualTo(0);  // First root span
    assertThat(span2.getSeqID()).isEqualTo(1);  // Second root span

    assertThat(span1_1.getSeqID()).isEqualTo(0);  // First child of span_1
    assertThat(span1_2.getSeqID()).isEqualTo(1);  // Second child of span_1
    assertThat(span1_3.getSeqID()).isEqualTo(2);  // Third child of span_1
  }
}

