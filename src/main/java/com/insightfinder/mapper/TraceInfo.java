package com.insightfinder.mapper;

import com.insightfinder.model.request.ContentData;
import com.insightfinder.model.request.TraceDataBody;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraceInfo {

  private final TraceDataBody traceDataBody;
  private final List<ContentData> promptResponsePairs;
}
