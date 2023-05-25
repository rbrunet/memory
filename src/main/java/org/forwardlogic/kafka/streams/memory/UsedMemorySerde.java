package org.forwardlogic.kafka.streams.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class UsedMemorySerde implements Serializer<UsedMemory>, Deserializer<UsedMemory>, Serde<UsedMemory> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    public byte[] serialize(String topic, UsedMemory data) {
        byte[] retVal = null;
        try {
            retVal = this.objectMapper.writeValueAsString(data).getBytes();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return retVal;
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

    @Override
    public Serializer<UsedMemory> serializer() {
        return this;
    }

    @Override
    public Deserializer<UsedMemory> deserializer() {
        return this;
    }

    @Override
    public UsedMemory deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, UsedMemory.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

