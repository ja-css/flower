package com.flower.engine;

import com.flower.conf.Flow;
import com.flower.conf.FlowId;
import com.flower.conf.FlowInfoPrm;
import com.flower.conf.StepInfoPrm;
import com.flower.engine.runner.event.EventContext;
import com.flower.engine.runner.event.EventParametersProvider;
import com.flower.engine.runner.event.FlowInfo;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

public class FlowImpl<T> implements Flow<T>, EventContext, EventParametersProvider {
  final FlowId id;

  final FlowInfo flowInfo;
  final T state;
  final AtomicReference<Optional<String>> status;
  final AtomicReference<String> currentStep;

  final Map<Class<?>, Object> eventProfileStates;

  @Nullable
  StepInfoPrm stepInfo = null;

  public FlowImpl(
      FlowId id,
      String flowName,
      Class<?> flowType,
      Optional<String> status,
      String currentStep,
      T state,
      Map<Class<?>, Object> eventProfileStates) {
    this.id = id;
    this.flowInfo = new FlowInfo(id, flowName, flowType);
    this.status = new AtomicReference<>(status);
    this.currentStep = new AtomicReference<>(currentStep);
    this.state = state;
    this.eventProfileStates = eventProfileStates;
  }

  @Override
  public FlowId getId() {
    return id;
  }

  @Override
  public T getState() {
    return state;
  }

  @Override
  public Optional<String> getStatus() {
    return Preconditions.checkNotNull(status.get());
  }

  @Override
  public String getCurrentStep() {
    return Preconditions.checkNotNull(currentStep.get());
  }

  public void setStatus(Optional<String> status) {
    this.status.set(status);
  }

  public void setCurrentStep(String currentStep) {
    this.currentStep.set(currentStep);
  }

  @Override
  public Object getFlowState() {
    return state;
  }

  @Override
  public Map<Class<?>, Object> getEventProfileStates() {
    return eventProfileStates;
  }

  @Override
  public FlowInfoPrm getFlowInfo() {
    return flowInfo;
  }

  @Override
  public StepInfoPrm getStepInfo() {
    return Preconditions.checkNotNull(stepInfo);
  }

  @Override
  public void setStepInfo(@Nullable StepInfoPrm stepInfo) {
    this.stepInfo = stepInfo;
  }
}
