package com.flower.engine.runner.step;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.flower.anno.event.EventType;
import com.flower.conf.FlowId;
import com.flower.conf.StepInfoPrm;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.callfunc.FunctionCallContext;
import com.flower.engine.runner.callfunc.FunctionCallUtil;
import com.flower.engine.runner.callfunc.StepFunctionCallState;
import com.flower.engine.runner.callfunc.TransitFunctionCallState;
import com.flower.engine.runner.callfunc.TransitFunctionExceptionCallState;
import com.flower.engine.runner.event.EventContext;
import com.flower.engine.runner.event.EventParametersProvider;
import com.flower.engine.runner.event.EventRunner;
import com.flower.engine.runner.event.StepInfo;
import com.flower.engine.runner.state.ObjectStateAccess;
import com.flower.engine.runner.state.StateAccess;
import com.flower.engine.runner.state.StateAccessConfig;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import javax.annotation.Nullable;

public class StepContext implements StepCallContext {
  final FunctionCallContext stepFunctionCall;
  final FunctionCallContext transitFunctionCall;
  final List<InternalTransition> transitions;
  final List<Pair<String, String>> flowFactories;
  final List<Pair<String, String>> flowRepos;

  @Nullable final String stepReturnValueToFlowParameterName;
  final StateAccessConfig flowStateAccessConfig;
  final boolean isTransitionerCatching;
  final StepInfoPrm stepInfo;
  final StepParameterInitProfile stepParameterInitProfile;

  boolean isTransitionerCatching(FunctionCallContext transitFunctionCall) {
    return transitFunctionCall.functionParameters.stream()
        .anyMatch(
            prm ->
                prm.getFunctionParameterType().equals(ParameterType.TRANSIT_IN_RET_OR_EXCEPTION));
  }

  public StepContext(
      final FunctionCallContext stepFunctionCall,
      final FunctionCallContext transitFunctionCall,
      @Nullable String stepReturnValueToFlowParameterName,
      StateAccessConfig flowStateAccessConfig,
      String stepName,
      String transitName,
      List<InternalTransition> transitions,
      boolean isFirstStep,
      List<Pair<String, String>> flowFactories,
      List<Pair<String, String>> flowRepos
  ) {
    this.stepFunctionCall = stepFunctionCall;
    this.transitFunctionCall = transitFunctionCall;
    this.stepReturnValueToFlowParameterName = stepReturnValueToFlowParameterName;
    this.flowStateAccessConfig = flowStateAccessConfig;
    this.isTransitionerCatching = isTransitionerCatching(transitFunctionCall);
    this.stepInfo = new StepInfo(stepName, transitName, isFirstStep, false);
    this.transitions = transitions;
    this.flowFactories = flowFactories;
    this.flowRepos = flowRepos;
    this.stepParameterInitProfile =
        new StepParameterInitProfile(
            stepFunctionCall, transitFunctionCall, stepReturnValueToFlowParameterName);
  }

  ListenableFuture<?> invokeStepFunction(
      FlowId flowId,
      StateAccess stateAccess,
      EventRunner eventRunner,
      EventContext eventContext,
      EventParametersProvider eventParametersProvider) {
    StepFunctionCallState stepCallState =
        new StepFunctionCallState(stateAccess, stepReturnValueToFlowParameterName);

    return FluentFuture.from(
            eventRunner.runEvents(
                EventType.BEFORE_EXEC, eventContext, eventParametersProvider, null, null))
        .transformAsync(
            void_ -> FunctionCallUtil.invokeStepFunction(flowId, stepCallState, stepFunctionCall),
            directExecutor())
        .transformAsync(
            ret ->
                Futures.transform(
                    eventRunner.runEvents(
                        EventType.AFTER_EXEC, eventContext, eventParametersProvider, null, null),
                    void_ -> ret,
                    directExecutor()),
            directExecutor());
  }

  ListenableFuture<InternalTransition> invokeTransitFunction(
      StateAccess stateAccess,
      Object stepFunctionReturnValue,
      EventRunner eventRunner,
      EventContext eventContext,
      EventParametersProvider eventParametersProvider) {
    TransitFunctionCallState callState =
        new TransitFunctionCallState(stateAccess, stepFunctionReturnValue);

    return FluentFuture.from(
            eventRunner.runEvents(
                EventType.BEFORE_TRANSIT, eventContext, eventParametersProvider, null, null))
        .transformAsync(
            void_ -> FunctionCallUtil.invokeTransitFunction(callState, transitFunctionCall),
            directExecutor())
        .transformAsync(
            transition ->
                Futures.transform(
                    eventRunner.runEvents(
                        EventType.AFTER_TRANSIT,
                        eventContext,
                        eventParametersProvider,
                        transition,
                        null),
                    void_ -> transition,
                    directExecutor()),
            directExecutor());
  }

  ListenableFuture<InternalTransition> invokeTransitFunctionOnException(
      StateAccess stateAccess,
      Throwable t,
      EventRunner eventRunner,
      EventContext eventContext,
      EventParametersProvider eventParametersProvider) {
    if (isTransitionerCatching) {
      TransitFunctionExceptionCallState callState =
          new TransitFunctionExceptionCallState(stateAccess, t);

      return FluentFuture.from(
              eventRunner.runEvents(
                  EventType.AFTER_EXEC, eventContext, eventParametersProvider, null, null))
          .transformAsync(
              void_ ->
                  eventRunner.runEvents(
                      EventType.BEFORE_TRANSIT, eventContext, eventParametersProvider, null, null),
              directExecutor())
          .transformAsync(
              void_ ->
                  FunctionCallUtil.invokeTransitFunctionOnException(callState, transitFunctionCall),
              directExecutor())
          .transformAsync(
              transition ->
                  Futures.transform(
                      eventRunner.runEvents(
                          EventType.AFTER_TRANSIT,
                          eventContext,
                          eventParametersProvider,
                          transition,
                          null),
                      void_ -> transition,
                      directExecutor()),
              directExecutor());
    } else return Futures.immediateFailedFuture(t);
  }

  @Override
  public StepInfoPrm getStepInfo() {
    return stepInfo;
  }

  @Override
  public List<InternalTransition> getTransitions() {
    return transitions;
  }

  @Override
  public List<Pair<String, String>> getFlowFactories() { return flowFactories; }

  @Override
  public List<Pair<String, String>> getFlowRepos() { return flowRepos; }

  static class RetValAndTransition {
    final Object value;
    final boolean useTransition;

    public RetValAndTransition(Object value, boolean useTransition) {
      this.value = value;
      this.useTransition = useTransition;
    }
  }

  public ListenableFuture<InternalTransition> call(
      FlowId flowId,
      Object flowState,
      EventRunner eventRunner,
      EventContext eventContext,
      EventParametersProvider eventParametersProvider) {
    final ObjectStateAccess stateAccess = new ObjectStateAccess(flowState, flowStateAccessConfig);

    // 1) Call executor - step function
    return Futures.transformAsync(
        Futures.catchingAsync(
            Futures.transform(
                invokeStepFunction(
                    flowId, stateAccess, eventRunner, eventContext, eventParametersProvider),
                stepFunctionReturnValue -> new RetValAndTransition(stepFunctionReturnValue, false),
                MoreExecutors.directExecutor()),
            Throwable.class,
            t ->
                Futures.transform(
                    invokeTransitFunctionOnException(
                        stateAccess, t, eventRunner, eventContext, eventParametersProvider),
                    transition -> new RetValAndTransition(transition, true),
                    MoreExecutors.directExecutor()),
            MoreExecutors.directExecutor()),
        retValAndTransition -> {
          if (retValAndTransition.useTransition) {
            return Futures.immediateFuture((InternalTransition) retValAndTransition.value);
          } else {
            return invokeTransitFunction(
                stateAccess,
                retValAndTransition.value,
                eventRunner,
                eventContext,
                eventParametersProvider);
          }
        },
        MoreExecutors.directExecutor());
  }

  @Override
  public StepParameterInitProfile stepParameterInitProfile() {
    return stepParameterInitProfile;
  }
}
