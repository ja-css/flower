package com.flower.engine.runner;

import com.flower.anno.event.EventType;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowExecCallback;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowId;
import com.flower.conf.InternalFlowExec;
import com.flower.conf.StateSerializer;
import com.flower.conf.StepInfoPrm;
import com.flower.engine.FlowImpl;
import com.flower.engine.FlowerId;
import com.flower.engine.runner.step.InternalTransition;
import com.flower.engine.runner.step.StepCallContext;
import com.flower.utilities.FlowerException;
import com.flower.utilities.FutureCombiner;
import com.flower.utilities.FuturesTool;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.SettableFuture;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class FlowExecImpl<T> implements InternalFlowExec<T> {
  static final String BEGIN = "BEGIN([BEGIN])";
  static final String END = "END([END])";
  static final String DIAGRAM_NAME = "flowchart";

  static final String LINK_BEGIN = "--";
  static final String LINK_END = "-->";
  static final String FLOW_FACTORY_LINK_BEGIN = "-.";
  static final String FLOW_FACTORY_LINK_END = ".->";
  static final String FLOW_REPO_LINK_BEGIN = "-.";
  static final String FLOW_REPO_LINK_END = ".->";

  final FlowRunner flowRunner;
  final Class<T> flowType;
  final FlowCallContext flowCallContext;
  final FlowExecCallback flowExecCallback;
  final ListeningScheduledExecutorService scheduler;
  @Nullable final StateSerializer<T> stateSerializer;

  public FlowExecImpl(
      FlowRunner flowRunner,
      Class<T> flowType,
      FlowCallContext flowCallContext,
      FlowExecCallback flowExecCallback,
      ListeningScheduledExecutorService scheduler,
      @Nullable StateSerializer<T> stateSerializer) {
    this.flowRunner = flowRunner;
    this.flowType = flowType;
    this.flowCallContext = flowCallContext;
    this.flowExecCallback = flowExecCallback;
    this.scheduler = scheduler;
    this.stateSerializer = stateSerializer;
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

      SettableFuture<T> settableFlowFuture = SettableFuture.create();
      //Recursive flow execution routine, executes the flow
      transitionToNextStep(
          flowInstance,
          firstStepCallFuture,
          flowCallContext.flowName,
          flowCallContext.firstStepName,
          settableFlowFuture);

      ListenableFuture<T> flowFuture =
          FluentFuture.from(settableFlowFuture)
              .transform(
                  t -> {
                    flow.setStepInfo(null);
                    flowCallContext.runEvents(EventType.AFTER_FLOW, flow, flow, null, null);
                    return t;
                  },
                  scheduler);

      checkReturnValue =
          FluentFuture.from(flowFuture)
              .transformAsync(f -> flowFuture, scheduler)
              .catchingAsync(
                  Throwable.class,
                  t -> {
                    flow.setStepInfo(null);
                    flowCallContext.runEvents(EventType.FLOW_EXCEPTION, flow, flow, null, t);
                    return flowFuture;
                  },
                  scheduler);

      flowExecCallback.flowStarted(flowId, checkReturnValue);

      Futures.whenAllComplete(checkReturnValue)
          .call(
              () -> {
                flowExecCallback.flowFinished(flowId);
                return null;
              },
              scheduler);

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
    graphBuilder.append("- ").append(flowType.getSimpleName()).append("\n");
    graphBuilder
        .append("```mermaid\n" + DIAGRAM_NAME + "\n")
        .append(BEGIN)
        .append('\n')
        .append(END)
        .append('\n');

    for (Map.Entry<String, StepCallContext> entry : flowCallContext.stepCalls.entrySet()) {
      StepCallContext step = entry.getValue();
      String stepName = step.getStepInfo().stepName();
      boolean isFirst = step.getStepInfo().isFirstStep();
      List<InternalTransition> transitions = step.getTransitions();

      graphBuilder.append(stepName+"[["+stepName+"]]").append('\n');
      if (isFirst) {
        String link = createLink(BEGIN, stepName, null);
        if (existingLinks.add(link)) { graphBuilder.append(link).append('\n'); }
      }

      for (InternalTransition transition : transitions) {
        if (transition.isTerminal()) {
          String link = createPreFormattedLink(stepName, END, transition.getNote());
          if (existingLinks.add(link)) { graphBuilder.append(link).append('\n'); }
        } else {
          String link = createLink(stepName, Preconditions.checkNotNull(transition.getStepName()), transition.getNote());
          if (existingLinks.add(link)) { graphBuilder.append(link).append('\n'); }
        }
      }

      for (Pair<String, String> flowFactory : step.getFlowFactories()) {
          String link = createFlowFactoryLink(stepName, "Factory:" + getSimpleClassName(flowFactory.getKey()), flowFactory.getValue());
          graphBuilder.append(link).append('\n');
      }
      for (Pair<String, String> flowRepo : step.getFlowRepos()) {
          String link = createFlowRepoLink(stepName, "Repo:" + getSimpleClassName(flowRepo.getKey()), flowRepo.getValue());
          graphBuilder.append(link).append('\n');
      }
    }

    graphBuilder.append("```\n");

    return graphBuilder.toString();
  }

  static String getSimpleClassName(String className) {
    if (StringUtils.isBlank(className)) {
      return "";
    }
    int lastDotIndex = className.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return className;
    }
    return className.substring(lastDotIndex + 1);
  }

  String createPreFormattedLink(String left, String right, @Nullable String note) {
    if (StringUtils.isBlank(note)) {
      return String.format("%s %s %s", left, LINK_END, right);
    }
    return String.format("%s %s%s%s %s", left, LINK_BEGIN, note, LINK_END, right);
  }

  String createLink(String left, String right, @Nullable String note) {
    if (StringUtils.isBlank(note)) {
      return String.format("%s %s %s[[%s]]", left, LINK_END, right, right);
    }
    return String.format("%s %s%s%s %s[[%s]]", left, LINK_BEGIN, note, LINK_END, right, right);
  }

  String createFlowFactoryLink(String left, String right, @Nullable String note) {
    if (StringUtils.isBlank(note)) {
      return String.format("%s %s %s>%s]", left, FLOW_FACTORY_LINK_END, right, right);
    }
    return String.format("%s %s%s%s %s>%s]", left, FLOW_FACTORY_LINK_BEGIN, note, FLOW_FACTORY_LINK_END, right, right);
  }

  String createFlowRepoLink(String left, String right, @Nullable String note) {
    if (StringUtils.isBlank(note)) {
      return String.format("%s %s %s{{%s}}", left, FLOW_REPO_LINK_END, right, right);
    }
    return String.format("%s %s%s%s %s{{%s}}", left, FLOW_REPO_LINK_BEGIN, note, FLOW_REPO_LINK_END, right, right);
  }

  ListenableFuture<T> runEventsForLastStep(FlowImpl<T> flowInstance) {
    return FluentFuture.from(
            flowCallContext.runEvents(EventType.AFTER_STEP, flowInstance, flowInstance, null, null))
        .transform(void_ -> flowInstance.getState(), scheduler);
  }

  void transitionToNextStep(
      FlowImpl<T> flowInstance,
      ListenableFuture<InternalTransition> stepCallFuture,
      String flowTypeName,
      String stepName,
      SettableFuture<T> settableFuture) {
    Futures.whenAllComplete(stepCallFuture)
        .run(
            () -> {
              try {
                InternalTransition transition = stepCallFuture.get();
                if (transition == null) {
                  throw new FlowerException(
                      String.format("Step [%s] of Flow type [%s] provided `null` Transition object.",
                              stepName, flowTypeName));
                }

                // 1. Check final state in the form of transition to Terminal step
                if (transition.isTerminal()) {
                  if (transition.getDelay() != null) {
                    Duration delay = transition.getDelay();

                    // Flow finished after a delay
                    FuturesTool.assignSettableFuture(
                        FutureCombiner.delayExecutionAsync(
                            scheduler,
                            delay.toNanos(),
                            TimeUnit.NANOSECONDS,
                            () -> runEventsForLastStep(flowInstance)),
                        settableFuture,
                        scheduler);
                  } else {
                    // Flow finished
                    FuturesTool.assignSettableFuture(
                        runEventsForLastStep(flowInstance), settableFuture, scheduler);
                  }
                } else {
                  transitionToNextStep0(flowInstance, transition, settableFuture);
                }
              } catch (ExecutionException e) {
                settableFuture.setException(e.getCause());
              } catch (Throwable t) {
                settableFuture.setException(t);
              }
            },
            scheduler);
  }

  void transitionToNextStep0(
      FlowImpl<T> flowInstance, InternalTransition transition, SettableFuture<T> settableFuture) {
    if (transition.getDelay() != null) {
      Duration delay = transition.getDelay();
      FutureCombiner.delayExecutionAsync(
          scheduler,
          delay.toNanos(),
          TimeUnit.NANOSECONDS,
          () -> {
            runNextStep(
                flowInstance, Preconditions.checkNotNull(transition.getStepName()), settableFuture);
            return null;
          });
    } else {
      runNextStep(
          flowInstance, Preconditions.checkNotNull(transition.getStepName()), settableFuture);
    }
  }

  void runNextStep(
      FlowImpl<T> flowInstance, String nextStepName, SettableFuture<T> settableFuture) {
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
                  scheduler)
              .transformAsync(
                  void_ -> flowCallContext.runStep(flowInstance, nextStepName), scheduler);
    } else {
      stepCallFuture = flowCallContext.runStep(flowInstance, nextStepName);
    }

    transitionToNextStep(
        flowInstance, stepCallFuture, flowCallContext.flowName, nextStepName, settableFuture);
  }

  public Class<T> getFlowType() {
    return flowType;
  }

  @Nullable
  @Override
  public StateSerializer<T> getStateSerializer() {
    return stateSerializer;
  }
}
