package com.flower.engine.runner;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.flower.anno.event.EventType;
import com.flower.conf.FlowId;
import com.flower.conf.StepInfoPrm;
import com.flower.conf.Transition;
import com.flower.engine.FlowImpl;
import com.flower.engine.runner.event.EventContext;
import com.flower.engine.runner.event.EventFunctionContext;
import com.flower.engine.runner.event.EventFunctions;
import com.flower.engine.runner.event.EventParametersProvider;
import com.flower.engine.runner.event.EventProfileForFlowTypeCallContext;
import com.flower.engine.runner.event.EventRunUtil;
import com.flower.engine.runner.event.EventRunner;
import com.flower.engine.runner.step.InternalTransition;
import com.flower.engine.runner.step.StepCallContext;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class FlowCallContext implements EventRunner {
  final Map<String, StepCallContext> stepCalls;
  final Map<String, EventProfileForFlowTypeCallContext> eventContexts;
  // final
  final String firstStepName;

  final String flowName;
  final Class<?> flowType;

  public FlowCallContext(
      String flowName,
      Class<?> flowType,
      Map<String, StepCallContext> stepCalls,
      String firstStepName,
      Map<String, EventProfileForFlowTypeCallContext> eventContexts) {
    this.flowName = flowName;
    this.flowType = flowType;
    this.stepCalls = stepCalls;
    this.firstStepName = firstStepName;
    this.eventContexts = eventContexts;
    if (!stepCalls.containsKey(firstStepName))
      throw new IllegalStateException(
          String.format(
              "Fatal: firstStepName is not contained in steps: flow[%s] step [%s]",
              flowName, firstStepName));
  }

  <T> FlowImpl<T> createFlow(FlowId id, T flowState)
      throws IllegalAccessException, InstantiationException, InvocationTargetException {
    Map<String, Object> eventProfileStates = new HashMap<>();
    for (EventProfileForFlowTypeCallContext eventContext : eventContexts.values()) {
      String eventProfileName = eventContext.getEventProfileName();
      Class<?> eventProfileContainerType = eventContext.getEventProfileContainerType();

      try {
        Constructor<?> constructor = eventProfileContainerType.getConstructor();
        constructor.setAccessible(true);
        eventProfileStates.put(eventProfileName, constructor.newInstance());
      } catch (NoSuchMethodException nsme) {
        eventProfileStates.put(eventProfileName, eventProfileContainerType.newInstance());
      }
    }

    return new FlowImpl<>(
        id, flowName, flowType, Optional.empty(), firstStepName, flowState, eventProfileStates);
  }

  @Override
  public ListenableFuture<Void> runEvents(
      EventType eventType,
      EventContext eventContext,
      EventParametersProvider eventParametersProvider,
      @Nullable Transition transition,
      @Nullable Throwable flowException) {
    List<EventFunctionContext> eventFunctionList = new ArrayList<>();

    for (Map.Entry<String, EventProfileForFlowTypeCallContext> entry : eventContexts.entrySet()) {
      String profileName = entry.getKey();
      EventProfileForFlowTypeCallContext callContext = entry.getValue();
      Object eventProfileState =
          Preconditions.checkNotNull(eventContext.getEventProfileStates().get(profileName));

      EventFunctions eventFunctions = callContext.getFunctionsForEventType(eventType);
      if (eventFunctions != null)
        eventFunctionList.addAll(
            eventFunctions.getFunctions(eventContext.getFlowState(), eventProfileState));
    }

    return EventRunUtil.runEvents(
        eventFunctionList, eventParametersProvider, eventType, transition, flowException);
  }

  <T> Pair<FlowImpl<T>, ListenableFuture<InternalTransition>> runFirstStep(FlowImpl<T> flow) {
    return Pair.of(
        flow,
        FluentFuture.from(runEvents(EventType.BEFORE_FLOW, flow, flow, null, null))
            .transformAsync(
                void_ -> {
                  // TODO: some duplication here?
                  flow.setCurrentStep(firstStepName);
                  StepInfoPrm stepInfo = getStep(firstStepName).getStepInfo();
                  flow.setStepInfo(stepInfo);

                  return runEvents(EventType.BEFORE_STEP, flow, flow, null, null);
                },
                directExecutor())
            .transformAsync(void_ -> runStep(flow, firstStepName), directExecutor()));
  }

  StepCallContext getStep(String stepName) {
    return Preconditions.checkNotNull(stepCalls.get(stepName));
  }

  ListenableFuture<InternalTransition> runStep(FlowImpl flow, String stepName) {
    try {
      StepCallContext stepCallContext = getStep(stepName);

      return FluentFuture.from(runEvents(EventType.BEFORE_STEP_ITERATION, flow, flow, null, null))
          .transformAsync(
              void_ -> stepCallContext.call(flow.getId(), flow.getState(), this, flow, flow),
              directExecutor())
          .transformAsync(
              t ->
                  Futures.transform(
                      runEvents(EventType.AFTER_STEP_ITERATION, flow, flow, null, null),
                      void_ -> t,
                      directExecutor()),
              directExecutor());
    } catch (Throwable t) {
      return Futures.immediateFailedFuture(t);
    }
  }
}
