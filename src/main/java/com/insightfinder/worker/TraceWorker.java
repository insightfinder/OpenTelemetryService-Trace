package com.insightfinder.worker;

import com.insightfinder.GrpcServer;
import com.insightfinder.model.message.Message;
import com.insightfinder.model.request.SpanDataBody;
import com.insightfinder.model.request.TraceDataBody;
import com.insightfinder.service.InsightFinderService;
import com.insightfinder.service.JaegerService;
import com.insightfinder.util.ParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class TraceWorker implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(TraceWorker.class);

    // TODO: Move those settings to configuration files instead of using them here.
    private final InsightFinderService InsightFinder = new InsightFinderService("https://stg.insightfinder.com");
    private final JaegerService Jaeger = new JaegerService("18.212.200.99",false,4317,16686);

    public TraceWorker(int threadNum){
        LOG.info("Trace Worker thread " + threadNum + " started.");
    }

    @Override
    public void run() {
        Message message = null;

        while(true){
            // Get task from the queue.
            try{
                message = GrpcServer.queue.take();
            }catch (Exception e){
                LOG.error(e.getMessage());
            }

            if(message == null){
                continue;
            }

            LOG.info("Prepare to send trace '%s' to project '%s' for user '%s'.".formatted(message.traceId,message.ifProject,message.ifUser));

            // Create Project IfNotExist and save the result to cache.
            if(GrpcServer.projectLocalCache.get(message.ifProject) == null){
                boolean isProjectCreated = InsightFinder.createProjectIfNotExist(message.ifProject,"Trace",message.ifProject,message.ifUser,message.ifLicenseKey);
                if(isProjectCreated){
                    LOG.info("Project '%s' created successfully for user '%s'".formatted(message.ifProject,message.ifUser));
                    GrpcServer.projectLocalCache.putIfAbsent(message.ifProject,true);
                }else {
                    LOG.info("Fail to create the project '%s' for user '%s'".formatted(message.ifProject,message.ifUser));
                    continue;
                }
            }

            // Get Trace data from Jaeger
            var rawJaegerData = Jaeger.getTraceData(message.traceId);

            // Map Spans
            var traceDataBody = new TraceDataBody();

            var rawData = rawJaegerData.getJSONArray("data");
            var rawTrace = rawData.getJSONObject(0);
            var rawSpans = rawTrace.getJSONArray("spans");

            for(int i = 0; i< rawSpans.size(); i++){
                var curSpan = rawSpans.getJSONObject(i);

                // Set some properties based on first and last span
                if(i == 0){
                    traceDataBody.startTime = curSpan.getLong("startTime");
                }

                if(i == rawSpans.size() -1){
                    traceDataBody.endTime = curSpan.getLong("startTime") + curSpan.getLong("duration");
                    traceDataBody.duration = traceDataBody.endTime - traceDataBody.startTime;
                }

                var spanBody = new SpanDataBody();

                spanBody.spanID = curSpan.getString("spanID");
                spanBody.traceID = curSpan.getString("traceID");
                spanBody.operationName = curSpan.getString("operationName");
                spanBody.startTime = curSpan.getLong("startTime");
                spanBody.duration = curSpan.getLong("duration");
                spanBody.childSpans = new HashMap<>();
                spanBody.attributes = ParseUtil.getAttrMapFromJsonArray(curSpan.getJSONArray("tags"));

                // Save the span to correct parent span in the Trace.
                var references = curSpan.getJSONArray("references");
                if(!references.isEmpty()){
                    var reference = references.getJSONObject(0);
                    if(reference.getString("refType").equals("CHILD_OF")){
                        spanBody.parentSpanId = reference.getString("spanID");
                    } else{
                        spanBody.parentSpanId = "";
                    }
                }
                traceDataBody.addSpan(spanBody);
            }

            traceDataBody.processes = rawData.getJSONObject(0).getJSONObject("processes");
            traceDataBody.serviceName = traceDataBody.processes.getJSONObject("p1").getString("serviceName");
            traceDataBody.traceID = message.traceId;

            InsightFinder.sendData(traceDataBody, message.ifUser,message.ifLicenseKey,message.ifProject,traceDataBody.serviceName, traceDataBody.startTime, traceDataBody.serviceName );


        }


    }

}
