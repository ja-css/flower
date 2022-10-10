package com.flower.engine.runner.callfunc;

import com.flower.engine.runner.state.StateAccess;

public class EventFunctionCallState {
  public final StateAccess flowStateAccess;
  public final StateAccess eventProfileStateAccess;

  public EventFunctionCallState(StateAccess flowStateAccess, StateAccess eventProfileStateAccess) {
    this.flowStateAccess = flowStateAccess;
    this.eventProfileStateAccess = eventProfileStateAccess;
  }
}
