package com.flower.engine.runner.event;

import java.util.Map;

public interface EventContext {
  Object getFlowState();

  Map<Class<?>, Object> getEventProfileStates();

  // Implement those for event parameter injection
  /*setFlowInfo
  setStepInfo
  setTransitionInfo*/
}
