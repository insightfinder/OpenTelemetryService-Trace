package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class IFLogTracePromptDataReceivePayload {

  @JSONField(name = "userName")
  private String userName;

  @JSONField(name = "projectName")
  private String projectName;

  @JSONField(name = "licenseKey")
  private String licenseKey;

  @JSONField(name = "logTracePromptData")
  private String logTracePromptDataList;

  @JSONField(name = "agentType")
  private String insightAgentType;
}
