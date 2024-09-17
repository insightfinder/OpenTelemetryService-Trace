package com.insightfinder.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JaegerConfig {

  private String serverName;
  private boolean tlsEnabled;
  private int grpcPort;
  private int uiPort;
}
