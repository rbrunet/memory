package org.forwardlogic.kafka.streams.memory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record UsedMemory(String hostAddress, long usedMemoryInKB) {

    @JsonIgnore
    public String getKey() {
        return hostAddress() + ".used-memory";
    }
}
