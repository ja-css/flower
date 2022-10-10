package com.flower.engine.runner.callfunc;

import com.flower.engine.runner.state.StateAccess;

public class TransitFunctionExceptionCallState {
  public final StateAccess stateAccess;
  public final Throwable stepFunctionException;

  public TransitFunctionExceptionCallState(
      StateAccess stateAccess, Throwable stepFunctionException) {
    this.stateAccess = stateAccess;
    this.stepFunctionException = stepFunctionException;
  }
}
