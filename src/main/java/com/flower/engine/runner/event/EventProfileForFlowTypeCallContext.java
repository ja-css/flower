package com.flower.engine.runner.event;

import com.flower.anno.event.EventType;
import com.flower.engine.runner.state.StateAccessConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class EventProfileForFlowTypeCallContext {
  final Class<?> eventProfileContainerType;
  final String eventProfileName;
  // TODO: add concurrency info
  final Map<EventType, EventFunctions> eventFunctionsByType;

  public EventProfileForFlowTypeCallContext(
      String eventProfileName,
      Class<?> eventProfileContainerType,
      Map<EventType, List<EventFunction>> eventFunctionsByType,
      StateAccessConfig eventProfileStateAccessConfig,
      StateAccessConfig flowStateAccessConfig) {
    this.eventProfileName = eventProfileName;
    this.eventProfileContainerType = eventProfileContainerType;

    this.eventFunctionsByType = new HashMap<>();
    for (Map.Entry<EventType, List<EventFunction>> entry : eventFunctionsByType.entrySet()) {
      EventType entryType = entry.getKey();
      List<EventFunction> functions = entry.getValue();

      this.eventFunctionsByType.put(
          entryType,
          new EventFunctions(eventProfileStateAccessConfig, flowStateAccessConfig, functions));
    }
  }

  public @Nullable EventFunctions getFunctionsForEventType(EventType eventType) {
    return eventFunctionsByType.get(eventType);
  }

  /*public ListenableFuture<Void> runEvents(
      Object eventProfileState,
      Object flowState,
      EventParametersProvider eventParametersProvider,
      EventType eventType,
      @Nullable Transition transition) {
    EventFunctions functionsForEventType = getFunctionsForEventType(eventType);

    ObjectStateAccess eventProfileStateAccess =
        new ObjectStateAccess(eventProfileState, functionsForEventType.eventProfileStateAccessConfig);
    ObjectStateAccess flowStateAccess = new ObjectStateAccess(flowState, functionsForEventType.flowStateAccessConfig);

    //List<EventFunction> eventFunctionList = eventFunctionsByType.get(eventType);
    List<EventFunction> eventFunctionList = functionsForEventType.functions;

    List<ListenableFuture<Void>> futures = new ArrayList<>();
    if (eventFunctionList != null)
      for (EventFunction eventFunction : eventFunctionList) {
        futures.add(
            invokeEventFunction(
                eventProfileStateAccess,
                flowStateAccess,
                eventFunction.functionCallContext,
                eventParametersProvider, eventType, transition));
      }

    // TODO: implement proper order of calling in EventCallContextForFlowType
    // !!! not here, in EventCallContextForFlowType
    return Futures.whenAllComplete(futures)
        .callAsync(Futures::immediateVoidFuture, MoreExecutors.directExecutor());
  }*/

  public String getEventProfileName() {
    return eventProfileName;
  }

  public Class<?> getEventProfileContainerType() {
    return eventProfileContainerType;
  }

  public Map<EventType, EventFunctions> getEventFunctionsByType() {
    return eventFunctionsByType;
  }
}
