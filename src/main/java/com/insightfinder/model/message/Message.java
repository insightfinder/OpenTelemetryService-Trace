package com.insightfinder.model.message;

import com.google.common.primitives.Ints;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Message implements Delayed {
    public String traceId;
    public String ifUser;
    public String ifProject;
    public String ifLicenseKey;

    private final long startTime;


    public Message(String traceId, String ifUser, String ifProject, String ifLicenseKey) {
        this.traceId = traceId;
        this.ifUser = ifUser;
        this.ifProject = ifProject;
        this.ifLicenseKey = ifLicenseKey;

        // Each message will be delayed for 1min to get received by the consumers.
        this.startTime = System.currentTimeMillis() + 60000;

    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed msg) {
        return Ints.saturatedCast(
                this.startTime - ((Message) msg).startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(traceId, message.traceId) && Objects.equals(ifUser, message.ifUser) && Objects.equals(ifProject, message.ifProject) && Objects.equals(ifLicenseKey, message.ifLicenseKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traceId, ifUser, ifProject, ifLicenseKey);
    }
}
