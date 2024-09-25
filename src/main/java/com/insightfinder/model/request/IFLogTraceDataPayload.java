package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class IFLogTraceDataPayload {

  @JSONField(name = "timestamp")
  private long timeStamp;

  @JSONField(name = "instanceName")
  private String instanceName;

  @JSONField(name = "data")
  private Object data;

  @JSONField(name = "componentName")
  private String componentName;
}
