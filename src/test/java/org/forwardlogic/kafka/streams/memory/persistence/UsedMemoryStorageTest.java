package org.forwardlogic.kafka.streams.memory.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UsedMemorySpringPersistenceTestConfig.class})
public class UsedMemoryStorageTest {

    @Autowired
    private UsedMemoryAggregationRepository usedMemoryAggregationRepository;

    @Test
    void test_storing_one_usedMemory() {
        UsedMemoryAggregationEntity entity = new UsedMemoryAggregationEntity();
        float usedMemoryInKB = 10.0f;
        String hostAddress = "localhost";
        entity.setUsedMemoryInKB(usedMemoryInKB);
        entity.setHostAddress(hostAddress);
        long timestamp = 1234l;
        entity.setTimestamp(timestamp);

        this.usedMemoryAggregationRepository.save(entity);
        Iterable<UsedMemoryAggregationEntity> usedMemoryEntities = this.usedMemoryAggregationRepository.findAll();

        UsedMemoryAggregationEntity usedMemoryAggregationEntity = usedMemoryEntities.iterator().next();

        assertAll("Grouped assertions on UserMemoryAggregationEntity",
                () -> assertThat(usedMemoryAggregationEntity, notNullValue()),
                () -> assertThat(usedMemoryAggregationEntity.getUsedMemoryInKB(), is(usedMemoryInKB)),
                () -> assertThat(usedMemoryAggregationEntity.getHostAddress(), is(hostAddress)),
                () -> assertThat(usedMemoryAggregationEntity.getTimestamp(), is(timestamp)));
    }
}
