package com.insightfinder.config.model;

import com.insightfinder.model.ValueType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValueMapping {

  private String filedPath;
  private ValueType rawDataType;
}
