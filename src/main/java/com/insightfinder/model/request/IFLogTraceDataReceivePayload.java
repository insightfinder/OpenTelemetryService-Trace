package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class IFLogTraceDataReceivePayload {

  @JSONField(name = "userName")
  private String userName;

  @JSONField(name = "projectName")
  private String projectName;

  @JSONField(name = "licenseKey")
  private String licenseKey;

  @JSONField(name = "logTraceData")
  private String logTraceDataList;

  @JSONField(name = "agentType")
  private String insightAgentType;

  @JSONField(name = "minTimestamp")
  private Long minTimestamp;

  @JSONField(name = "maxTimestamp")
  private Long maxTimestamp;
}
