package com.insightfinder.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResponseRecord extends Content {

  private String response;

  public ResponseRecord(String content, int tokenCount) {
    setContent(content);
    setTokenCount(tokenCount);
  }

  @Override
  String getContent() {
    return response;
  }

  @Override
  void setContent(String content) {
    this.response = content;
  }

}
