package com.insightfinder.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InputPrompt extends Content {

  private String prompt;

  public InputPrompt(String content, int tokenCount) {
    setContent(content);
    setTokenCount(tokenCount);
  }

  @Override
  String getContent() {
    return prompt;
  }

  @Override
  void setContent(String content) {
    this.prompt = content;
  }
}
