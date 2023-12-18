package com.flower.engine.runner;

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

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.apache.commons.lang3.tuple.Pair;

public class FlowCallContext implements EventRunner {
  final Map<String, StepCallContext> stepCalls;
  final Map<Class<?>, EventProfileForFlowTypeCallContext> eventContexts;
  // final
  final String firstStepName;

  final String flowName;
  final Class<?> flowType;
  final ListeningScheduledExecutorService scheduler;

  public FlowCallContext(
      String flowName,
      Class<?> flowType,
      Map<String, StepCallContext> stepCalls,
      String firstStepName,
      Map<Class<?>, EventProfileForFlowTypeCallContext> eventContexts,
      ListeningScheduledExecutorService scheduler) {
    this.flowName = flowName;
    this.flowType = flowType;
    this.stepCalls = stepCalls;
    this.firstStepName = firstStepName;
    this.eventContexts = eventContexts;
    this.scheduler = scheduler;
    if (!stepCalls.containsKey(firstStepName))
      throw new IllegalStateException(
          String.format(
              "Fatal: firstStepName is not contained in steps: flow[%s] step [%s]",
              flowName, firstStepName));
  }

  <T> FlowImpl<T> createFlow(FlowId id, T flowState)
      throws IllegalAccessException, InstantiationException, InvocationTargetException {
    Map<Class<?>, Object> eventProfileStates = new HashMap<>();
    for (EventProfileForFlowTypeCallContext eventContext : eventContexts.values()) {
      String eventProfileName = eventContext.getEventProfileName();
      Class<?> eventProfileContainerType = eventContext.getEventProfileContainerType();

      try {
        Constructor<?> constructor = eventProfileContainerType.getConstructor();
        constructor.setAccessible(true);
        eventProfileStates.put(eventProfileContainerType, constructor.newInstance());
      } catch (NoSuchMethodException nsme) {
        try {
          eventProfileStates.put(eventProfileContainerType, eventProfileContainerType.newInstance());
        } catch(IllegalAccessException iae) {
          //TODO: figure why this is happening and fix. Reproducible in StateUpdateOnErrorTest.partialStateUpdate5Test() / OnErrorEventProfile.
          throw new IllegalStateException("This error happens when EventProfile class doesn't have a default constructor, please add default constructor.", iae);
        }
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

    for (Map.Entry<Class<?>, EventProfileForFlowTypeCallContext> entry : eventContexts.entrySet()) {
      Class<?> profileNameType = entry.getKey();
      EventProfileForFlowTypeCallContext callContext = entry.getValue();
      Object eventProfileState =
          Preconditions.checkNotNull(eventContext.getEventProfileStates().get(profileNameType));

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
                //important to context switch here, to untie the calling thread, initiating the flow execution.
                scheduler)
            .transformAsync(void_ -> runStep(flow, firstStepName), scheduler));
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
              scheduler)
          .transformAsync(
              t ->
                  Futures.transform(
                      runEvents(EventType.AFTER_STEP_ITERATION, flow, flow, null, null),
                      void_ -> t,
                      scheduler),
              scheduler);
    } catch (Throwable t) {
      return Futures.immediateFailedFuture(t);
    }
  }
}
