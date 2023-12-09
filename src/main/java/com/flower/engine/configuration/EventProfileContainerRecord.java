package com.flower.engine.configuration;

import com.flower.anno.event.EventCall;
import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventType;
import com.flower.anno.params.common.Output;
import com.flower.conf.NullableInOutPrm;
import com.google.common.base.Preconditions;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Annotated with
public class EventProfileContainerRecord extends ContainerRecord {
  public final Class<?> eventProfileContainerType;
  final EventProfileContainer annotation;
  public final String eventProfileName;

  public Map<String, StateFieldRecord> state;

  public Map<String, EventRecord> eventFunctions;
  public Map<String, EventCallRecord> eventCalls;

  public EventProfileContainerRecord(
      Class<?> eventProfileContainerType,
      final EventProfileContainer annotation,
      final String eventProfileName) {
    this.eventProfileContainerType = eventProfileContainerType;
    this.annotation = annotation;
    this.eventProfileName = eventProfileName;

    state = new HashMap<>();
    eventFunctions = new HashMap<>();
    eventCalls = new HashMap<>();
  }

  public void initialize() {
    state =
        FlowTypeRecord.loadStateParameters(
            eventProfileContainerType, "EventProfileContainer", eventProfileName);

    eventFunctions = loadEventFunctions();
    eventCalls = loadEventCalls();

    validateNullAway();
  }

  static class EventsForType {
    final List<EventRecord> events = new ArrayList<>();
    final List<EventCallRecord> eventCalls = new ArrayList<>();
  }

  public void validateNullAway() {
    Map<EventType, EventsForType> eventsByType = new HashMap<>();
    for (EventType eventType : EventType.values()) {
      eventsByType.put(eventType, new EventsForType());
    }
    for (EventRecord eventFunction : eventFunctions.values()) {
      for (EventType type : eventFunction.annotation.types()) {
        Preconditions.checkNotNull(eventsByType.get(type)).events.add(eventFunction);
      }
    }
    for (EventCallRecord eventCall : eventCalls.values()) {
      for (EventType type : eventCall.annotation.types()) {
        Preconditions.checkNotNull(eventsByType.get(type)).eventCalls.add(eventCall);
      }
    }

    Map<String, Boolean> stateFieldsInit = new HashMap<>();
    for (StateFieldRecord stateFieldRecord : state.values()) {
      stateFieldsInit.put(stateFieldRecord.stateFieldName, stateFieldRecord.isFinal && !stateFieldRecord.isNullable);
    }

    // Informational, in case this will be needed in future NullAway versions:
    // The following is where @InFromFlow is validated:
    //  FlowCallContextCreator.createFlowCallContext()
    //     -> FlowCallContextCreator.createEventProfile()
    //        -> StepCallContextCreator.createEventContext()

    // With EventProfiles, the order of execution assumed as the following:
    //  BEFORE_FLOW,
    //  BEFORE_STEP,
    //  BEFORE_STEP_ITERATION,
    //  BEFORE_EXEC,
    //  AFTER_EXEC,
    //  BEFORE_TRANSIT,
    //  AFTER_TRANSIT,
    //  AFTER_STEP_ITERATION,
    //  AFTER_STEP,
    //  AFTER_FLOW
    // A special case with Event Profiles is `FLOW_EXCEPTION`. In this case the corresponding
    // function can use fields initialized in `BEFORE_FLOW`, `BEFORE_STEP`, `BEFORE_STEP_ITERATION`,
    // `BEFORE_EXEC`, but due to specifics of this exception we don't consider any fields
    // initialized by the function itself as initialized in any other functions.
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.BEFORE_FLOW);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.BEFORE_STEP);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.BEFORE_STEP_ITERATION);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.BEFORE_EXEC);

    testNullAway(eventsByType, stateFieldsInit, EventType.FLOW_EXCEPTION);

    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.AFTER_EXEC);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.BEFORE_TRANSIT);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.AFTER_TRANSIT);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.AFTER_STEP_ITERATION);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.AFTER_STEP);
    stateFieldsInit = testNullAway(eventsByType, stateFieldsInit, EventType.AFTER_FLOW);
  }

  Map<String, Boolean> testNullAway(
      Map<EventType, EventsForType> eventsMapByType,
      Map<String, Boolean> stateFieldsInit,
      EventType eventType) {
    EventsForType eventsForType = Preconditions.checkNotNull(eventsMapByType.get(eventType));
    Map<String, Boolean> stateFieldsInitUpdate = new HashMap<>(stateFieldsInit);

    for (EventRecord event : eventsForType.events) {
      testNullAwayFunction(
          stateFieldsInit,
          event.functionSignature,
          stateFieldsInitUpdate,
          event.eventFunctionName,
          eventType);
    }

    for (EventCallRecord eventCall : eventsForType.eventCalls) {
      testNullAwayFunction(
          stateFieldsInit,
          eventCall.eventFunctionParameterOverrides,
          stateFieldsInitUpdate,
          eventCall.eventCallName,
          eventType);
    }

    return stateFieldsInitUpdate;
  }

  void testNullAwayFunction(
      Map<String, Boolean> stateFieldsInit,
      List<FunctionParameterRecord> signature,
      Map<String, Boolean> stateFieldsInitUpdate,
      String functionName,
      EventType type) {
    // Non-Nullable parameters
    for (FunctionParameterRecord param : signature) {
      if (param.type == FunctionParameterType.IN) {
        if (param.nullableAnnotation == null && !Preconditions.checkNotNull(param.inAnnotation).checkNotNull()) {
          String fieldName = Preconditions.checkNotNull(param.fieldName);
          if (!stateFieldsInit.containsKey(fieldName) || !stateFieldsInit.get(fieldName)) {
            throw new IllegalStateException(
                String.format(
                    "EventProfile %s: field %s is input to function %s event type %s @In parameter %s, but it's not initialized. Make parameter @Nullable or initialize.",
                    eventProfileName, fieldName, functionName, type, param.name));
          }
        }
      } else if (param.type == FunctionParameterType.IN_OUT) {
        Type rawType =
            param.genericParameterType instanceof ParameterizedType
                ? ((ParameterizedType) param.genericParameterType).getRawType()
                : param.genericParameterType;
        if (!rawType.equals(NullableInOutPrm.class) && !Preconditions.checkNotNull(param.inOutAnnotation).checkNotNull()) {
          String fieldName = Preconditions.checkNotNull(param.fieldName);
          if (!stateFieldsInit.containsKey(fieldName) || !stateFieldsInit.get(fieldName)) {
            throw new IllegalStateException(
                String.format(
                    "EventProfile %s: field %s is input to function %s event type %s @InOut parameter %s, but it's not initialized. Change parameter type to '@InOut NullableInOutPrm prm' or initialize.",
                    eventProfileName, fieldName, functionName, type, param.name));
          }
        }
      }

      // Mandatory output is initializing fields
      if (param.type == FunctionParameterType.OUT
          && Preconditions.checkNotNull(param.outAnnotation).out() == Output.MANDATORY) {
        stateFieldsInitUpdate.put(param.fieldName, true);
      } else if (param.type == FunctionParameterType.IN_OUT
          && (Preconditions.checkNotNull(param.inOutAnnotation).out() == Output.MANDATORY ||
              Preconditions.checkNotNull(param.inOutAnnotation).checkNotNull())) {
        stateFieldsInitUpdate.put(param.fieldName, true);
      } else if (param.type == FunctionParameterType.IN
          && Preconditions.checkNotNull(param.inAnnotation).checkNotNull()) {
        stateFieldsInitUpdate.put(param.fieldName, true);
      }
    }
  }

  public static void validateFunction(Method method) {
    // Event function can't be generic
    if (Arrays.stream(method.getGenericParameterTypes())
        .anyMatch(type -> type instanceof TypeVariable))
      throw new IllegalStateException(
          String.format(
              "Flower Event Function can't be generic: type [%s] method [%s]",
              method.getDeclaringClass(), method.getName()));

    // Validation: Flower function must be static
    if ((method.getModifiers() & Modifier.STATIC) == 0)
      throw new AnnotationFormatError(
          String.format(
              "Flower Event Function must be static: type [%s] method [%s]",
              method.getDeclaringClass(), method.getName()));
  }

  public Map<String, EventRecord> loadEventFunctions() {
    Map<String, EventRecord> eventFunctions = new HashMap<>();
    List<Method> eventFunctionList =
        getMethodsAnnotatedWith(eventProfileContainerType, EventFunction.class);
    for (Method eventFunctionMethod : eventFunctionList) {
      validateFunction(eventFunctionMethod);
      EventFunction annotation = eventFunctionMethod.getAnnotation(EventFunction.class);
      String eventFunctionName = annotation.name();
      if (eventFunctionName.trim().equals("")) eventFunctionName = eventFunctionMethod.getName();
      EventRecord eventRecord = new EventRecord(eventFunctionMethod, annotation, eventFunctionName);
      eventRecord.initialize();

      if (eventFunctions.containsKey(eventFunctionName))
        throw new IllegalStateException(
            "Duplicate EventFunction name. EventFunctionName: ["
                + eventFunctionName
                + "] Container class: ["
                + eventProfileContainerType
                + "]");
      if (eventCalls.containsKey(eventFunctionName))
        throw new IllegalStateException(
            "Duplicate EventFunction name. EventFunctionName: ["
                + eventFunctionName
                + "] Container class: ["
                + eventProfileContainerType
                + "]");
      eventFunctions.put(eventFunctionName, eventRecord);
    }
    return eventFunctions;
  }

  public Map<String, EventCallRecord> loadEventCalls() {
    Map<String, EventCallRecord> eventCalls = new HashMap<>();
    List<Method> eventCallFunctionList =
        getMethodsAnnotatedWith(eventProfileContainerType, EventCall.class);
    for (Method eventCallMethod : eventCallFunctionList) {
      validateFunction(eventCallMethod);
      EventCall annotation = eventCallMethod.getAnnotation(EventCall.class);

      String eventCallName = annotation.name();
      if (eventCallName.trim().equals("")) eventCallName = eventCallMethod.getName();
      EventCallRecord eventCallRecord =
          new EventCallRecord(eventCallMethod, annotation, eventCallName);

      if (eventCalls.containsKey(eventCallName))
        throw new IllegalStateException(
            "Duplicate EventCall name for a Flow. EventCallName: ["
                + eventCallName
                + "] Container class: ["
                + eventProfileContainerType
                + "]");
      if (eventFunctions.containsKey(eventCallName))
        throw new IllegalStateException(
            "Duplicate EventCall name for a Flow. EventCallName: ["
                + eventCallName
                + "] Container class: ["
                + eventProfileContainerType
                + "]");
      eventCallRecord.initialize();

      eventCalls.put(eventCallName, eventCallRecord);
    }
    return eventCalls;
  }
}
