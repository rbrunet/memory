package org.forwardlogic.kafka.streams.memory;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.kstream.Windowed;
import org.forwardlogic.kafka.streams.memory.persistence.UsedMemoryAggregationEntity;
import org.forwardlogic.kafka.streams.memory.persistence.UsedMemoryAggregationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class UsedMemoryCollector {

    public static final String AGGREGATED_USED_MEMORY_TOPIC = "aggregated-used-memory";

    private UsedMemoryAggregationRepository usedMemoryAggregationRepository;

    @Autowired
    public UsedMemoryCollector(UsedMemoryAggregationRepository repository) {
        this.usedMemoryAggregationRepository = repository;
    }

    @KafkaListener(id = "collector", topics = AGGREGATED_USED_MEMORY_TOPIC)
    @Transactional
    public UsedMemoryAggregationEntity collect(ConsumerRecord<Windowed<String>, UsedMemoryCountAndSum> record) {
        List<UsedMemoryAggregationEntity> previousEntities = this.usedMemoryAggregationRepository.findUsedMemoryAggregationEntitiesByHostAddressAndTimestamp(record.value().getHostAddress(),
                record.value().getTimestamp());
        UsedMemoryAggregationEntity entity = null;
        if (previousEntities.isEmpty()) {
            entity = new UsedMemoryAggregationEntity(record.value().getHostAddress(), record.value().getAverage(), record.key().window().start());
        } else {
            entity = previousEntities.get(0);
            entity.setUsedMemoryInKB(record.value().getAverage());
        }

        this.usedMemoryAggregationRepository.save(entity);

        return entity;
    }
}
