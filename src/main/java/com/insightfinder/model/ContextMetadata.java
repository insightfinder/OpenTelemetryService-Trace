package com.insightfinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContextMetadata {

  private String username;
  private String projectName;
  private String systemName;
  private String licenseKey;
}
