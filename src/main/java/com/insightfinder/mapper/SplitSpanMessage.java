package com.insightfinder.mapper;

/**
 * One prompt or response span found by split-span extraction.
 *
 * @param input      true for a prompt span, false for a response span
 * @param content    the prompt or response text
 * @param order      value of the configured order attribute, null when absent
 * @param tokenCount token count from the span's attributes, null when absent
 */
public record SplitSpanMessage(boolean input, String content, String spanId, long startTime,
                               long duration, Long order, Integer tokenCount) {

}
