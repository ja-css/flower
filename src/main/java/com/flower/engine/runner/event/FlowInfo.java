package com.flower.engine.runner.event;

import com.flower.conf.FlowId;
import com.flower.conf.FlowInfoPrm;

import java.util.Objects;

public class FlowInfo implements FlowInfoPrm {
  private final FlowId flowId;
  private final String flowName;
  private final Class<?> flowType;

  public FlowInfo(FlowId flowId, String flowName, Class<?> flowType) {
    this.flowId = flowId;
    this.flowName = flowName;
    this.flowType = flowType;
  }

  @Override
  public FlowId flowId() {
    return flowId;
  }

  @Override
  public String flowName() {
    return flowName;
  }

  @Override
  public Class<?> flowType() {
    return flowType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FlowInfo)) return false;
    FlowInfo flowInfo = (FlowInfo) o;
    return Objects.equals(flowId, flowInfo.flowId)
        && Objects.equals(flowName, flowInfo.flowName)
        && Objects.equals(flowType, flowInfo.flowType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(flowId, flowName, flowType);
  }

  @Override
  public String toString() {
    return "FlowInfo{"
        + "flowId="
        + flowId
        + ", flowName='"
        + flowName
        + '\''
        + ", flowType="
        + flowType
        + '}';
  }
}
