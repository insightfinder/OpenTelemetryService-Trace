package com.insightfinder.model.message;

import com.google.common.primitives.Ints;
import com.insightfinder.config.Config;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class TraceInfo implements Delayed {

  private static final long delay = Config.getInstance().getDelay();
  private final long startTime;
  public String traceId;
  public String ifUser;
  public String ifProject;
  public String ifLicenseKey;


  public TraceInfo(String traceId, String ifUser, String ifProject, String ifLicenseKey) {
    this.traceId = traceId;
    this.ifUser = ifUser;
    this.ifProject = ifProject;
    this.ifLicenseKey = ifLicenseKey;

    // Each message will be delayed for 1min to get received by the consumers.
    this.startTime = System.currentTimeMillis() + delay;

  }

  @Override
  public long getDelay(@NotNull TimeUnit unit) {
    long diff = startTime - System.currentTimeMillis();
    return unit.convert(diff, TimeUnit.MILLISECONDS);
  }

  @Override
  public int compareTo(@NotNull Delayed msg) {
    return Ints.saturatedCast(
        this.startTime - ((TraceInfo) msg).startTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TraceInfo traceInfo = (TraceInfo) o;
    return Objects.equals(traceId, traceInfo.traceId) && Objects.equals(ifUser,
        traceInfo.ifUser) && Objects.equals(ifProject, traceInfo.ifProject) && Objects.equals(
        ifLicenseKey, traceInfo.ifLicenseKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(traceId, ifUser, ifProject, ifLicenseKey);
  }
}
