package com.insightfinder.config.model;

import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class PromptExtractionConfig {

  private String processPath;
  private Set<String> processNames;
  private Map<String, PromptConfig> promptConfig;
}
