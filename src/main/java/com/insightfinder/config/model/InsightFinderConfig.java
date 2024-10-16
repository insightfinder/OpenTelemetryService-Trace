package com.insightfinder.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsightFinderConfig {

  private String username;
  private String serverUrl;
  private String serverUri;
  private String checkAndCreateUri;
  private String licenseKey;
}
