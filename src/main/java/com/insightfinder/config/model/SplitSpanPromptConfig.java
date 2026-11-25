package com.insightfinder.config.model;

import lombok.Data;

/**
 * Prompt/response extraction for traces where the prompt and the response are on different
 * spans (e.g. one span per chat message). All tag names and values come from config.
 *
 * <pre>
 * splitSpan:
 *   enabled: true
 *   typePath: "tags.type"            # attribute that says what kind of span this is
 *   inputType: "user_message"        # typePath value of a prompt span
 *   outputType: "agent_message"      # typePath value of a response span
 *   inputField: "tags.content"       # prompt text on a prompt span
 *   outputField: "tags.content"      # response text on a response span
 *   orderPath: "tags.seq"            # optional; numeric order of spans, falls back to startTime
 *   responseMode: "ALL_JOINED"       # ALL_JOINED | FIRST | LAST (responses between two prompts)
 *   responseSeparator: "\n\n"        # used by ALL_JOINED
 * </pre>
 */
@Data
public class SplitSpanPromptConfig {

  private boolean enabled;
  private String typePath;
  private String inputType;
  private String outputType;
  private String inputField;
  private String outputField;
  private String orderPath;
  private String responseMode;
  private String responseSeparator;
}
