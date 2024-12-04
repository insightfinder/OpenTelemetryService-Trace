package com.insightfinder.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InputPrompt extends Content {

  private String prompt;

  public InputPrompt(String content) {
    setContent(content);
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
