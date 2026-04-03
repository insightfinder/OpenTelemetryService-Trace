package com.insightfinder.model.request;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpanNode {

  private String spanId;
  private String operationName;
  private List<SpanNode> children;
}
