package com.flower.engine.runner;

import com.flower.anno.event.EventType;
import com.flower.engine.configuration.EventCallRecord;
import com.flower.engine.configuration.EventProfileContainerRecord;
import com.flower.engine.configuration.EventRecord;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.GlobalFunctionRecord;
import com.flower.engine.configuration.StateFieldRecord;
import com.flower.engine.configuration.StepAndTransitCallRecord;
import com.flower.engine.configuration.StepAndTransitRecord;
import com.flower.engine.configuration.StepCallRecord;
import com.flower.engine.configuration.StepRecord;
import com.flower.engine.configuration.TransitionerCallRecord;
import com.flower.engine.configuration.TransitionerRecord;
import com.flower.engine.function.StateField;
import com.flower.engine.runner.event.EventFunction;
import com.flower.engine.runner.event.EventProfileForFlowTypeCallContext;
import com.flower.engine.runner.parameters.FunctionCallParameterCreator;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.ExpectedInitializedField;
import com.flower.engine.runner.step.InternalTransition;
import com.flower.engine.runner.step.StepCallContext;
import com.flower.engine.runner.step.StepCallContextCreator;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class FlowCallContextCreator {
  final FunctionCallParameterCreator functionCallParameterCreator;
  final StepCallContextCreator stepCallContextCreator;

  public FlowCallContextCreator(FlowRunner flowRunner) {
    this.functionCallParameterCreator = new FunctionCallParameterCreator(flowRunner);
    this.stepCallContextCreator = new StepCallContextCreator(flowRunner);
  }

  // ----------- Flow call context -----------

  public FlowCallContext createFlowCallContext(
      ListeningScheduledExecutorService scheduler,
      FlowTypeRecord flowTypeRecord,
      Map<Class<?>, Map<String, GlobalFunctionRecord>> globalFunctionsByContainerAndName,
      Map<Class<?>, EventProfileContainerRecord> eventProfilesByType,
      List<Class<?>> defaultEventProfiles) {
    Map<String, StepCallContext> stepCalls = new HashMap<>();
    StateAccessConfig flowStateAccessConfig = createStateAccess(flowTypeRecord.state);

    // Step
    for (StepRecord stepRecord : flowTypeRecord.steps.values()) {
      StepCallContext stepCallContext =
          createStepContext(
              flowTypeRecord, flowStateAccessConfig, stepRecord, globalFunctionsByContainerAndName);
      stepCalls.put(stepRecord.stepName, stepCallContext);
    }

    // StepCall
    for (StepCallRecord stepCallRecord : flowTypeRecord.stepCalls.values()) {
      StepCallContext stepCallContext =
          createStepCallContext(
              flowTypeRecord, flowStateAccessConfig, stepCallRecord, globalFunctionsByContainerAndName);
      stepCalls.put(stepCallRecord.stepName, stepCallContext);
    }

    // StepAndTransit
    for (StepAndTransitRecord stepAndTransitRecord : flowTypeRecord.stepAndTransits.values()) {
      StepCallContext stepCallContext =
          createStepAndTransitContext(flowTypeRecord, flowStateAccessConfig, stepAndTransitRecord);
      stepCalls.put(stepAndTransitRecord.stepName, stepCallContext);
    }

    // StepAndTransitCall
    for (StepAndTransitCallRecord stepAndTransitCallRecord :
        flowTypeRecord.stepAndTransitCalls.values()) {
      StepCallContext stepCallContext =
          createStepAndTransitCallContext(
              flowTypeRecord,
              flowStateAccessConfig,
              stepAndTransitCallRecord,
              globalFunctionsByContainerAndName);
      stepCalls.put(stepAndTransitCallRecord.stepName, stepCallContext);
    }

    List<StepCallContext> stepCallList = new ArrayList<>(stepCalls.values());
    for (StepCallContext stepCall : stepCallList) {
      for (InternalTransition transition : stepCall.getTransitions()) {
        String stepName = transition.getStepName();
        if (stepName != null) {
          if (!stepCalls.containsKey(stepName)) {
            throw new IllegalStateException(
                String.format(
                    "Flow contains transition to non-existent Step: Flow [%s] Step [%s] Transition Step [%s]",
                    flowTypeRecord.flowTypeName, stepCall.getStepInfo().stepName(), stepName));
          }
        }
      }
    }

    // TODO: Event profiles-related initialization also Default event profiles?
    // TODO: validate existence and types of flow parameters in EventFunctions
    // TODO: get EventProfiles for the FlowType

    Set<Class<?>> eventProfileTypes = new HashSet<>();
    boolean disableAllExternalProfiles =
        (flowTypeRecord.disableEventProfiles != null) && flowTypeRecord.disableAllExternal;
    if (!disableAllExternalProfiles) {
      eventProfileTypes.addAll(defaultEventProfiles);
    }
    if (flowTypeRecord.eventProfiles != null) {
      eventProfileTypes.addAll(flowTypeRecord.eventProfiles);
    }
    if (flowTypeRecord.disableEventProfiles != null) {
      for (Class<?> eventProfileToDisable : flowTypeRecord.disableEventProfiles) {
        eventProfileTypes.remove(eventProfileToDisable);
      }
    }

    Map<Class<?>, EventProfileForFlowTypeCallContext> eventContexts = new HashMap<>();
    for (Class<?> eventProfileType : eventProfileTypes) {
      EventProfileContainerRecord eventProfile = eventProfilesByType.get(eventProfileType);
      if (eventProfile == null)
        throw new IllegalStateException(
            String.format(
                "Flow refers to non-existent EventProfile: Flow [%s] EventProfile [%s]",
                flowTypeRecord.flowTypeName, eventProfileType));

      EventProfileForFlowTypeCallContext eventContext =
          createEventProfileContext(
              flowTypeRecord, eventProfile, flowStateAccessConfig, globalFunctionsByContainerAndName);
      eventContexts.put(eventProfileType, eventContext);
    }

    String firstStepName = getFirstStepName(flowTypeRecord);
    nullAwayCheck(flowTypeRecord, firstStepName, stepCalls, flowStateAccessConfig);

    return new FlowCallContext(
        flowTypeRecord.flowTypeName,
        flowTypeRecord.flowType,
        stepCalls,
        firstStepName,
        eventContexts,
        scheduler);
  }

  class StepInitializationEntry {
    Map<String, Set<String>> initializationPaths = new HashMap<>();

    /**
     * Add execution path that leads to the Step with fields initialized up to that point.
     *
     * @param path Execution path
     * @param previouslyInitializedFields Previously initialized fields
     * @return true, if the initialization set is unique
     */
    boolean addPath(String path, Set<String> previouslyInitializedFields) {
      Set<String> pathsToRemove = new HashSet<>();
      for (Map.Entry<String, Set<String>> existingEntry : initializationPaths.entrySet()) {
        Set<String> existingInitializedFields = existingEntry.getValue();

        if (previouslyInitializedFields.containsAll(existingInitializedFields)) {
          // If there exists a path that is a full subset of initialized parameters, we don't add
          // the new path.
          return false;
        } else if (existingInitializedFields.containsAll(previouslyInitializedFields)) {
          // If the new path is a full subset of an existing path, we replace the existing path.
          // TODO: does this matter?
          pathsToRemove.add(existingEntry.getKey());
        }
      }

      pathsToRemove.forEach(p -> initializationPaths.remove(p));
      initializationPaths.put(path, previouslyInitializedFields);
      return true;
    }
  }

  void nullAwayCheck(
      FlowTypeRecord flow,
      String firstStepName,
      Map<String, StepCallContext> stepCalls,
      StateAccessConfig flowStateAccessConfig) {
    Set<String> constructorInitializedFields = new HashSet<>();
    for (StateField fields : flowStateAccessConfig.stateFieldMap.values()) {
      if (fields.isFinal && !fields.isNullable) {
        constructorInitializedFields.add(fields.field.getName());
      }
    }

    Map<String, StepInitializationEntry> stepsInitializationEntries = new HashMap<>();
    populateInitializedPaths(
        constructorInitializedFields,
        flow,
        checkNotNull(stepCalls.get(firstStepName)),
        stepCalls,
        stepsInitializationEntries,
        "*");
  }

  void populateInitializedPaths(
      Set<String> previouslyInitializedFields,
      FlowTypeRecord flow,
      StepCallContext step,
      Map<String, StepCallContext> stepCalls,
      Map<String, StepInitializationEntry> stepsInitializationEntries,
      String path) {
    String stepName = step.getStepInfo().stepName();
    StepInitializationEntry stepsInitializationEntry = stepsInitializationEntries.get(stepName);
    if (stepsInitializationEntry == null) {
      stepsInitializationEntry = new StepInitializationEntry();
      stepsInitializationEntries.put(stepName, stepsInitializationEntry);
    }

    if (stepsInitializationEntry.addPath(path, previouslyInitializedFields)) {
      // If this is a new unique set of parameters, we do the following
      // 1) make sure we don't have uninitialized parameters in this Step
      for (ExpectedInitializedField nonNullField :
          step.stepParameterInitProfile().stepExpectsTheFollowingFieldsInitialized) {
        if (!previouslyInitializedFields.contains(nonNullField.fieldName)) {
          throw new IllegalStateException(
              String.format(
                  "Step [%s / %s] of Flow [%s] expects to find the state field [%s] initialized, but it's not initialized in the following execution path [%s]",
                  step.getStepInfo().stepName(),
                  nonNullField.functionName,
                  flow.flowTypeName,
                  nonNullField.fieldName,
                  path + " -> " + stepName));
        }
      }

      // 2) Add parameters initialized by this step to initialized set, and test next steps
      Set<String> parametersInitializedByStep = new HashSet<>(previouslyInitializedFields);
      parametersInitializedByStep.addAll(step.stepParameterInitProfile().fieldsInitializedByStep);

      for (InternalTransition transition : step.getTransitions()) {
        if (!transition.isTerminal()) {
          populateInitializedPaths(
              parametersInitializedByStep,
              flow,
              checkNotNull(stepCalls.get(transition.getStepName())),
              stepCalls,
              stepsInitializationEntries,
              path + " -> " + stepName);
        }
      }
    }
  }

  // ----------- State config -----------

  public static StateAccessConfig createStateAccess(Map<String, StateFieldRecord> state) {
    Map<String, StateField> stateParameterMap = new HashMap<>();

    for (StateFieldRecord stateFieldRecord : state.values())
      stateParameterMap.put(
          stateFieldRecord.stateFieldName,
          new StateField(
              stateFieldRecord.stateFieldClass,
              stateFieldRecord.stateField,
              stateFieldRecord.isFinal,
              stateFieldRecord.isNullable));

    return new StateAccessConfig(stateParameterMap);
  }

  // ----------- Event call context -----------

  void addFunctions(
      Map<EventType, List<EventFunction>> eventFunctionsByType,
      EventType[] events,
      EventFunction eventFunction) {
    for (EventType event : events) {
      List<EventFunction> functionList =
          eventFunctionsByType.computeIfAbsent(event, k -> new ArrayList<>());
      functionList.add(eventFunction);
    }
  }

  EventProfileForFlowTypeCallContext createEventProfileContext(
      FlowTypeRecord flowTypeRecord,
      EventProfileContainerRecord eventProfile,
      StateAccessConfig flowStateAccessConfig,
      Map<Class<?>, Map<String, GlobalFunctionRecord>> globalFunctionsByContainerAndName) {
    StateAccessConfig eventProfileStateAccessConfig = createStateAccess(eventProfile.state);

    Map<EventType, List<EventFunction>> eventFunctionsByType = new HashMap<>();

    // TODO: transform to FunctionCallContext
    for (EventRecord eventRecord : eventProfile.eventFunctions.values()) {
      EventFunction eventFunction =
          stepCallContextCreator.createEventContext(
              flowTypeRecord, eventRecord, flowStateAccessConfig, eventProfileStateAccessConfig);
      addFunctions(eventFunctionsByType, eventRecord.annotation.types(), eventFunction);
    }

    for (EventCallRecord eventCallRecord : eventProfile.eventCalls.values()) {
      GlobalFunctionRecord globalFunction = null;
      Map<String, GlobalFunctionRecord> containerFunctions = globalFunctionsByContainerAndName.get(eventCallRecord.globalFunctionContainer);
      if (containerFunctions != null) {
        globalFunction = containerFunctions.get(eventCallRecord.globalFunctionName);
      }
      if (globalFunction == null)
        throw new IllegalStateException(
            String.format(
                "EventCall refers to non-existent GlobalFunction: EventProfile [%s] GlobalFunctionContainer [%s] GlobalFunction [%s]",
                eventProfile.eventProfileName, eventCallRecord.globalFunctionContainer, eventCallRecord.globalFunctionName));

      EventFunction eventFunction =
          stepCallContextCreator.createEventCallContext(
              flowTypeRecord,
              eventCallRecord,
              globalFunction,
              flowStateAccessConfig,
              eventProfileStateAccessConfig);
      addFunctions(eventFunctionsByType, eventCallRecord.annotation.types(), eventFunction);
    }

    return new EventProfileForFlowTypeCallContext(
        eventProfile.eventProfileName,
        eventProfile.eventProfileContainerType,
        eventFunctionsByType,
        eventProfileStateAccessConfig,
        flowStateAccessConfig);
  }

  // ----------- Step call context -----------

  public StepCallContext createStepContext(
      FlowTypeRecord flowTypeRecord,
      StateAccessConfig flowStateAccess,
      StepRecord stepRecord,
      Map<Class<?>, Map<String, GlobalFunctionRecord>> globalFunctionsByContainerAndName) {
    String flowName = flowTypeRecord.flowTypeName;

    String stepName = stepRecord.stepName;
    String transitionerName = stepRecord.transitionerName;
    Class<?> globalTransitionerContainer = stepRecord.globalTransitionerContainer;
    String globalTransitionerName = stepRecord.globalTransitionerName;

    if (StringUtils.isEmpty(transitionerName) && StringUtils.isEmpty(globalTransitionerName))
      throw new IllegalStateException(
          String.format(
              "Step function should have either transit or globalTransit set. Flow [%s] Step [%s]",
              flowName, stepName));

    if (StringUtils.isNotEmpty(transitionerName) && StringUtils.isNotEmpty(globalTransitionerName))
      throw new IllegalStateException(
          String.format(
              "Step function can't have both transit or globalTransit set. Flow [%s] Step [%s]",
              flowName, stepName));

    if (StringUtils.isNotEmpty(transitionerName)) {
      TransitionerRecord transitionerRecord = flowTypeRecord.transitioners.get(transitionerName);

      if (transitionerRecord != null) {
        return stepCallContextCreator.createStepContext(
            flowTypeRecord, stepRecord, transitionerRecord, flowStateAccess);
      } else {
        TransitionerCallRecord transitionerCallRecord =
            checkNotNull(flowTypeRecord.transitionerCalls.get(transitionerName));
        GlobalFunctionRecord transitGlobalFunction = null;
        Map<String, GlobalFunctionRecord> containerFunctions = globalFunctionsByContainerAndName.get(transitionerCallRecord.globalFunctionContainer);
        if (containerFunctions != null) {
          transitGlobalFunction = containerFunctions.get(transitionerCallRecord.globalFunctionName);
        }
        if (transitGlobalFunction == null)
          throw new IllegalStateException(
              String.format(
                  "TransitionerCall refers to non-existent GlobalFunction: Flow [%s] Transitioner [%s] GlobalFunctionContainer [%s] GlobalFunction [%s]",
                  flowName, transitionerName, transitionerCallRecord.globalFunctionContainer, transitionerCallRecord.globalFunctionName));

        return stepCallContextCreator.createStepContext(
            flowTypeRecord,
            stepRecord,
            transitionerCallRecord,
            transitGlobalFunction,
            flowStateAccess);
      }
    } else {
      GlobalFunctionRecord transitGlobalFunction = null;
      Map<String, GlobalFunctionRecord> containerFunctions = globalFunctionsByContainerAndName.get(globalTransitionerContainer);
      if (containerFunctions != null) {
        transitGlobalFunction = containerFunctions.get(globalTransitionerName);
      }
      if (transitGlobalFunction == null) {
        throw new IllegalStateException(
            String.format(
                "TransitionerCall refers to non-existent GlobalFunction: Flow [%s] Transitioner [%s] GlobalFunctionContainer [%s] GlobalFunction [%s]",
                flowName, transitionerName, globalTransitionerContainer, globalTransitionerName));
      }
      return stepCallContextCreator.createStepContext(
          flowTypeRecord,
          stepRecord,
          transitGlobalFunction,
          flowStateAccess);
    }
  }

  public StepCallContext createStepCallContext(
      FlowTypeRecord flowTypeRecord,
      StateAccessConfig flowStateAccess,
      StepCallRecord stepCallRecord,
      Map<Class<?>, Map<String, GlobalFunctionRecord>> globalFunctionsByContainerAndName) {
    String flowName = flowTypeRecord.flowTypeName;

    String stepCallName = stepCallRecord.stepName;
    String transitionerName = stepCallRecord.transitionerName;
    Class<?> globalTransitionerContainer = stepCallRecord.globalTransitionerContainer;
    String globalTransitionerName = stepCallRecord.globalTransitionerName;
    GlobalFunctionRecord globalFunction = null;
    {
      Map<String, GlobalFunctionRecord> containerFunctions = globalFunctionsByContainerAndName.get(stepCallRecord.globalFunctionContainer);
      if (containerFunctions != null) {
        globalFunction = containerFunctions.get(stepCallRecord.globalFunctionName);
      }
    }
    if (globalFunction == null)
      throw new IllegalStateException(
          String.format(
              "StepCall refers to non-existent GlobalFunction: Flow [%s] Step [%s] GlobalFunctionContainer [%s] GlobalFunction [%s]",
              flowName, stepCallName, stepCallRecord.globalFunctionContainer, stepCallRecord.globalFunctionName));

    if (StringUtils.isEmpty(transitionerName) && StringUtils.isEmpty(globalTransitionerName))
      throw new IllegalStateException(
          String.format(
              "Step call should have either transit or globalTransit set. Flow [%s] Step [%s]",
              flowName, stepCallName));

    if (StringUtils.isNotEmpty(transitionerName) && StringUtils.isNotEmpty(globalTransitionerName))
      throw new IllegalStateException(
          String.format(
              "Step call can't have both transit or globalTransit set. Flow [%s] Step [%s]",
              flowName, stepCallName));

    if (StringUtils.isNotEmpty(transitionerName)) {
      TransitionerRecord transitionerRecord = flowTypeRecord.transitioners.get(transitionerName);

      if (transitionerRecord != null) {
        return stepCallContextCreator.createStepCallContext(
            flowTypeRecord, stepCallRecord, globalFunction, transitionerRecord, flowStateAccess);
      } else {
        TransitionerCallRecord transitionerCallRecord =
            checkNotNull(flowTypeRecord.transitionerCalls.get(transitionerName));
        GlobalFunctionRecord transitGlobalFunction = null;
        {
          Map<String, GlobalFunctionRecord> containerFunctions = globalFunctionsByContainerAndName.get(transitionerCallRecord.globalFunctionContainer);
          if (containerFunctions != null) {
            transitGlobalFunction = containerFunctions.get(transitionerCallRecord.globalFunctionName);
          }
        }
        if (transitGlobalFunction == null)
          throw new IllegalStateException(
              String.format(
                  "TransitionerCall refers to non-existent GlobalFunction: Flow [%s] Transitioner [%s] GlobalFunctionContainer [%s] GlobalFunction [%s]",
                  flowName, transitionerName, transitionerCallRecord.globalFunctionContainer, transitionerCallRecord.globalFunctionName));

        return stepCallContextCreator.createStepCallContext(
            flowTypeRecord,
            stepCallRecord,
            globalFunction,
            transitionerCallRecord,
            transitGlobalFunction,
            flowStateAccess);
      }
    } else {
      GlobalFunctionRecord transitGlobalFunction = null;
      Map<String, GlobalFunctionRecord> containerFunctions = globalFunctionsByContainerAndName.get(globalTransitionerContainer);
      if (containerFunctions != null) {
        transitGlobalFunction = containerFunctions.get(globalTransitionerName);
      }
      if (transitGlobalFunction == null) {
        throw new IllegalStateException(
            String.format(
                "TransitionerCall refers to non-existent GlobalFunction: Flow [%s] Transitioner [%s] GlobalFunctionContainer [%s] GlobalFunction [%s]",
                flowName, transitionerName, globalTransitionerContainer, globalTransitionerName));
      }
      return stepCallContextCreator.createStepCallContext(
          flowTypeRecord,
          stepCallRecord,
          globalFunction,
          transitGlobalFunction,
          flowStateAccess);
    }
  }

  public StepCallContext createStepAndTransitContext(
      FlowTypeRecord flowTypeRecord,
      StateAccessConfig flowStateAccess,
      StepAndTransitRecord stepAndTransitRecord) {
    String flowName = flowTypeRecord.flowTypeName;
    return stepCallContextCreator.createStepAndTransitContext(
        flowTypeRecord, stepAndTransitRecord, flowStateAccess);
  }

  public StepCallContext createStepAndTransitCallContext(
      FlowTypeRecord flowTypeRecord,
      StateAccessConfig flowStateAccess,
      StepAndTransitCallRecord stepAndTransitCallRecord,
      Map<Class<?>, Map<String, GlobalFunctionRecord>> globalFunctionsByContainerAndName) {
    String flowName = flowTypeRecord.flowTypeName;

    String stepAndTransitName = stepAndTransitCallRecord.stepName;

    GlobalFunctionRecord globalFunction = null;
    Map<String, GlobalFunctionRecord> containerFunctions = globalFunctionsByContainerAndName.get(stepAndTransitCallRecord.globalFunctionContainer);
    if (containerFunctions != null) {
      globalFunction = containerFunctions.get(stepAndTransitCallRecord.globalFunctionName);
    }
    if (globalFunction == null)
      throw new IllegalStateException(
          String.format(
              "StepCall refers to non-existent GlobalFunction: Flow [%s] Step [%s] GlobalFunctionContainer [%s] GlobalFunction [%s]",
              flowName, stepAndTransitName, stepAndTransitCallRecord.globalFunctionContainer, stepAndTransitCallRecord.globalFunctionName));

    return stepCallContextCreator.createStepAndTransitCallContext(
        flowTypeRecord, stepAndTransitCallRecord, globalFunction, flowStateAccess);
  }

  // ----------- First step name -----------

  String getFirstStepName(FlowTypeRecord flowTypeRecord) {
    String flowName = flowTypeRecord.flowTypeName;
    String firstStepName = flowTypeRecord.getFirstStepName();

    if (firstStepName == null)
      throw new IllegalStateException(
          String.format("First step is not indicated: Flow [%s]", flowName));

    return firstStepName;
  }
}
