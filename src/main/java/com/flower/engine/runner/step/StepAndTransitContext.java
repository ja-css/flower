package com.flower.engine.runner.step;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.flower.anno.event.EventType;
import com.flower.conf.FlowId;
import com.flower.conf.StepInfoPrm;
import com.flower.engine.runner.callfunc.FunctionCallContext;
import com.flower.engine.runner.callfunc.FunctionCallUtil;
import com.flower.engine.runner.callfunc.StepAndTransitFunctionCallState;
import com.flower.engine.runner.event.EventContext;
import com.flower.engine.runner.event.EventParametersProvider;
import com.flower.engine.runner.event.EventRunner;
import com.flower.engine.runner.event.StepInfo;
import com.flower.engine.runner.state.ObjectStateAccess;
import com.flower.engine.runner.state.StateAccessConfig;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class StepAndTransitContext implements StepCallContext {
  final FunctionCallContext stepAndTransitFunctionCall;

  final StateAccessConfig flowStateAccessConfig;

  final StepInfoPrm stepInfo;

  final List<InternalTransition> transitions;
  final List<Pair<String, String>> flowFactories;
  final List<Pair<String, String>> flowRepos;
  final StepParameterInitProfile stepParameterInitProfile;

  public StepAndTransitContext(
      FunctionCallContext stepAndTransitFunctionCall,
      StateAccessConfig flowStateAccessConfig,
      String stepName,
      List<InternalTransition> transitions,
      boolean isFirstStep,
      List<Pair<String, String>> flowFactories,
      List<Pair<String, String>> flowRepos) {
    this.stepAndTransitFunctionCall = stepAndTransitFunctionCall;
    this.flowStateAccessConfig = flowStateAccessConfig;
    // exec is the same as transit
    this.stepInfo = new StepInfo(stepName, stepName, isFirstStep, true);
    this.transitions = transitions;
    this.flowFactories = flowFactories;
    this.flowRepos = flowRepos;

    this.stepParameterInitProfile = new StepParameterInitProfile(stepAndTransitFunctionCall);
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

  public ListenableFuture<InternalTransition> call(
      FlowId flowId,
      Object flowState,
      EventRunner eventRunner,
      EventContext eventContext,
      EventParametersProvider eventParametersProvider) {
    ObjectStateAccess stateAccess = new ObjectStateAccess(flowState, flowStateAccessConfig);

    StepAndTransitFunctionCallState callState = new StepAndTransitFunctionCallState(stateAccess);

    return FluentFuture.from(
            eventRunner.runEvents(
                EventType.BEFORE_EXEC, eventContext, eventParametersProvider, null, null))
        .transformAsync(
            void_ ->
                FunctionCallUtil.invokeStepAndTransitFunction(
                    flowId, callState, stepAndTransitFunctionCall),
            directExecutor())
        .transformAsync(
            ret ->
                Futures.transform(
                    eventRunner.runEvents(
                        EventType.AFTER_EXEC, eventContext, eventParametersProvider, null, null),
                    void_ -> ret,
                    directExecutor()),
            directExecutor())
        .transformAsync(
            ret ->
                Futures.transform(
                    eventRunner.runEvents(
                        EventType.BEFORE_TRANSIT,
                        eventContext,
                        eventParametersProvider,
                        null,
                        null),
                    void_ -> ret,
                    directExecutor()),
            directExecutor())
        .transformAsync(
            ret ->
                Futures.transform(
                    eventRunner.runEvents(
                        EventType.AFTER_TRANSIT, eventContext, eventParametersProvider, ret, null),
                    void_ -> ret,
                    directExecutor()),
            directExecutor());
  }

  @Override
  public StepParameterInitProfile stepParameterInitProfile() {
    return stepParameterInitProfile;
  }
}
