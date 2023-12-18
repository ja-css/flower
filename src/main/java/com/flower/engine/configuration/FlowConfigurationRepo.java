package com.flower.engine.configuration;

import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class FlowConfigurationRepo {
  private final Map<Class<?>, FlowTypeRecord> flowTypeByClass;
  private final Map<String, FlowTypeRecord> flowTypeByName;

  private final Map<Class<?>, GlobalFunctionContainerRecord> globalFunctionContainerByClass;
  private final Map<String, GlobalFunctionContainerRecord> globalFunctionContainerByName;

  private final Map<Class<?>, EventProfileContainerRecord> eventProfileContainerByClass;
  private final Map<Class<?>, String> eventProfileContainerNameByClass;
  private final Map<String, EventProfileContainerRecord> eventProfileContainerByName;

  private final List<Class<?>> defaultEventProfiles;

  public FlowConfigurationRepo() {
    flowTypeByClass = new HashMap<>();
    flowTypeByName = new HashMap<>();
    globalFunctionContainerByClass = new HashMap<>();
    globalFunctionContainerByName = new HashMap<>();
    eventProfileContainerByClass = new HashMap<>();
    eventProfileContainerByName = new HashMap<>();
    eventProfileContainerNameByClass = new HashMap<>();
    defaultEventProfiles = new ArrayList<>();
  }

  /**
   * Register Global Function Container
   *
   * @param globalFunctionContainerType Global Function Container class
   */
  public void registerGlobalFunctionContainer(final Class<?> globalFunctionContainerType) {
    if (!globalFunctionContainerType.isAnnotationPresent(GlobalFunctionContainer.class))
      throw new AnnotationFormatError(
          "Global Function Container must be annotated with @GlobalFunctionContainer");

    GlobalFunctionContainer annotation =
        globalFunctionContainerType.getAnnotation(GlobalFunctionContainer.class);
    String globalFunctionContainerName = annotation.name();
    if (globalFunctionContainerName.trim().equals(""))
      globalFunctionContainerName = globalFunctionContainerType.getCanonicalName();

    if (globalFunctionContainerByClass.containsKey(globalFunctionContainerType))
      throw new IllegalStateException("Duplicate GlobalFunctionContainer type: [" + globalFunctionContainerType + "]");//???
    if (globalFunctionContainerByName.containsKey(globalFunctionContainerName))
      throw new IllegalStateException(
          "Duplicate Global Function Container name: ["
              + globalFunctionContainerName
              + "] type1 ["
              + globalFunctionContainerByName.get(globalFunctionContainerName)
                  .globalFunctionContainerType
              + "] type2 ["
              + globalFunctionContainerType
              + "]");

    GlobalFunctionContainerRecord globalFunctionContainerRecord =
        new GlobalFunctionContainerRecord(
            globalFunctionContainerType, annotation, globalFunctionContainerName);

    globalFunctionContainerByClass.put(globalFunctionContainerType, globalFunctionContainerRecord);
    globalFunctionContainerByName.put(globalFunctionContainerName, globalFunctionContainerRecord);
  }

  /**
   * Register Flow Type
   *
   * @param flowType Flow Type class
   */
  public void registerFlowType(final Class<?> flowType) {
    if (!flowType.isAnnotationPresent(FlowType.class))
      throw new AnnotationFormatError("Flow Type must be annotated with @FlowType");

    FlowType annotation = flowType.getAnnotation(FlowType.class);
    String flowTypeName = annotation.name();
    if (flowTypeName.trim().equals("")) flowTypeName = flowType.getCanonicalName();
    Class<?> parentFlowType = annotation.extendz() == void.class ? null : annotation.extendz();

    if (flowTypeByClass.containsKey(flowType))
      throw new IllegalStateException("Duplicate Flow type: [" + flowType + "]");
    if (flowTypeByName.containsKey(flowTypeName))
      throw new IllegalStateException(
          "Duplicate Flow name: ["
              + flowTypeName
              + "] type1 ["
              + flowTypeByName.get(flowTypeName).flowType
              + "] type2 ["
              + flowType
              + "]");

    FlowTypeRecord flowTypeRecord =
        new FlowTypeRecord(flowType, annotation, flowTypeName, parentFlowType, eventProfileContainerNameByClass);

    flowTypeByClass.put(flowType, flowTypeRecord);
    flowTypeByName.put(flowTypeName, flowTypeRecord);
  }

  /**
   * Register Event Profile Container
   *
   * @param eventProfileContainerType Event Profile Container class
   */
  public void registerEventProfileContainer(final Class<?> eventProfileContainerType) {
    registerEventProfileContainer(eventProfileContainerType, false);
  }

  /**
   * Register Event Profile Container
   *
   * @param eventProfileContainerType Event Profile Container class
   * @param isDefault true - use as default Event Profile for all Flows
   */
  public void registerEventProfileContainer(
      final Class<?> eventProfileContainerType, boolean isDefault) {
    if (!eventProfileContainerType.isAnnotationPresent(EventProfileContainer.class))
      throw new AnnotationFormatError(
          "Event Profile Container must be annotated with @EventProfileContainer");

    EventProfileContainer annotation =
        eventProfileContainerType.getAnnotation(EventProfileContainer.class);
    String eventProfileName = annotation.name();
    if (eventProfileName.trim().equals(""))
      eventProfileName = eventProfileContainerType.getCanonicalName();

    if (eventProfileContainerByClass.containsKey(eventProfileContainerType))
      throw new IllegalStateException(
          "Duplicate EventProfileContainer type: [" + eventProfileContainerType + "]");

    if (eventProfileContainerByName.containsKey(eventProfileName))
      throw new IllegalStateException(
          "Duplicate EventProfileContainer name: ["
              + eventProfileName
              + "] type1 ["
              + eventProfileContainerByName.get(eventProfileName).eventProfileContainerType
              + "] type2 ["
              + eventProfileContainerType
              + "]");

    EventProfileContainerRecord eventProfileContainerRecord =
        new EventProfileContainerRecord(eventProfileContainerType, annotation, eventProfileName);

    eventProfileContainerByClass.put(eventProfileContainerType, eventProfileContainerRecord);
    eventProfileContainerByName.put(eventProfileName, eventProfileContainerRecord);
    eventProfileContainerNameByClass.put(eventProfileContainerType, eventProfileName);

    if (isDefault) defaultEventProfiles.add(eventProfileContainerType);
  }

  public void initialize() {
    ImmutableList<FlowTypeRecord> flowTypes = ImmutableList.copyOf(flowTypeByName.values());

    // TODO: Topological sort implementation can be improved?
    int numberUninitialized;
    do {
      numberUninitialized = 0;

      for (FlowTypeRecord flowTypeRecord : flowTypes) {
        if (!flowTypeRecord.isInitialized()) {
          Optional<Class<?>> parentFlowTypeOpt = flowTypeRecord.parentFlowType;

          if (!parentFlowTypeOpt.isPresent()) {
            flowTypeRecord.initialize(null);
          } else {
            Class<?> parentFlowType = parentFlowTypeOpt.get();
            FlowTypeRecord parentFlow = flowTypeByClass.get(parentFlowType);
            if (parentFlow == null) {
              throw new IllegalStateException(
                  String.format(
                      "Parent flow not found. Flow: %s; ParentFlow: %s.",
                      flowTypeRecord.flowTypeName, parentFlowType));
            } else {
              if (parentFlow.isInitialized()) {
                flowTypeRecord.initialize(parentFlow);
              } else {
                numberUninitialized += 1;
              }
            }
          }
        }
      }
    } while (numberUninitialized != 0);

    for (GlobalFunctionContainerRecord globalFunctionContainerRecord :
        globalFunctionContainerByClass.values()) {
      globalFunctionContainerRecord.initialize();
    }

    for (EventProfileContainerRecord eventProfileContainerRecord :
        eventProfileContainerByName.values()) {
      eventProfileContainerRecord.initialize();
    }
  }

  @Nullable
  public FlowTypeRecord getFlow(Class<?> flowClass) {
    return flowTypeByClass.get(flowClass);
  }

  public Collection<FlowTypeRecord> getFlowTypes() {
    return flowTypeByName.values();
  }

  public Collection<GlobalFunctionContainerRecord> getGlobalFunctionContainers() {
    return globalFunctionContainerByClass.values();
  }

  public Collection<EventProfileContainerRecord> getEventProfileContainers() {
    return eventProfileContainerByName.values();
  }

  public List<Class<?>> getDefaultEventProfiles() {
    return defaultEventProfiles;
  }
}
