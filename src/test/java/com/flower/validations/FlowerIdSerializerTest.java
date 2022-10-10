package com.flower.validations;

import static org.junit.Assert.assertEquals;

import com.flower.engine.FlowerId;
import com.flower.engine.FlowerIdSerializer;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class FlowerIdSerializerTest {
  @Test
  void test() {
    FlowerId root = new FlowerId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    FlowerId id2 = new FlowerId(UUID.fromString("00000000-0000-0000-0000-000000000002"), root);
    FlowerId id3 = new FlowerId(UUID.fromString("00000000-0000-0000-0000-000000000003"), id2);
    FlowerId id4 = new FlowerId(UUID.fromString("00000000-0000-0000-0000-000000000004"), id3);
    FlowerId id5 = new FlowerId(UUID.fromString("00000000-0000-0000-0000-000000000005"), id4);

    String token = FlowerIdSerializer.serialize(id5);
    FlowerId flowerId = FlowerIdSerializer.deserialize(token);

    assertEquals(id5, flowerId);
  }
}
