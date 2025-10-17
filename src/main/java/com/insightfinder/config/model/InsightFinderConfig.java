package com.insightfinder.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsightFinderConfig {

  private String serverUrl;
  private String serverUri;
  private String checkAndCreateUri;
  private String promptUri;
  private String promptProjectName;
  private String promptSystemName;
  private int connectTimeout;
  private int readTimeout;
  private int writeTimeout;
}
