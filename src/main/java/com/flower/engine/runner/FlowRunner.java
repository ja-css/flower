package com.flower.engine.runner;

import com.flower.conf.FactoryOfFlowTypeFactories;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowExecCallback;
import com.flower.conf.FlowId;
import com.flower.conf.FlowRepoPrm;
import com.flower.conf.InternalFlowExec;
import com.flower.conf.StateSerializer;
import com.flower.engine.configuration.EventProfileContainerRecord;
import com.flower.engine.configuration.FlowConfigurationRepo;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.GlobalFunctionContainerRecord;
import com.flower.engine.configuration.GlobalFunctionRecord;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class FlowRunner implements FlowExecCallback {
  private static final int TIME_TO_LIVE_IN_CACHE_MILLIS = 30 * 60 * 1000;
  private static final int MAXIMUM_CACHE_SIZE = 1000;

  private final ListeningScheduledExecutorService scheduler;
  private final FlowConfigurationRepo flowConfigurationRepo;
  private final FlowCallContextCreator flowCallContextCreator;
  private final Map<FlowId, ListenableFuture<Object>> activeFlows;
  private final Cache<FlowId, ListenableFuture<Object>> flowsCache;

  private final DynamicFlowExec dynamicFlowExec;

  @Nullable private Map<Class<?>, InternalFlowExec> flowExecByClass;
  @Nullable private Map<String, InternalFlowExec> flowExecByName;

  @Nullable private List<FactoryOfFlowTypeFactories> flowTypeFactoriesList;

  public FlowRunner(
      FlowConfigurationRepo flowConfigurationRepo, ListeningScheduledExecutorService scheduler) {
    this(flowConfigurationRepo, scheduler, TIME_TO_LIVE_IN_CACHE_MILLIS, MAXIMUM_CACHE_SIZE);
  }

  public FlowRunner(
      FlowConfigurationRepo flowConfigurationRepo,
      ListeningScheduledExecutorService scheduler,
      int ttlInCacheMillis,
      int maxCacheSize) {
    this.flowConfigurationRepo = flowConfigurationRepo;
    this.scheduler = scheduler;
    this.flowCallContextCreator = new FlowCallContextCreator(this);
    this.dynamicFlowExec = new DynamicFlowExec(this);
    activeFlows = new ConcurrentHashMap<>();
    flowsCache =
        CacheBuilder.newBuilder()
            .expireAfterWrite(ttlInCacheMillis, TimeUnit.MILLISECONDS)
            .maximumSize(maxCacheSize)
            .build();
  }

  public void initialize() {
    // 1. Map global functions
    final Map<Class<?>, Map<String, GlobalFunctionRecord>> globalFunctionsByContainerAndName = new HashMap<>();
    for (GlobalFunctionContainerRecord container :
        flowConfigurationRepo.getGlobalFunctionContainers()) {
      for (GlobalFunctionRecord globalFunction : container.getGlobalFunctions()) {
        Map<String, GlobalFunctionRecord> globalFunctionsByName =
            globalFunctionsByContainerAndName.computeIfAbsent(container.getGlobalFunctionContainerType(), t -> new HashMap<>());

        String globalFunctionName = globalFunction.functionName;
        if (globalFunctionsByName.containsKey(globalFunctionName))
          throw new IllegalStateException(
              "Duplicate GlobalFunction name. GlobalFunctionName: ["
                  + globalFunctionName
                  + "] Container class 1: ["
                  + globalFunctionsByName.get(globalFunctionName).globalFunctionContainerType
                  + "] Container class 2: ["
                  + globalFunction.globalFunctionContainerType
                  + "]");
        globalFunctionsByName.put(globalFunctionName, globalFunction);
      }
    }

    // 2. Map event profiles
    final Map<Class<?>, EventProfileContainerRecord> eventProfilesByType = new HashMap<>();
    for (EventProfileContainerRecord eventProfile :
        flowConfigurationRepo.getEventProfileContainers()) {
      Class<?> eventProfileType = eventProfile.eventProfileContainerType;
      if (eventProfilesByType.containsKey(eventProfileType))
        throw new IllegalStateException(
            "Duplicate EventProfile name. EventProfileName: ["
                + eventProfileType
                + "] EventProfileContainer class 1: ["
                + eventProfilesByType.get(eventProfileType).eventProfileContainerType
                + "] EventProfileContainer class 2: ["
                + eventProfile.eventProfileContainerType
                + "]");
      eventProfilesByType.put(eventProfileType, eventProfile);
    }

    // 3. Get  default event profile names
    final List<Class<?>> defaultEventProfiles = flowConfigurationRepo.getDefaultEventProfiles();

    // 4. Create FlowCallContext for flow types
    flowTypeFactoriesList = new ArrayList<>();
    flowExecByClass = new HashMap<>();
    flowExecByName = new HashMap<>();
    for (FlowTypeRecord flowTypeRecord : flowConfigurationRepo.getFlowTypes()) {
      FlowCallContext flowCallContext =
          flowCallContextCreator.createFlowCallContext(
              scheduler, flowTypeRecord, globalFunctionsByContainerAndName, eventProfilesByType, defaultEventProfiles);
      Class<?> flowType = flowTypeRecord.flowType;
      //TODO: implement StateSerializer
      InternalFlowExec<?> flowExec = createExec(flowType, flowCallContext, null);
      flowExecByClass.put(flowTypeRecord.flowType, flowExec);
      flowExecByName.put(flowTypeRecord.flowTypeName, flowExec);
    }

    for (FactoryOfFlowTypeFactories flowTypeFactory : flowTypeFactoriesList) {
      flowTypeFactory.initFlowExec();
    }

    flowTypeFactoriesList.clear();
  }

  private <T> InternalFlowExec<T> createExec(Class<T> flowType, FlowCallContext flowCallContext, @Nullable StateSerializer<T> stateSerializer) {
    return new FlowExecImpl<>(this, flowType, flowCallContext, this, scheduler, stateSerializer);
  }

  // ------------------------------------------------------------------

  public <T> FlowExec<T> getFlowExec(Class<T> flowStateClass) {
    return getInternalFlowExec(flowStateClass);
  }

  public <T> InternalFlowExec<T> getInternalFlowExec(Class<T> flowStateClass) {
    if (flowExecByClass == null)
      throw new IllegalStateException(
          "FlowExecByClass is null. Please initialize() FlowConfigurationRepo.");

    InternalFlowExec<T> flowExec = flowExecByClass.get(flowStateClass);
    if (flowExec == null)
      throw new IllegalStateException(
          String.format("Registered FlowExec not found for Flow class [%s].", flowStateClass));

    return flowExec;
  }

  public <T> FlowExec<T> getFlowExec(String flowStateName) {
    return getInternalFlowExec(flowStateName);
  }

  public <T> InternalFlowExec<T> getInternalFlowExec(String flowStateName) {
    if (flowExecByName == null)
      throw new IllegalStateException(
          "FlowExecByClass is null. Please initialize() FlowConfigurationRepo.");

    InternalFlowExec<T> flowExec = flowExecByName.get(flowStateName);
    if (flowExec == null)
      throw new IllegalStateException(
          String.format("Registered FlowExec not found for Flow name [%s].", flowStateName));

    return flowExec;
  }

  @Nullable
  public ListenableFuture<Object> getActiveFlow(FlowId flowId) {
    return activeFlows.get(flowId);
  }

  @Nullable
  public ListenableFuture<Object> getFlowFuture(FlowId flowId) {
    ListenableFuture<Object> flowFuture = getActiveFlow(flowId);
    return flowFuture != null ? flowFuture : flowsCache.getIfPresent(flowId);
  }

  @Override
  public void flowStarted(FlowId flowId, ListenableFuture flow) {
    activeFlows.put(flowId, flow);
  }

  @Override
  public void flowFinished(FlowId flowId) {
    // remove from active flows and put to flow LRU cache
    ListenableFuture<Object> flow = Preconditions.checkNotNull(activeFlows.get(flowId));
    flowsCache.put(flowId, flow);
    activeFlows.remove(flowId);
  }

  public void registerFactoryOfFlowTypeFactories(
      FactoryOfFlowTypeFactories factoryOfFlowTypeFactories) {
    Preconditions.checkNotNull(flowTypeFactoriesList).add(factoryOfFlowTypeFactories);
  }

  public <T> InternalFlowExec<T> getDynamicFlowExec() {
    return dynamicFlowExec;
  }

  public ListeningScheduledExecutorService getScheduler() {
    return scheduler;
  }
}
