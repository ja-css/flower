package com.flower.engine.runner.state;

public class ObjectStateAccess implements StateAccess {
  private final Object flowState;
  private final StateAccessConfig flowStateAccess;

  public ObjectStateAccess(Object flowState, StateAccessConfig flowStateAccess) {
    this.flowState = flowState;
    this.flowStateAccess = flowStateAccess;
  }

  @Override
  public Object getField(String fieldName) {
    return flowStateAccess.getField(flowState, fieldName);
  }

  @Override
  public boolean hasField(String fieldName) {
    return flowStateAccess.hasField(fieldName);
  }

  @Override
  public void updateField(String fieldName, Object parameterValue) {
    flowStateAccess.updateField(flowState, fieldName, parameterValue);
  }
}
