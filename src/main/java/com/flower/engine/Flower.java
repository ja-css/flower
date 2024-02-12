package com.flower.engine;

import com.flower.conf.FlowExec;
import com.flower.conf.StateSerializer;
import com.flower.conf.FlowerEngine;
import com.flower.engine.configuration.FlowConfigurationRepo;
import com.flower.engine.runner.FlowRunner;
import com.flower.executor.TaskCheckingExecutor;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class Flower implements FlowerEngine {
  // TODO: does this belong to FlowConfigurationRepo
  public void registerTransitFunctions(Class<?> transitFunctionType) {
    throw new UnsupportedOperationException();
  }

  // TODO: implement?
  // TODO: What's that about?
  private boolean implicitStateAllowed = false;

  // -------------------------------------------

  private final ListeningScheduledExecutorService scheduler;
  private final FlowConfigurationRepo flowConfigurationRepo;
  private final FlowRunner flowRunner;

  public Flower(ListeningScheduledExecutorService scheduler) {
    this.scheduler = scheduler;
    flowConfigurationRepo = new FlowConfigurationRepo();
    flowRunner = new FlowRunner(flowConfigurationRepo, scheduler);
  }

  public Flower(int threadCount) {
    this(MoreExecutors.listeningDecorator(TaskCheckingExecutor.builder().corePoolSize(threadCount).build()));
  }

  public Flower() {
    this(1);
  }

  public void registerFlow(final Class<?>... flowTypes) {
    for (Class<?> flowType : flowTypes) {
      flowConfigurationRepo.registerFlowType(flowType);
    }
  }

  public <T> void overrideFlowStateSerializer(final Class<T> flowType, StateSerializer<T> stateSerializer) {
    //TODO: implement
  }

  public <T> void overrideEventProfileStateSerializer(final Class<T> eventProfileType, StateSerializer<T> stateSerializer) {
    //TODO: implement
  }

  // TODO: Global function containers can't be generic, but global functions CAN be generic
  public void registerGlobalFunctions(final Class<?> globalFunctionContainer) {
    if (globalFunctionContainer.getTypeParameters().length > 0)
      throw new IllegalStateException(
          String.format(
              "Global function container class can't be generic, even though Global Functions can: type [%s]",
              globalFunctionContainer));
    flowConfigurationRepo.registerGlobalFunctionContainer(globalFunctionContainer);
  }

  // TODO: Event Profile CAN'T be generic, because it's flow-agnostic and can't have different
  //  generics for different flows to support @InFromFlow, @OutFromFlow, @InOutFromFlow
  //  Therefore, Event functions also CAN'T be generic
  public void registerEventProfile(final Class<?> eventProfileContainer) {
    if (eventProfileContainer.getTypeParameters().length > 0)
      throw new IllegalStateException(
          String.format(
              "Event Profile container can't be generic: type [%s]", eventProfileContainer));
    flowConfigurationRepo.registerEventProfileContainer(eventProfileContainer);
  }

  // TODO: Event Profile CAN'T be generic, because it's flow-agnostic and can't have different
  //  generics for different flows to support @InFromFlow, @OutFromFlow, @InOutFromFlow
  //  Therefore, Event functions also CAN'T be generic
  public void registerEventProfile(final Class<?> eventProfileContainer, boolean isDefault) {
    if (eventProfileContainer.getTypeParameters().length > 0)
      throw new IllegalStateException(
          String.format(
              "Event Profile container can't be generic: type [%s]", eventProfileContainer));
    flowConfigurationRepo.registerEventProfileContainer(eventProfileContainer, isDefault);
  }

  public void initialize() {
    flowConfigurationRepo.initialize();
    flowRunner.initialize();
  }

  @Override
  public <T> FlowExec<T> getFlowExec(Class<T> flowType) {
    return flowRunner.getFlowExec(flowType);
  }

  public void shutdownScheduler() {
    scheduler.shutdownNow();
  }
}
