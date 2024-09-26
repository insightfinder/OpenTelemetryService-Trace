package com.insightfinder.model.request;
import com.alibaba.fastjson2.annotation.JSONField;
import java.util.Map;

public class SpanDataBody {

    @JSONField(name = "traceID")
    public String traceID;

    @JSONField(name = "spanID")
    public String spanID;

    @JSONField(name = "operationName")
    public String operationName;

    @JSONField(name = "startTime")
    public long startTime;

    @JSONField(name = "duration")
    public long duration;

    // TODO: total_tokens


    @JSONField(name = "attributes")
    public Map<String, Object> attributes;

    // TODO: prompt_response -> The tag in the attrList

    @JSONField(name = "parentSpanId")
    public String parentSpanId;

    @JSONField(name = "childSpans")
    public Map<String, SpanDataBody> childSpans;

}



