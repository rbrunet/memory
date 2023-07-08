package org.forwardlogic.kafka.streams.memory.persistence;

import java.io.Serializable;
import java.util.Objects;

public class UsedMemoryAggregationKey implements Serializable {

    private String hostAddress;
    private long timestamp;

    public UsedMemoryAggregationKey() {
    }

    public UsedMemoryAggregationKey(String hostAddress, long timestamp) {
        this.hostAddress = hostAddress;
        this.timestamp = timestamp;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsedMemoryAggregationKey that)) return false;
        return timestamp == that.timestamp && Objects.equals(hostAddress, that.hostAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostAddress, timestamp);
    }
}
