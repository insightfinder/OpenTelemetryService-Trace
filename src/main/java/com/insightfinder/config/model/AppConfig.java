package com.insightfinder.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {

  private int traceWorkerNum;
  private long traceDelayInMillis;
  private boolean enableTraceLog;
  private boolean enablePromptLog;
  private TLS tls;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TLS {

    private boolean enabled;
    private String certificateFile;
    private String privateKeyFile;
  }
}
