package com.insightfinder.model.request;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class IFLogDataPayload {

    @JSONField(name = "timestamp")
    private long timeStamp;

    @JSONField(name = "tag")
    private String tag;

    @JSONField(name = "data")
    private Object data;

    @JSONField(name = "componentName")
    private String componentName;
}
