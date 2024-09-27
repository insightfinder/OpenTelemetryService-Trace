package com.insightfinder.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigModel {

  private GrpcConfig grpc;
  private InsightFinderConfig insightFinder;
  private JaegerConfig jaeger;
  private AppConfig app;
  private DataConfig data;
}
