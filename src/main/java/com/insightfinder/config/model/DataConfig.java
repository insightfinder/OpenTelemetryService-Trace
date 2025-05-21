package com.insightfinder.config.model;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataConfig {

  private boolean useCustomTokenizer;
  private boolean overwriteTraceAndSpanIdByUUID;
  private boolean overwriteTimestamp;
  private String overwriteUUIDPath;
  private String overwriteTimestampPath;
  private Map<String, ValueMapping> attrMapping;
  private PromptExtractionConfig promptExtraction;
  private UnsuccessResponseExtractionConfig unsuccessResponseExtraction;
}
