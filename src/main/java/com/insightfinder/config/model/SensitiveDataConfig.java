package com.insightfinder.config.model;

import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SensitiveDataConfig {
  private boolean sensitiveDataFilterEnabled= Boolean.FALSE;
  private List<String> sensitiveDataRegex = Collections.emptyList();
  private String replacement = "";
}
