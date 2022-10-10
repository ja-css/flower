package com.flower.engine.runner.state;

public interface StateAccess {
  Object getField(String fieldName);

  boolean hasField(String fieldName);

  void updateField(String fieldName, Object parameterValue);
}
