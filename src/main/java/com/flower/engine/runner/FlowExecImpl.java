package com.flower.engine.runner;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.flower.anno.event.EventType;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowExecCallback;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowId;
import com.flower.conf.InternalFlowExec;
import com.flower.conf.StepInfoPrm;
import com.flower.engine.FlowImpl;
import com.flower.engine.FlowerId;
import com.flower.engine.runner.step.InternalTransition;
import com.flower.engine.runner.step.StepCallContext;
import com.flower.utilities.FlowerException;
import com.flower.utilities.FutureCombiner;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;

public class FlowExecImpl<T> implements InternalFlowExec<T> {
  static final String BEGIN = "BEGIN";
  static final String END = "END";
  static final String LINK = "-->";

  final FlowRunner flowRunner;
  final Class<T> flowType;
  final FlowCallContext flowCallContext;
  final FlowExecCallback flowExecCallback;
  final ListeningScheduledExecutorService scheduler;

  public FlowExecImpl(
      FlowRunner flowRunner,
      Class<T> flowType,
      FlowCallContext flowCallContext,
      FlowExecCallback flowExecCallback,
      ListeningScheduledExecutorService scheduler) {
    this.flowRunner = flowRunner;
    this.flowType = flowType;
    this.flowCallContext = flowCallContext;
    this.flowExecCallback = flowExecCallback;
    this.scheduler = scheduler;
  }

  @Override
  public FlowFuture<T> runChildFlow(FlowId parentFlowId, T flow, Duration startupDelay) {
    // TODO: decouple FlowerId - FlowId factory?
    final FlowerId flowId = new FlowerId((FlowerId) parentFlowId);
    return runFlow(flowId, flow, startupDelay);
  }

  @Override
  public FlowFuture<T> runFlow(T flow, Duration startupDelay) {
    final FlowerId flowId = new FlowerId();
    return runFlow(flowId, flow, startupDelay);
  }

  public FlowFuture<T> runFlow(FlowId flowId, T flow, Duration startupDelay) {
    // TODO: implement
    throw new UnsupportedOperationException("Running flow with startup delay is not implemented yet");
  }

  @Override
  public FlowFuture<T> runChildFlow(FlowId parentFlowId, T flow) {
    // TODO: decouple FlowerId - FlowId factory?
    final FlowerId flowId = new FlowerId((FlowerId) parentFlowId);
    return runFlow(flowId, flow);
  }

  @Override
  public FlowFuture<T> runFlow(T flowState) {
    final FlowerId flowId = new FlowerId();
    return runFlow(flowId, flowState);
  }

  public FlowFuture<T> runFlow(FlowId flowId, T flowState) {
    if (!flowState.getClass().equals(flowType)) {
      //Inherited flows will have a different executor.
      FlowExec childExec = flowRunner.getFlowExec(flowState.getClass());
      return childExec.runFlow(flowState);
    }

    FlowImpl<T> flow_ = null;
    ListenableFuture<T> checkReturnValue;
    try {
      final FlowImpl<T> flow = flow_ = flowCallContext.createFlow(flowId, flowState);

      final Pair<FlowImpl<T>, ListenableFuture<InternalTransition>> flowContextPair =
          flowCallContext.runFirstStep(flow);

      final FlowImpl<T> flowInstance = flowContextPair.getLeft();
      final ListenableFuture<InternalTransition> firstStepCallFuture = flowContextPair.getRight();

      ListenableFuture<T> flowFuture =
          FluentFuture.from(transitionToNextStep(flowInstance, firstStepCallFuture, flowCallContext.flowName, flowCallContext.firstStepName))
              .transform(
                  t -> {
                    flow.setStepInfo(null);
                    flowCallContext.runEvents(EventType.AFTER_FLOW, flow, flow, null, null);
                    return t;
                  },
                  directExecutor());

      checkReturnValue =
          FluentFuture.from(flowFuture)
              .transformAsync(f -> flowFuture, directExecutor())
              .catchingAsync(
                  Throwable.class,
                  t -> {
                    flow.setStepInfo(null);
                    flowCallContext.runEvents(EventType.FLOW_EXCEPTION, flow, flow, null, t);
                    return flowFuture;
                  },
                  directExecutor());

      flowExecCallback.flowStarted(flowId, checkReturnValue);

      Futures.whenAllComplete(checkReturnValue)
          .call(
              () -> {
                flowExecCallback.flowFinished(flowId);
                return null;
              },
              MoreExecutors.directExecutor());

      return new SimpleFlowFuture<>(flowId, checkReturnValue);
    } catch (Throwable t) {
      if (flow_ != null) {
        flowCallContext.runEvents(EventType.FLOW_EXCEPTION, flow_, flow_, null, t);
      }
      return new SimpleFlowFuture<>(flowId, Futures.immediateFailedFuture(t));
    }
  }

  @Override
  public String buildMermaidGraph() {
    Set<String> existingLinks = new HashSet<>();

    StringBuilder graphBuilder = new StringBuilder();
    graphBuilder
        .append("```mermaid\ngraph LR\n")
        .append(BEGIN)
        .append('\n')
        .append(END)
        .append('\n');

    for (Map.Entry<String, StepCallContext> entry : flowCallContext.stepCalls.entrySet()) {
      StepCallContext step = entry.getValue();
      String stepName = step.getStepInfo().stepName();
      boolean isFirst = step.getStepInfo().isFirstStep();
      List<InternalTransition> transitions = step.getTransitions();

      graphBuilder.append(stepName).append('\n');
      if (isFirst) {
        String link = createLink(BEGIN, stepName);
        if (existingLinks.add(link)) graphBuilder.append(link).append('\n');
      }

      for (InternalTransition transition : transitions) {
        if (transition.isTerminal()) {
          String link = createLink(stepName, END);
          if (existingLinks.add(link)) graphBuilder.append(link).append('\n');
        } else {
          String link = createLink(stepName, Preconditions.checkNotNull(transition.getStepName()));
          if (existingLinks.add(link)) graphBuilder.append(link).append('\n');
        }
      }
    }

    graphBuilder.append("```");

    return graphBuilder.toString();
  }

  String createLink(String left, String right) {
    return String.format("%s %s %s", left, LINK, right);
  }

  ListenableFuture<T> runEventsForLastStep(FlowImpl<T> flowInstance) {
    return FluentFuture.from(
            flowCallContext.runEvents(EventType.AFTER_STEP, flowInstance, flowInstance, null, null))
        .transform(void_ -> flowInstance.getState(), directExecutor());
  }

  ListenableFuture<T> transitionToNextStep(
      FlowImpl<T> flowInstance, ListenableFuture<InternalTransition> stepCallFuture, String flowTypeName, String stepName) {
    return FluentFuture.from(stepCallFuture)
        .transformAsync(
            transition -> {
              if (transition == null) {
                throw new FlowerException("Step [" + stepName + "] of Flow type [" + flowTypeName + "] provided `null` Transition object.");
              }

              // 1. Check final state in the form of transition to Terminal step
              if (transition.isTerminal()) {
                if (transition.getDelay() != null) {
                  Duration delay = transition.getDelay();

                  // Flow finished after a delay
                  return FutureCombiner.delayExecutionAsync(
                      scheduler,
                      delay.toNanos(),
                      TimeUnit.NANOSECONDS,
                      () -> runEventsForLastStep(flowInstance));
                } else {
                  // Flow finished
                  return runEventsForLastStep(flowInstance);
                }
              } else

              // 2. Delay
              if (transition.getDelay() != null) {
                Duration delay = transition.getDelay();
                return FutureCombiner.delayExecutionAsync(
                    scheduler,
                    delay.toNanos(),
                    TimeUnit.NANOSECONDS,
                    () ->
                        runNextStep(
                            flowInstance, Preconditions.checkNotNull(transition.getStepName())));
              } else
                return runNextStep(
                    flowInstance, Preconditions.checkNotNull(transition.getStepName()));
            },
            // We need this context switch between steps to allow stack trace to reset.
            // If directExecutor will be used here stack overflow is possible for daemon flows or
            // flows with large number of step iterations.
            scheduler);
  }

  ListenableFuture<T> runNextStep(FlowImpl<T> flowInstance, String nextStepName) {
    String currentStepName = flowInstance.getCurrentStep();

    ListenableFuture<InternalTransition> stepCallFuture;
    // Step changed
    if (!currentStepName.equals(nextStepName)) {
      stepCallFuture =
          FluentFuture.from(
                  flowCallContext.runEvents(
                      EventType.AFTER_STEP, flowInstance, flowInstance, null, null))
              .transformAsync(
                  void_ -> {
                    // TODO: some duplication here?
                    flowInstance.setCurrentStep(nextStepName);
                    StepInfoPrm stepInfo = flowCallContext.getStep(nextStepName).getStepInfo();
                    flowInstance.setStepInfo(stepInfo);
                    return flowCallContext.runEvents(
                        EventType.BEFORE_STEP, flowInstance, flowInstance, null, null);
                  },
                  directExecutor())
              .transformAsync(
                  void_ -> flowCallContext.runStep(flowInstance, nextStepName), directExecutor());
    } else {
      stepCallFuture = flowCallContext.runStep(flowInstance, nextStepName);
    }

    return transitionToNextStep(flowInstance, stepCallFuture, flowCallContext.flowName, nextStepName);
  }

  public Class<T> getFlowType() {
    return flowType;
  }
}
