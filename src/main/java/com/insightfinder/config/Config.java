package com.insightfinder.config;

import com.insightfinder.config.model.AppConfig;
import com.insightfinder.config.model.AppConfig.TLS;
import com.insightfinder.config.model.ConfigModel;
import com.insightfinder.config.model.GrpcConfig;
import com.insightfinder.config.model.InsightFinderConfig;
import com.insightfinder.config.model.JaegerConfig;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class Config {

  private static final String CONFIG_FILE_PATH = "application.yml";
  private static Config instance;
  private ConfigModel configModel;

  private Config() {
    try {
      loadConfigs();
    } catch (IOException e) {
      log.error("Error loading config file: {}", e.getMessage());
      System.exit(1);
    }
  }

  public static Config getInstance() {
    if (instance == null) {
      instance = new Config();
    }
    return instance;
  }

  private void loadConfigs() throws IOException {
    try (InputStream input = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH)) {
      configModel = new Yaml().loadAs(input, ConfigModel.class);
    }
  }

  private AppConfig getAppConfig() {
    return configModel.getApp();
  }

  private GrpcConfig getGrpcConfig() {
    return configModel.getGrpc();
  }

  private InsightFinderConfig getIFConfig() {
    return configModel.getInsightFinder();
  }

  private JaegerConfig getJaegerConfig() {
    return configModel.getJaeger();
  }

  public int getAppTraceWorkerNum() {
    return getAppConfig().getTraceWorkerNum();
  }

  public long getDelay() {
    return getAppConfig().getDelay();
  }

  public int getGrpcPort() {
    return getGrpcConfig().getPort();
  }

  public int getGrpcMaxInboundMessageSizeInKB() {
    return getGrpcConfig().getMaxInboundMessageSizeInKB();
  }

  public String getIFUsername() {
    return getIFConfig().getUsername();
  }

  public String getIFServerUrl() {
    return getIFConfig().getServerUrl();
  }

  public String getIFServerUri() {
    return getIFConfig().getServerUri();
  }

  public String getIFCheckAndCreateUri() {
    return getIFConfig().getCheckAndCreateUri();
  }

  public String getIFLicenseKey() {
    return getIFConfig().getLicenseKey();
  }

  public String getJaegerServerName() {
    return getJaegerConfig().getServerName();
  }

  public int getJaegerGrpcPort() {
    return getJaegerConfig().getGrpcPort();
  }

  public int getJaegerUiPort() {
    return getJaegerConfig().getUiPort();
  }

  public boolean isJaegerTlsEnabled() {
    return getJaegerConfig().isTlsEnabled();
  }

  private TLS getTls() {
    return getAppConfig().getTls();
  }

  public boolean isAppTlsEnabled() {
    return getTls().isEnabled();
  }

  public String getAppPrivateKeyFile() {
    return getTls().getPrivateKeyFile();
  }

  public String getAppCertificateFile() {
    return getTls().getCertificateFile();
  }
}
