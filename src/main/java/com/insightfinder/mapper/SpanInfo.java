package com.insightfinder.mapper;

import com.insightfinder.model.request.PromptData;
import com.insightfinder.model.request.SpanDataBody;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpanInfo {

  private final SpanDataBody spanDataBody;
  private final PromptData promptData;
}
