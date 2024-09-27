package com.insightfinder.config.model;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataConfig {

  private Map<String, ValueMapping> attrMapping;
}
