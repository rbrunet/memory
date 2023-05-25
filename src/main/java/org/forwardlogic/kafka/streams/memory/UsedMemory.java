package org.forwardlogic.kafka.streams.memory;

import java.util.List;
import java.util.Objects;

public record UsedMemory(String hostAddress, long usedMemoryInKB) {
}
