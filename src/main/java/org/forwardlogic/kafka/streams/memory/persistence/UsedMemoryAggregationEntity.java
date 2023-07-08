package org.forwardlogic.kafka.streams.memory.persistence;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="used_memory_aggr_entity")
@IdClass(UsedMemoryAggregationKey.class)
public class UsedMemoryAggregationEntity {

    private Long id;

    @Id
    @Column(name = "address")
    private String hostAddress;

    @Column(name = "used_memory")
    private float usedMemoryInKB;

    @Id
    @Column(name = "collection_timestamp")
    private long timestamp;

    public UsedMemoryAggregationEntity(String hostAddress, float usedMemoryInKB, long timestamp) {
        this.hostAddress = hostAddress;
        this.usedMemoryInKB = usedMemoryInKB;
        this.timestamp = timestamp;
    }

    public UsedMemoryAggregationEntity() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public float getUsedMemoryInKB() {
        return usedMemoryInKB;
    }

    public void setUsedMemoryInKB(float usedMemoryInKB) {
        this.usedMemoryInKB = usedMemoryInKB;
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
        if (!(o instanceof UsedMemoryAggregationEntity entity)) return false;
        return timestamp == entity.timestamp && Objects.equals(hostAddress, entity.hostAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostAddress, timestamp);
    }

    @Override
    public String toString() {
        return "UsedMemoryEntity{" +
                "id=" + id +
                ", hostAddress='" + hostAddress + '\'' +
                ", usedMemoryInKB=" + usedMemoryInKB +
                ", timestamp=" + timestamp +
                '}';
    }
}
