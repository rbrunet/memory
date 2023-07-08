package org.forwardlogic.kafka.streams.memory.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsedMemoryAggregationRepository extends CrudRepository<UsedMemoryAggregationEntity, UsedMemoryAggregationKey> {

    public List<UsedMemoryAggregationEntity> findUsedMemoryAggregationEntitiesByHostAddressAndTimestamp(String hostAddress, long timestamp);
}
