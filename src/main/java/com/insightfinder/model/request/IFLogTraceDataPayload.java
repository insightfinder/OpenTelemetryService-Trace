package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.List;
import lombok.Data;

@Data
public class IFLogTraceDataPayload {

  @JSONField(name = "timestamp")
  private String timeStamp;

  @JSONField(name = "instanceName")
  private String instanceName;

  @JSONField(name = "data")
  private Object data;

  @JSONField(name = "componentName")
  private String componentName;

  @JSONField(name = "promptData")
  private LogTracePromptEventLite promptData;

  public void setPromptData(List<ContentData> promptResponsePairs) {
    if (!promptResponsePairs.isEmpty()) {
      var promptResponsePair = promptResponsePairs.getFirst();
      this.promptData = new LogTracePromptEventLite();
      this.promptData.setSpanId(promptResponsePair.getInputPrompt().getSpanId());
      this.promptData.setEntryOperation(promptResponsePair.getEntryOperation());
      this.promptData.setInputPrompt(promptResponsePair.getInputPrompt());
      this.promptData.setResponseRecord(promptResponsePair.getResponseRecord());
      this.promptData.setSessionId(promptResponsePair.getSessionId());
    }
  }

  @Data
  private static class LogTracePromptEventLite {

    private String spanId;
    private String entryOperation;
    private InputPrompt inputPrompt;
    private ResponseRecord responseRecord;
    private String sessionId;
  }
}
