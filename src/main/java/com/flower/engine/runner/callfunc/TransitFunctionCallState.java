package com.flower.engine.runner.callfunc;

import com.flower.engine.runner.state.StateAccess;

public class TransitFunctionCallState {
  public final StateAccess stateAccess;
  public final Object previousReturnValue;

  public TransitFunctionCallState(StateAccess stateAccess, Object previousReturnValue) {
    this.stateAccess = stateAccess;
    this.previousReturnValue = previousReturnValue;
  }
}
