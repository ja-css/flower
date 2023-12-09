package com.flower.engine.configuration;

import com.flower.anno.event.EventType;
import com.flower.anno.params.common.Exec;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.events.EventInfo;
import com.flower.anno.params.events.FlowException;
import com.flower.anno.params.events.FlowInfo;
import com.flower.anno.params.events.InFromFlow;
import com.flower.anno.params.events.StepInfo;
import com.flower.anno.params.events.TransitionInfo;
import com.flower.anno.params.step.FlowFactory;
import com.flower.anno.params.step.FlowRepo;
import com.flower.anno.params.transit.InRet;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowFactoryPrm;
import com.flower.conf.FlowInfoPrm;
import com.flower.conf.FlowRepoPrm;
import com.flower.conf.InOutPrm;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.StepInfoPrm;
import com.flower.conf.Transition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class FunctionRecord {
  List<FunctionParameterRecord> loadGlobalFunctionSignature(Method method) {
    return loadFunctionSignature(null, method, FunctionType.GLOBAL, null);
  }

  List<FunctionParameterRecord> loadEventFunctionSignature(Method method, EventType[] eventTypes) {
    return loadFunctionSignature(null, method, FunctionType.EVENT, eventTypes);
  }

  List<FunctionParameterRecord> loadFunctionSignature(
      Class<?> flowType, Method method, FunctionType functionType) {
    return loadFunctionSignature(flowType, method, functionType, null);
  }

  FunctionReturnValueRecord loadFunctionReturnValue(@Nullable Class<?> flowType, Method method) {
    Type genericReturnValueType = method.getGenericReturnType();
    return new FunctionReturnValueRecord(
        flowType, genericReturnValueType, method.getAnnotation(Nullable.class) != null);
  }

  private List<FunctionParameterRecord> loadFunctionSignature(
      @Nullable Class<?> flowType,
      Method method,
      FunctionType functionType,
      @Nullable EventType[] eventTypes) {
    if (eventTypes == null) eventTypes = new EventType[] {};

    List<FunctionParameterRecord> parameters = new ArrayList<>();
    String physicalFunctionName =
        String.format("%s.%s", method.getDeclaringClass().getCanonicalName(), method.getName());

    Type[] genericParameterTypes = method.getGenericParameterTypes();
    Parameter[] params = method.getParameters();

    FunctionParameterRecord inRetFound = null, intRetOrExceptionFound = null;

    Map<String, FunctionParameterRecord> parameterMap = new HashMap<>();
    for (int i = 0; i < params.length; i++) {
      Parameter prm = params[i];
      Type genericParameterType = genericParameterTypes[i];
      FunctionParameterRecord parameter =
          loadParameter(
              flowType, prm, genericParameterType, physicalFunctionName, functionType, eventTypes);
      parameters.add(parameter);

      if (parameter.type.equals(FunctionParameterType.IN_RET)) inRetFound = parameter;
      if (parameter.type.equals(FunctionParameterType.IN_RET_OR_EXCEPTION))
        intRetOrExceptionFound = parameter;

      if (!parameterMap.containsKey(parameter.name)) parameterMap.put(parameter.name, parameter);
      else
        throw new IllegalStateException(
            "Conflicting ParameterNames: name ["
                + parameter.name
                + "] Function Method ["
                + method
                + "]");
    }

    // The reason why we can't have both @InRet and @InRetOrException, is because in cases when an
    // Exception is raised
    // the value of @InRet is undefined. For that reason we can only have @InRet in cases when
    // Exception interrupts
    // execution on Flower level, while @InRetOrException allows for the flow to proceed.
    if (inRetFound != null && intRetOrExceptionFound != null)
      throw new IllegalStateException(
          "Function can't have both IN_RET and IN_RET_OR_EXCEPTION parameters: IN_RET name ["
              + inRetFound.name
              + "] IN_RET_OR_EXCEPTION name ["
              + intRetOrExceptionFound.name
              + "] Function Method ["
              + method
              + "]");

    return parameters;
  }

  FunctionParameterRecord loadParameter(
      @Nullable Class<?> flowType,
      Parameter prm,
      Type genericParameterType,
      String physicalFunctionName,
      FunctionType functionType,
      @Nullable EventType[] eventTypes) {
    In inAnnotation = prm.getAnnotation(In.class);
    Out outAnnotation = prm.getAnnotation(Out.class);
    InOut inOutAnnotation = prm.getAnnotation(InOut.class);
    Exec executorAnnotation = prm.getAnnotation(Exec.class);
    StepRef stepRefAnnotation = prm.getAnnotation(StepRef.class);
    Terminal terminalAnnotation = prm.getAnnotation(Terminal.class);
    InRet inRetAnnotation = prm.getAnnotation(InRet.class);
    InRetOrException inRetOrExceptionAnnotation = prm.getAnnotation(InRetOrException.class);
    FlowFactory flowFactoryAnnotation = prm.getAnnotation(FlowFactory.class);
    FlowRepo flowRepoAnnotation = prm.getAnnotation(FlowRepo.class);

    InFromFlow inFromFlowAnnotation = prm.getAnnotation(InFromFlow.class);

    EventInfo eventInfoAnnotation = prm.getAnnotation(EventInfo.class);
    FlowInfo flowInfoAnnotation = prm.getAnnotation(FlowInfo.class);
    StepInfo stepInfoAnnotation = prm.getAnnotation(StepInfo.class);
    TransitionInfo transitionInfoAnnotation = prm.getAnnotation(TransitionInfo.class);
    FlowException flowExceptionAnnotation = prm.getAnnotation(FlowException.class);

    Nullable nullableAnnotation = prm.getAnnotation(Nullable.class);

    // Only one Flower Parameter annotation is allowed
    Annotation[] annotations =
        new Annotation[] {
          inAnnotation,
          outAnnotation,
          inOutAnnotation,
          executorAnnotation,
          stepRefAnnotation,
          terminalAnnotation,
          inRetAnnotation,
          inRetOrExceptionAnnotation,
          flowFactoryAnnotation,
          flowRepoAnnotation,
          inFromFlowAnnotation,
          eventInfoAnnotation,
          flowInfoAnnotation,
          stepInfoAnnotation,
          transitionInfoAnnotation,
          flowExceptionAnnotation
        };
    Annotation first = null;
    for (Annotation annotation : annotations) {
      if (annotation != null) {
        if (first == null) first = annotation;
        else {
          throw new AnnotationFormatError(
              String.format(
                  "Function %s. Parameter %s is annotated as both: %s, %s.",
                  physicalFunctionName, prm.getName(), first, annotation));
        }
      }
    }

    if (nullableAnnotation != null
        && inAnnotation == null
        && inRetAnnotation == null
        && inFromFlowAnnotation == null) {
      throw new AnnotationFormatError(
          String.format(
              "Function %s. Parameter %s cannot be annotated as @Nullable. Only @In, @InRet and @InFromFlow parameters can be Nullable.",
              physicalFunctionName, prm.getName()));
    }

    if (inAnnotation != null) {
      return new FunctionParameterRecord(
          inAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (outAnnotation != null) {
      if (!prm.getType().equals(OutPrm.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @Out should be of type %s",
                physicalFunctionName, prm.getName(), OutPrm.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          outAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (inOutAnnotation != null) {
      if (!prm.getType().equals(InOutPrm.class) && !prm.getType().equals(NullableInOutPrm.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @InOut should be of type %s or %s",
                physicalFunctionName,
                prm.getName(),
                InOutPrm.class.getCanonicalName(),
                NullableInOutPrm.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          inOutAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (executorAnnotation != null) {
      if (!prm.getType().equals(Executor.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @Out should be of type %s",
                physicalFunctionName, prm.getName(), OutPrm.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          executorAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (stepRefAnnotation != null) {
      if (functionType != FunctionType.TRANSIT
          && functionType != FunctionType.STEP_AND_TRANSIT
          && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @StepRef can only be used in TransitFunction",
                physicalFunctionName, prm.getName()));
      }
      if (!prm.getType().equals(Transition.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @StepRef should be of type %s",
                physicalFunctionName, prm.getName(), Transition.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          stepRefAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (terminalAnnotation != null) {
      if (functionType != FunctionType.TRANSIT
          && functionType != FunctionType.STEP_AND_TRANSIT
          && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @Terminal can only be used in TransitFunction",
                physicalFunctionName, prm.getName()));
      }
      if (!prm.getType().equals(Transition.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @Terminal should be of type %s",
                physicalFunctionName, prm.getName(), Transition.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          terminalAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (inRetAnnotation != null) {
      if (functionType != FunctionType.TRANSIT && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @InRet can only be used in TransitFunction",
                physicalFunctionName, prm.getName()));
      }
      return new FunctionParameterRecord(
          inRetAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (inRetOrExceptionAnnotation != null) {
      if (functionType != FunctionType.TRANSIT && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @InRetOrException can only be used in TransitFunction",
                physicalFunctionName, prm.getName()));
      }
      if (!prm.getType().equals(ReturnValueOrException.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @InRetOrException should be of type %s",
                physicalFunctionName,
                prm.getName(),
                ReturnValueOrException.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          inRetOrExceptionAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (flowFactoryAnnotation != null) {
      if (functionType != FunctionType.STEP
          && functionType != FunctionType.STEP_AND_TRANSIT
          && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowTypeFactory can only be used in StepFunction",
                physicalFunctionName, prm.getName()));
      }
      if (!prm.getType().equals(FlowFactoryPrm.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowTypeFactory should be of type %s",
                physicalFunctionName, prm.getName(), FlowFactoryPrm.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          flowFactoryAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (flowRepoAnnotation != null) {
      if (functionType != FunctionType.STEP
          && functionType != FunctionType.STEP_AND_TRANSIT
          && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowRepo can only be used in StepFunction",
                physicalFunctionName, prm.getName()));
      }
      if (!prm.getType().equals(FlowRepoPrm.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowRepo should be of type %s",
                physicalFunctionName, prm.getName(), FlowRepoPrm.class.getCanonicalName()));
      }
      return new FunctionParameterRecord(
          flowRepoAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (eventInfoAnnotation != null) {
      if (!prm.getType().equals(EventType.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @EventInfo should be of type %s",
                physicalFunctionName, prm.getName(), EventType.class.getCanonicalName()));
      }
      if (functionType != FunctionType.EVENT && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @EventInfo can only be used in EventFunction",
                physicalFunctionName, prm.getName()));
      }
      return new FunctionParameterRecord(
          eventInfoAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (inFromFlowAnnotation != null) {
      if (nullableAnnotation == null) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @InFromFlow must be @Nullable",
                physicalFunctionName, prm.getName()));
      }
      return new FunctionParameterRecord(
          inFromFlowAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (flowInfoAnnotation != null) {
      if (!prm.getType().equals(FlowInfoPrm.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowInfo should be of type %s",
                physicalFunctionName, prm.getName(), FlowInfoPrm.class.getCanonicalName()));
      }
      if (functionType != FunctionType.EVENT && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowInfo can only be used in EventFunction",
                physicalFunctionName, prm.getName()));
      }
      return new FunctionParameterRecord(
          flowInfoAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (stepInfoAnnotation != null) {
      Set<EventType> allowedEventTypes =
          ImmutableSet.of(
              EventType.BEFORE_STEP,
              EventType.AFTER_STEP,
              EventType.BEFORE_STEP_ITERATION,
              EventType.AFTER_STEP_ITERATION,
              EventType.BEFORE_EXEC,
              EventType.AFTER_EXEC,
              EventType.BEFORE_TRANSIT,
              EventType.AFTER_TRANSIT);
      if (!prm.getType().equals(StepInfoPrm.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @StepInfo should be of type %s",
                physicalFunctionName, prm.getName(), StepInfoPrm.class.getCanonicalName()));
      }
      if (functionType != FunctionType.EVENT && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @StepInfo can only be used in EventFunction",
                physicalFunctionName, prm.getName()));
      }
      if (notAllEventTypesSupported(allowedEventTypes, Preconditions.checkNotNull(eventTypes))) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @StepInfo can only be used in EventFunction for the following EventTypes %s",
                physicalFunctionName, prm.getName(), allowedEventTypes));
      }
      return new FunctionParameterRecord(
          stepInfoAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (transitionInfoAnnotation != null) {
      Set<EventType> allowedEventTypes =
          ImmutableSet.of(EventType.AFTER_TRANSIT /*, AFTER_STEP_ITERATION, AFTER_STEP*/);
      if (!prm.getType().equals(Transition.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @TransitionInfo should be of type %s",
                physicalFunctionName, prm.getName(), Transition.class.getCanonicalName()));
      }
      if (functionType != FunctionType.EVENT && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @TransitionInfo can only be used in EventFunction",
                physicalFunctionName, prm.getName()));
      }
      if (notAllEventTypesSupported(allowedEventTypes, Preconditions.checkNotNull(eventTypes))) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes %s",
                physicalFunctionName, prm.getName(), allowedEventTypes));
      }
      return new FunctionParameterRecord(
          transitionInfoAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else if (flowExceptionAnnotation != null) {
      Set<EventType> allowedEventTypes = ImmutableSet.of(EventType.FLOW_EXCEPTION);
      if (!prm.getType().equals(Throwable.class)) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowException should be of type %s",
                physicalFunctionName, prm.getName(), Throwable.class.getCanonicalName()));
      }
      if (functionType != FunctionType.EVENT && functionType != FunctionType.GLOBAL) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowException can only be used in EventFunction",
                physicalFunctionName, prm.getName()));
      }
      if (notAllEventTypesSupported(allowedEventTypes, Preconditions.checkNotNull(eventTypes))) {
        throw new AnnotationFormatError(
            String.format(
                "Function %s. Parameter %s annotated as @FlowException can only be used in EventFunction for the following EventTypes %s",
                physicalFunctionName, prm.getName(), allowedEventTypes));
      }
      return new FunctionParameterRecord(
          flowExceptionAnnotation, nullableAnnotation, prm, genericParameterType, flowType);
    } else {
      // Flower Parameter annotation is required
      throw new AnnotationFormatError(
          String.format(
              "Function %s. Parameter %s should be annotated as one of [@In, @Out, @InOut, @StepRef, @Terminal, @InRet, @InRetOrException, @FlowFactory, @FlowRepo, @EventInfo, @FlowInfo, @StepInfo, @TransitionInfo, @FlowException]",
              physicalFunctionName, prm.getName()));
    }
  }

  boolean notAllEventTypesSupported(
      Set<EventType> allowedEventTypes, EventType[] eventTypesToCheck) {
    for (EventType eventType : eventTypesToCheck) {
      if (!allowedEventTypes.contains(eventType)) return true;
    }
    return false;
  }
}
