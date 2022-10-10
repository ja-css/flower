package com.flower.engine.runner.event;

import com.flower.engine.runner.state.ObjectStateAccess;
import com.flower.engine.runner.state.StateAccessConfig;
import java.util.List;
import java.util.stream.Collectors;

public class EventFunctions {
  public final StateAccessConfig eventProfileStateAccessConfig;
  public final StateAccessConfig flowStateAccessConfig;
  public final List<EventFunction> functions;

  public EventFunctions(
      StateAccessConfig eventProfileStateAccessConfig,
      StateAccessConfig flowStateAccessConfig,
      List<EventFunction> functions) {
    this.eventProfileStateAccessConfig = eventProfileStateAccessConfig;
    this.flowStateAccessConfig = flowStateAccessConfig;
    this.functions = functions;
  }

  public List<EventFunctionContext> getFunctions(Object flowState, Object eventProfileState) {
    ObjectStateAccess eventProfileStateAccess =
        new ObjectStateAccess(eventProfileState, eventProfileStateAccessConfig);
    ObjectStateAccess flowStateAccess = new ObjectStateAccess(flowState, flowStateAccessConfig);

    return functions.stream()
        .map(ef -> new EventFunctionContext(eventProfileStateAccess, flowStateAccess, ef))
        .collect(Collectors.toList());
  }
}
