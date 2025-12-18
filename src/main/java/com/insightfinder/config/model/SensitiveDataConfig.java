package com.insightfinder.config.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SensitiveDataConfig {
  private boolean sensitiveDataFilterEnabled;
  private String sensitiveDataRegex;
  private String replacement;
}
