package com.insightfinder.config.model;

import java.util.Map;
import lombok.Data;

@Data
public class PromptExtractionConfig {

  private String processPath;
  private String processName;
  private Map<String, PromptConfig> promptConfig;
}
