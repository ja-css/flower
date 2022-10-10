package com.flower.engine.runner.callfunc;

import com.flower.engine.runner.state.StateAccess;

import javax.annotation.Nullable;

public class StepFunctionCallState {
  public final StateAccess stateAccess;
  public final @Nullable String returnValueToFlowParameterName;

  public StepFunctionCallState(
      StateAccess stateAccess, @Nullable String returnValueToFlowParameterName) {
    this.stateAccess = stateAccess;
    this.returnValueToFlowParameterName = returnValueToFlowParameterName;
  }
}
