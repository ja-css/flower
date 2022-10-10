package com.flower.engine;

import com.google.common.base.Preconditions;
import java.util.UUID;

public class FlowerIdSerializer {
  private static final String SEPARATOR = "->";

  public static String serialize(FlowerId flowerId) {
    if (flowerId.parentFlowerId() == null) {
      return flowerId.id();
    } else {
      return serialize(flowerId.parentFlowerId()) + SEPARATOR + flowerId.id();
    }
  }

  public static FlowerId deserialize(String flowIdToken) {
    String[] idList = flowIdToken.split("->");

    FlowerId flowerId = null;
    for (String id : idList) {
      if (flowerId == null) {
        flowerId = new FlowerId(UUID.fromString(id));
      } else {
        flowerId = new FlowerId(UUID.fromString(id), flowerId);
      }
    }

    return Preconditions.checkNotNull(flowerId);
  }
}
