package com.insightfinder.service;

import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_AGENT_TYPE;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_DATA_TYPE;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_INSTANCE_TYPE;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_LICENSE_KEY;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_OPERATION;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_PROJECT_CLOUD_TYPE;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_PROJECT_NAME;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_SYSTEM_NAME;
import static com.insightfinder.config.WebClientConstant.HTTP_REQUEST_PARAM_USERNAME;

import com.alibaba.fastjson2.JSON;
import com.insightfinder.config.Config;
import com.insightfinder.model.message.TraceInfo;
import com.insightfinder.model.request.IFLogTraceDataPayload;
import com.insightfinder.model.request.IFLogTraceDataReceivePayload;
import com.insightfinder.model.request.TraceDataBody;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
public class InsightFinderService {

  private static final ConcurrentHashMap<String, Boolean> projectCache = new ConcurrentHashMap<>();
  private static final Config config = Config.getInstance();
  private static final String IF_AGENT_TYPE_CUSTOM = "Custom";
  private static final String IF_AGENT_TYPE_TRACE = "Trace";
  private static final String OPERATION_CHECK = "check";
  private static final String OPERATION_CREATE = "create";
  private static final String PRIVATE_CLOUD = "PrivateCloud";
  private static final String RESPONSE_IS_PROJECT_EXIST = "isProjectExist";
  private static final String RESPONSE_SUCCESS = "success";
  private static final String DATA_TYPE_LOG = "Log";
  private static InsightFinderService instance;
  private final OkHttpClient httpClient = new OkHttpClient();

  private InsightFinderService() {
  }

  public static InsightFinderService getInstance() {
    if (instance == null) {
      instance = new InsightFinderService();
    }
    return instance;
  }

  public boolean isProjectCreated(String projectName, String systemName, String user,
      String licenseKey) {
    if (isProjectExisted(projectName)) {
      return true;
    }
    boolean hasProject = createProjectIfNotExist(projectName, systemName, user, licenseKey);
    if (hasProject) {
      projectCache.put(projectName, true);
    }
    return projectCache.containsKey(projectName);
  }

  private boolean isProjectExisted(String projectName) {
    return projectCache.containsKey(projectName);
  }

  private boolean createProjectIfNotExist(String projectName, String systemName,
      String user, String licenseKey) {
    String checkAndCreateURL = config.getIFServerUrl() + config.getIFCheckAndCreateUri();
    boolean projectExist;
    RequestBody emptyFormBody = new FormBody.Builder().build();

    // Check Project
    var checkProjectUrl = Objects.requireNonNull(HttpUrl.parse(checkAndCreateURL))
        .newBuilder()
        .addQueryParameter(HTTP_REQUEST_PARAM_USERNAME, user)
        .addQueryParameter(HTTP_REQUEST_PARAM_LICENSE_KEY, licenseKey)
        .addQueryParameter(HTTP_REQUEST_PARAM_PROJECT_NAME, projectName)
        .addQueryParameter(HTTP_REQUEST_PARAM_SYSTEM_NAME, systemName)
        .addQueryParameter(HTTP_REQUEST_PARAM_OPERATION, OPERATION_CHECK).build();
    var checkProjectRequest = new Request.Builder().url(checkProjectUrl)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .post(emptyFormBody).build();
    try (Response response = httpClient.newCall(checkProjectRequest).execute()) {
      if (response.isSuccessful()) {
        if (response.body() != null) {
          var responseBodyJson = JSON.parseObject(response.body().string());
          projectExist = responseBodyJson.getBoolean(RESPONSE_IS_PROJECT_EXIST);
        } else {
          projectExist = false;
        }
      } else {
        log.error("Request failed with code: {}", response.code());
        return false;
      }
    } catch (IOException e) {
      log.error(e.getMessage());
      return false;
    }

    // Create Project
    if (!projectExist) {
      var createProjectUrl = Objects.requireNonNull(HttpUrl.parse(checkAndCreateURL))
          .newBuilder()
          .addQueryParameter(HTTP_REQUEST_PARAM_USERNAME, user)
          .addQueryParameter(HTTP_REQUEST_PARAM_LICENSE_KEY, licenseKey)
          .addQueryParameter(HTTP_REQUEST_PARAM_PROJECT_NAME, projectName)
          .addQueryParameter(HTTP_REQUEST_PARAM_SYSTEM_NAME, systemName)
          .addQueryParameter(HTTP_REQUEST_PARAM_INSTANCE_TYPE, PRIVATE_CLOUD)
          .addQueryParameter(HTTP_REQUEST_PARAM_PROJECT_CLOUD_TYPE, IF_AGENT_TYPE_TRACE)
          .addQueryParameter(HTTP_REQUEST_PARAM_AGENT_TYPE, IF_AGENT_TYPE_CUSTOM)
          .addQueryParameter(HTTP_REQUEST_PARAM_DATA_TYPE, DATA_TYPE_LOG)
          .addQueryParameter(HTTP_REQUEST_PARAM_OPERATION, OPERATION_CREATE).build();
      var createProjectRequest = new Request.Builder().url(createProjectUrl)
          .addHeader("Content-Type", "application/x-www-form-urlencoded")
          .post(emptyFormBody).build();

      try (Response response = httpClient.newCall(createProjectRequest).execute()) {
        var responseBodyJson = JSON.parseObject(response.body().string());
        if (response.isSuccessful()) {
          var isProjectCreated = responseBodyJson.getBoolean(RESPONSE_SUCCESS);
          if (isProjectCreated) {
            log.info("Project '{}' created for user '{}'", projectName, user);
            return true;
          } else {
            log.error("Failed to create project '{}' for user '{}': {}", projectExist, user,
                responseBodyJson.getString("message"));
            return false;
          }
        } else {
          log.error("Failed to create project '{}' for user '{}': {}", projectExist, user,
              response.code());
          return false;
        }
      } catch (IOException e) {
        log.error(e.getMessage());
        return false;
      }
    } else {
      return true;
    }
  }

  public void sendTraceData(TraceDataBody traceDataBody, TraceInfo traceInfo) {
    var iFLogTraceData = new IFLogTraceDataPayload();
    var iFPayload = new IFLogTraceDataReceivePayload();

    iFLogTraceData.setData(traceDataBody);
    iFLogTraceData.setTimeStamp(String.valueOf(traceDataBody.getStartTime()));
    var instanceName = traceDataBody.getInstanceName();
    iFLogTraceData.setInstanceName(instanceName);
    if (instanceName != null && !instanceName.isEmpty()) {
      iFLogTraceData.setComponentName(instanceName);
    }
    iFPayload.setLogTraceDataList(JSON.toJSONString(List.of(iFLogTraceData)));
    iFPayload.setUserName(traceInfo.getIfUser());
    iFPayload.setLicenseKey(traceInfo.getIfLicenseKey());
    iFPayload.setProjectName(traceInfo.getIfProject());
    iFPayload.setInsightAgentType("LogTrace");

//    log.info(JSON.toJSONString(iFPayload));
    RequestBody body = RequestBody.create(JSON.toJSONBytes(iFPayload),
        MediaType.get("application/json"));
    Request request = new Request.Builder()
        .url(config.getIFServerUrl() + config.getIFServerUri())
        .addHeader("agent-type", "Stream")
        .addHeader("Content-Type", "application/json")
        .post(body)
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("Error sending log data with response: {}", response.message());
      } else {
        log.info("Sent trace '{}' to project '{}' for user '{}'.",
            traceInfo.getTraceId(), traceInfo.getIfProject(), traceInfo.getIfUser());
      }
    } catch (IOException e) {
      log.error("Error sending log data with exception: {}", e.getMessage());
    }
  }
}
