package com.flower.engine.runner.step;

import com.flower.anno.params.common.Output;
import com.flower.conf.Transition;
import com.flower.engine.configuration.EventCallRecord;
import com.flower.engine.configuration.EventRecord;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.GlobalFunctionRecord;
import com.flower.engine.configuration.StepAndTransitCallRecord;
import com.flower.engine.configuration.StepAndTransitRecord;
import com.flower.engine.configuration.StepCallRecord;
import com.flower.engine.configuration.StepRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.configuration.TransitionerCallRecord;
import com.flower.engine.configuration.TransitionerRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.runner.FlowRunner;
import com.flower.engine.runner.callfunc.FunctionCallContext;
import com.flower.engine.runner.event.EventFunction;
import com.flower.engine.runner.parameters.FunctionCallParameterCreator;
import com.flower.engine.runner.parameters.ParameterCreationResult;
import com.flower.engine.runner.parameters.ReturnToParameterValidator;
import com.flower.engine.runner.parameters.comparison.context.GlobalFunctionAssumedType;
import com.flower.engine.runner.state.StateAccessConfig;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class StepCallContextCreator {
  final ReturnToParameterValidator returnToParameterValidator;
  final FunctionCallParameterCreator functionCallParameterCreator;

  public StepCallContextCreator(FlowRunner flowRunner) {
    this.functionCallParameterCreator = new FunctionCallParameterCreator(flowRunner);
    this.returnToParameterValidator = new ReturnToParameterValidator();
  }

  Type extractReturnType(Type returnType) {
    if (returnType instanceof ParameterizedType)
      if (((ParameterizedType) returnType).getRawType().equals(ListenableFuture.class))
        returnType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
    return returnType;
  }

  List<GlobalFunctionAssumedType> checkReturnValue(
      @Nullable String returnToFieldName,
      boolean isGlobalFunction,
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      Method functionOrCallMethod,
      String functionOrCallName,
      StateAccessConfig stateAccess,
      Method function) {
    // If global function exists, check global -> call
    if (isGlobalFunction) {
      Nullable callNullable = functionOrCallMethod.getAnnotation(Nullable.class);
      Nullable functionNullable = function.getAnnotation(Nullable.class);
      if ((callNullable == null && functionNullable != null)
          || (callNullable != null && functionNullable == null)) {
        throw new IllegalStateException(
            String.format(
                "Return value @Nullable annotations should match in CallFunction and GlobalFunction: Flow [%s] Function [%s]",
                flowTypeRecord.flowTypeName, functionOrCallName));
      }

      Type callReturnType = extractReturnType(functionOrCallMethod.getGenericReturnType());
      Type globalReturnType = extractReturnType(function.getGenericReturnType());

      return returnToParameterValidator.fromCallParameter(
          flowTypeRecord,
          functionFlowType,
          functionOrCallMethod,
          functionOrCallName,
          function,
          stateAccess,
          returnToFieldName,
          callReturnType,
          globalReturnType);
    } else {
      if (returnToFieldName != null) {
        Type returnType = extractReturnType(functionOrCallMethod.getGenericReturnType());

        if (returnType instanceof ParameterizedType)
          if (((ParameterizedType) returnType).getRawType().equals(ListenableFuture.class))
            returnType = ((ParameterizedType) returnType).getActualTypeArguments()[0];

        return returnToParameterValidator.fromBaseParameter(
            flowTypeRecord,
            functionFlowType,
            functionOrCallName,
            stateAccess,
            returnToFieldName,
            returnType);
      } else return ImmutableList.of();
    }
  }

  FunctionCallContext createFunctionCallContext(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      Method function,
      boolean isGlobalFunction,
      @Nullable Method stepFunctionMethod,
      @Nullable FunctionReturnValueRecord stepReturnValueRecord,
      List<FunctionParameterRecord> functionSignature,
      StateAccessConfig flowStateAccess,
      @Nullable StateAccessConfig eventProfileStateAccess,
      @Nullable List<FunctionParameterRecord> parameterOverrides,
      @Nullable List<TransitParameterOverrideRecord> transitParameterOverrides,
      @Nullable Type genericInRetType,
      @Nullable String returnToFieldName,
      List<InternalTransition> stepRefPrms,
      boolean isTransit) {
    List<GlobalFunctionAssumedType> assumedReturnTypes = new ArrayList<>();

    // functionOrCallMethod is null for Global Transitioner references
    // functionFlowType is null for Events, we don't check return value for Events
    if (functionOrCallMethod != null && functionFlowType != null) {
      assumedReturnTypes =
          checkReturnValue(
              returnToFieldName,
              isGlobalFunction,
              flowTypeRecord,
              functionFlowType,
              functionOrCallMethod,
              functionOrCallName,
              flowStateAccess,
              function);
    }

    // Transit function must return Transition
    if (isTransit) {
      Type returnType = function.getGenericReturnType();

      if (returnType instanceof ParameterizedType)
        if (((ParameterizedType) returnType).getRawType().equals(ListenableFuture.class))
          returnType = ((ParameterizedType) returnType).getActualTypeArguments()[0];

      if (!returnType.equals(Transition.class))
        throw new IllegalStateException(
            String.format(
                "Transit Return value type mismatch: expected [%s], actual return type [%s] . Flow [%s] Function [%s]",
                Transition.class, returnType, flowTypeRecord.flowTypeName, functionOrCallName));

      Nullable nullable = function.getAnnotation(Nullable.class);
      if (nullable != null)
        throw new IllegalStateException(
            String.format(
                "Transit Return value can't be @Nullable: Flow [%s] Function [%s]",
                flowTypeRecord.flowTypeName, functionOrCallName));
    }

    List<FunctionCallParameter> stepFunctionParametersWithOverrides =
        createCallParameters(
            flowTypeRecord,
            functionFlowType,
            functionOrCallMethod,
            functionOrCallName,
            stepFunctionMethod,
            stepReturnValueRecord,
            isGlobalFunction ? function : null,
            functionSignature,
            flowStateAccess,
            eventProfileStateAccess,
            parameterOverrides,
            transitParameterOverrides,
            genericInRetType,
            stepRefPrms,
            assumedReturnTypes);
    function.setAccessible(true);
    return new FunctionCallContext(
        stepFunctionParametersWithOverrides, function, functionOrCallName);
  }

  // ----------- Steps and Transitioners separately -----------

  // Step function, Transitioner function
  public StepCallContext createStepContext(
      FlowTypeRecord flowTypeRecord,
      StepRecord stepRecord,
      TransitionerRecord transitionerRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepRecord.flowType,
            stepRecord.method,
            stepRecord.stepName,
            stepRecord.method,
            false,
            null,
            null,
            stepRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            null,
            null,
            stepRecord.returnTo,
            stepRefPrms,
            false);
    FunctionCallContext transitFunctionCalContext =
        createFunctionCallContext(
            flowTypeRecord,
            transitionerRecord.flowType,
            transitionerRecord.method,
            transitionerRecord.transitionerName,
            transitionerRecord.method,
            false,
            null,
            stepRecord.returnValue,
            transitionerRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            stepRecord.transitParameterOverrides,
            stepRecord.method.getGenericReturnType(),
            null,
            stepRefPrms,
            true);
    return new StepContext(
        stepFunctionCallContext,
        transitFunctionCalContext,
        stepRecord.returnTo,
        flowStateAccess,
        stepRecord.stepName,
        transitionerRecord.transitionerName,
        stepRefPrms,
        stepRecord.isFirstStep);
  }

  // Step function, Transitioner call
  public StepCallContext createStepContext(
      FlowTypeRecord flowTypeRecord,
      StepRecord stepRecord,
      TransitionerCallRecord transitionerCallRecord,
      GlobalFunctionRecord transitionerGlobalFunctionRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepRecord.flowType,
            stepRecord.method,
            stepRecord.stepName,
            stepRecord.method,
            false,
            null,
            null,
            stepRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            null,
            null,
            stepRecord.returnTo,
            stepRefPrms,
            false);
    FunctionCallContext transitFunctionCalContext =
        createFunctionCallContext(
            flowTypeRecord,
            transitionerCallRecord.flowType,
            transitionerCallRecord.method,
            transitionerCallRecord.transitionerName,
            transitionerGlobalFunctionRecord.method,
            true,
            null,
            stepRecord.returnValue,
            transitionerGlobalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            transitionerCallRecord.transitParameterOverrides,
            stepRecord.transitParameterOverrides,
            stepRecord.method.getGenericReturnType(),
            null,
            stepRefPrms,
            true);
    return new StepContext(
        stepFunctionCallContext,
        transitFunctionCalContext,
        stepRecord.returnTo,
        flowStateAccess,
        stepRecord.stepName,
        transitionerCallRecord.transitionerName,
        stepRefPrms,
        stepRecord.isFirstStep);
  }

  // Step function, Transitioner reference
  public StepCallContext createStepContext(
      FlowTypeRecord flowTypeRecord,
      StepRecord stepRecord,
      GlobalFunctionRecord transitionerGlobalFunctionRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepRecord.flowType,
            stepRecord.method,
            stepRecord.stepName,
            stepRecord.method,
            false,
            null,
            null,
            stepRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            null,
            null,
            stepRecord.returnTo,
            stepRefPrms,
            false);
    FunctionCallContext transitFunctionCalContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepRecord.flowType,
            null,
            stepRecord.stepName,
            transitionerGlobalFunctionRecord.method,
            true,
            stepRecord.method,
            stepRecord.returnValue,
            transitionerGlobalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            stepRecord.transitParameterOverrides,
            stepRecord.method.getGenericReturnType(),
            null,
            stepRefPrms,
            true);
    return new StepContext(
        stepFunctionCallContext,
        transitFunctionCalContext,
        stepRecord.returnTo,
        flowStateAccess,
        stepRecord.stepName,
        transitionerGlobalFunctionRecord.functionName,
        stepRefPrms,
        stepRecord.isFirstStep);
  }

  // Step call, Transitioner function
  public StepCallContext createStepCallContext(
      FlowTypeRecord flowTypeRecord,
      StepCallRecord stepCallRecord,
      GlobalFunctionRecord stepGlobalFunctionRecord,
      TransitionerRecord transitionerRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepCallRecord.flowType,
            stepCallRecord.method,
            stepCallRecord.stepName,
            stepGlobalFunctionRecord.method,
            true,
            null,
            null,
            stepGlobalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            stepCallRecord.stepParameterOverrides,
            null,
            null,
            stepCallRecord.returnTo,
            stepRefPrms,
            false);
    FunctionCallContext transitFunctionCalContext =
        createFunctionCallContext(
            flowTypeRecord,
            transitionerRecord.flowType,
            transitionerRecord.method,
            transitionerRecord.transitionerName,
            transitionerRecord.method,
            false,
            null,
            stepCallRecord.returnValueOverride,
            transitionerRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            stepCallRecord.transitParameterOverrides,
            stepGlobalFunctionRecord.method.getGenericReturnType(),
            null,
            stepRefPrms,
            true);
    return new StepContext(
        stepFunctionCallContext,
        transitFunctionCalContext,
        stepCallRecord.returnTo,
        flowStateAccess,
        stepCallRecord.stepName,
        transitionerRecord.transitionerName,
        stepRefPrms,
        stepCallRecord.isFirstStep);
  }

  // Step call, Transitioner call
  public StepCallContext createStepCallContext(
      FlowTypeRecord flowTypeRecord,
      StepCallRecord stepCallRecord,
      GlobalFunctionRecord stepGlobalFunctionRecord,
      TransitionerCallRecord transitionerCallRecord,
      GlobalFunctionRecord transitionerGlobalFunctionRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepCallRecord.flowType,
            stepCallRecord.method,
            stepCallRecord.stepName,
            stepGlobalFunctionRecord.method,
            true,
            null,
            null,
            stepGlobalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            stepCallRecord.stepParameterOverrides,
            null,
            null,
            stepCallRecord.returnTo,
            stepRefPrms,
            false);
    FunctionCallContext transitFunctionCalContext =
        createFunctionCallContext(
            flowTypeRecord,
            transitionerCallRecord.flowType,
            transitionerCallRecord.method,
            transitionerCallRecord.transitionerName,
            transitionerGlobalFunctionRecord.method,
            true,
            null,
            stepCallRecord.returnValueOverride,
            transitionerGlobalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            transitionerCallRecord.transitParameterOverrides,
            stepCallRecord.transitParameterOverrides,
            stepGlobalFunctionRecord.method.getGenericReturnType(),
            null,
            stepRefPrms,
            true);
    return new StepContext(
        stepFunctionCallContext,
        transitFunctionCalContext,
        stepCallRecord.returnTo,
        flowStateAccess,
        stepCallRecord.stepName,
        transitionerCallRecord.transitionerName,
        stepRefPrms,
        stepCallRecord.isFirstStep);
  }

  // Step call, Transitioner reference
  public StepCallContext createStepCallContext(
      FlowTypeRecord flowTypeRecord,
      StepCallRecord stepCallRecord,
      GlobalFunctionRecord stepGlobalFunctionRecord,
      GlobalFunctionRecord transitionerGlobalFunctionRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepCallRecord.flowType,
            stepCallRecord.method,
            stepCallRecord.stepName,
            stepGlobalFunctionRecord.method,
            true,
            null,
            null,
            stepGlobalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            stepCallRecord.stepParameterOverrides,
            null,
            null,
            stepCallRecord.returnTo,
            stepRefPrms,
            false);
    FunctionCallContext transitFunctionCalContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepCallRecord.flowType,
            null,
            stepCallRecord.stepName,
            transitionerGlobalFunctionRecord.method,
            true,
            stepCallRecord.method,
            stepCallRecord.returnValueOverride,
            transitionerGlobalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            stepCallRecord.transitParameterOverrides,
            stepGlobalFunctionRecord.method.getGenericReturnType(),
            null,
            stepRefPrms,
            true);
    return new StepContext(
        stepFunctionCallContext,
        transitFunctionCalContext,
        stepCallRecord.returnTo,
        flowStateAccess,
        stepCallRecord.stepName,
        transitionerGlobalFunctionRecord.functionName,
        stepRefPrms,
        stepCallRecord.isFirstStep);
  }

  // ----------- Steps combined with Transitioners -----------

  public StepAndTransitContext createStepAndTransitContext(
      FlowTypeRecord flowTypeRecord,
      StepAndTransitRecord stepAndTransitRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepAndTransitFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepAndTransitRecord.flowType,
            stepAndTransitRecord.method,
            stepAndTransitRecord.stepName,
            stepAndTransitRecord.method,
            false,
            null,
            null,
            stepAndTransitRecord.functionSignature,
            flowStateAccess,
            null,
            null,
            null,
            null,
            null,
            stepRefPrms,
            true);
    return new StepAndTransitContext(
        stepAndTransitFunctionCallContext,
        flowStateAccess,
        stepAndTransitRecord.stepName,
        stepRefPrms,
        stepAndTransitRecord.isFirstStep);
  }

  public StepAndTransitContext createStepAndTransitCallContext(
      FlowTypeRecord flowTypeRecord,
      StepAndTransitCallRecord stepAndTransitCallRecord,
      GlobalFunctionRecord globalFunctionRecord,
      StateAccessConfig flowStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext stepAndTransitFunctionCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            stepAndTransitCallRecord.flowType,
            stepAndTransitCallRecord.method,
            stepAndTransitCallRecord.stepName,
            globalFunctionRecord.method,
            true,
            null,
            null,
            globalFunctionRecord.functionSignature,
            flowStateAccess,
            null,
            stepAndTransitCallRecord.stepParameterOverrides,
            null,
            null,
            null,
            stepRefPrms,
            true);
    return new StepAndTransitContext(
        stepAndTransitFunctionCallContext,
        flowStateAccess,
        stepAndTransitCallRecord.stepName,
        stepRefPrms,
        stepAndTransitCallRecord.isFirstStep);
  }

  // ----------- Event Handler Functions -----------

  public EventFunction createEventCallContext(
      FlowTypeRecord flowTypeRecord,
      EventCallRecord eventCallRecord,
      GlobalFunctionRecord globalFunctionRecord,
      StateAccessConfig flowStateAccess,
      StateAccessConfig eventProfileStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext eventCallContext =
        createFunctionCallContext(
            flowTypeRecord,
            flowTypeRecord.flowType,
            eventCallRecord.method,
            eventCallRecord.eventCallName,
            globalFunctionRecord.method,
            true,
            null,
            null,
            globalFunctionRecord.functionSignature,
            flowStateAccess,
            eventProfileStateAccess,
            eventCallRecord.eventFunctionParameterOverrides,
            null,
            null,
            null,
            stepRefPrms,
            false);
    return new EventFunction(
        eventCallRecord.eventCallName, eventCallRecord.annotation.concurrency(), eventCallContext);
  }

  public EventFunction createEventContext(
      FlowTypeRecord flowTypeRecord,
      EventRecord eventRecord,
      StateAccessConfig flowStateAccess,
      StateAccessConfig eventProfileStateAccess) {
    List<InternalTransition> stepRefPrms = new ArrayList<>();

    FunctionCallContext eventContext =
        createFunctionCallContext(
            flowTypeRecord,
            flowTypeRecord.flowType,
            eventRecord.method,
            eventRecord.eventFunctionName,
            eventRecord.method,
            false,
            null,
            null,
            eventRecord.functionSignature,
            flowStateAccess,
            eventProfileStateAccess,
            null,
            null,
            null,
            null,
            stepRefPrms,
            false);
    return new EventFunction(
        eventRecord.eventFunctionName, eventRecord.annotation.concurrency(), eventContext);
  }

  // ----------- Parameters -----------

  void compareOutputs(
      Output baseOut,
      Output globalOut,
      String parameterName,
      String flowName,
      String functionOrCallName) {
    if (baseOut != globalOut) {
      throw new IllegalStateException(
          String.format(
              "Call Parameter output type should match Global Parameter. Call Parameter output type [%s]; Global Parameter output type [%s]; Argument name: [%s] Flow: [%s] Function/Call: [%s]",
              baseOut, globalOut, parameterName, flowName, functionOrCallName));
    }
  }

  void compareCallAndGlobalParameters(
      FunctionParameterRecord basePrm,
      FunctionParameterRecord globalPrm,
      String flowName,
      String functionOrCallName) {
    String parameterName = basePrm.name;
    if ((basePrm.nullableAnnotation == null && globalPrm.nullableAnnotation != null)
        || (basePrm.nullableAnnotation != null && globalPrm.nullableAnnotation == null)) {
      throw new IllegalStateException(
          String.format(
              "Call's @Nullable prm annotation should match Global. Argument name: [%s] Flow: [%s] Function/Call: [%s]",
              parameterName, flowName, functionOrCallName));
    }

    if (basePrm.outAnnotation != null) {
      compareOutputs(
          basePrm.outAnnotation.out(),
          Preconditions.checkNotNull(globalPrm.outAnnotation).out(),
          parameterName,
          flowName,
          functionOrCallName);
    } else if (basePrm.inOutAnnotation != null) {
      compareOutputs(
          basePrm.inOutAnnotation.out(),
          Preconditions.checkNotNull(globalPrm.inOutAnnotation).out(),
          parameterName,
          flowName,
          functionOrCallName);
      Type baseRawType =
          basePrm.genericParameterType instanceof ParameterizedType
              ? ((ParameterizedType) basePrm.genericParameterType).getRawType()
              : basePrm.genericParameterType;
      Type globalRawType =
          globalPrm.genericParameterType instanceof ParameterizedType
              ? ((ParameterizedType) globalPrm.genericParameterType).getRawType()
              : globalPrm.genericParameterType;

      if (!baseRawType.equals(globalRawType)) {
        // InOutPrm should match InOutPrm, NullableInOutPrm should match NullableInOutPrm
        throw new IllegalStateException(
            String.format(
                "Call's @InOut type should match Global type (InOutPrm or NullableInOutPrm). Call Parameter type [%s]; Global Parameter type [%s]; Argument name: [%s] Flow: [%s] Function/Call: [%s]",
                baseRawType, globalRawType, parameterName, flowName, functionOrCallName));
      }
    }
  }

  List<FunctionCallParameter> createCallParameters(
      FlowTypeRecord flowTypeRecord,
      @Nullable Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      @Nullable Method stepFunctionMethod,
      @Nullable FunctionReturnValueRecord stepReturnValueRecord,
      @Nullable Method globalFunctionMethod,
      List<FunctionParameterRecord> baseFunctionParameters,
      StateAccessConfig flowStateAccess,
      @Nullable StateAccessConfig eventProfileStateAccess,
      @Nullable List<FunctionParameterRecord> callFunctionParameterOverrides,
      @Nullable List<TransitParameterOverrideRecord> transitParameterOverrides,
      @Nullable Type genericInRetType,
      List<InternalTransition> stepRefPrms,
      List<GlobalFunctionAssumedType> assumedReturnTypes) {
    String flowName = flowTypeRecord.flowTypeName;
    final Map<String, FunctionParameterRecord> baseParametersMap =
        baseFunctionParameters.stream().collect(Collectors.toMap(p -> p.name, p -> p));

    final Map<String, FunctionParameterRecord> callOverrideMap;
    if (callFunctionParameterOverrides != null) {
      callOverrideMap =
          callFunctionParameterOverrides.stream().collect(Collectors.toMap(p -> p.name, p -> p));
      callOverrideMap
          .values()
          .forEach(
              prm -> {
                String parameterName = prm.name;
                if (!baseParametersMap.containsKey(parameterName))
                  throw new IllegalStateException(
                      String.format(
                          "Function argument references nonexistent global function argument. Argument name: [%s] Flow: [%s] Function/Call: [%s]",
                          parameterName, flowName, functionOrCallName));
              });
      baseParametersMap
          .values()
          .forEach(
              parameter -> {
                String parameterName = parameter.name;
                if (!callOverrideMap.containsKey(parameterName))
                  throw new IllegalStateException(
                      String.format(
                          "Global function argument must be referenced in a call. Global function argument name: [%s] Flow: [%s] Function/Call: [%s]",
                          parameterName, flowName, functionOrCallName));

                FunctionParameterRecord globalPrm = callOverrideMap.get(parameterName);
                compareCallAndGlobalParameters(parameter, globalPrm, flowName, functionOrCallName);
              });
    } else {
      callOverrideMap = ImmutableMap.of();
    }

    final Map<String, TransitParameterOverrideRecord> transitOverrideMap;
    if (transitParameterOverrides != null) {
      transitOverrideMap =
          transitParameterOverrides.stream().collect(Collectors.toMap(p -> p.paramName, p -> p));
      transitOverrideMap
          .values()
          .forEach(
              prm -> {
                String parameterName = prm.paramName;
                if (!baseParametersMap.containsKey(prm.paramName))
                  throw new IllegalStateException(
                      String.format(
                          "Function parameter overrides nonexistent transit parameter. Parameter name: [%s] Flow: [%s] Function/Call: [%s]",
                          parameterName, flowName, functionOrCallName));
              });
    } else {
      transitOverrideMap = ImmutableMap.of();
    }

    List<GlobalFunctionAssumedType> assumedTypes = new ArrayList<>();
    List<FunctionCallParameter> parameters = new ArrayList<>();
    for (FunctionParameterRecord functionParameter : baseFunctionParameters) {
      String parameterName = functionParameter.name;

      ParameterCreationResult parameterCreationResult =
          functionCallParameterCreator.createFunctionCallParameter(
              flowTypeRecord,
              functionFlowType,
              functionOrCallMethod,
              functionOrCallName,
              stepFunctionMethod,
              stepReturnValueRecord,
              globalFunctionMethod,
              functionParameter,
              flowStateAccess,
              eventProfileStateAccess,
              callOverrideMap.get(parameterName),
              transitOverrideMap.get(parameterName),
              genericInRetType,
              stepRefPrms);

      parameters.add(parameterCreationResult.parameter);
      assumedTypes.addAll(parameterCreationResult.assumedTypes);
    }

    assumedTypes.addAll(assumedReturnTypes);
    assumedTypes.removeIf(Objects::isNull);
    Map<String, List<GlobalFunctionAssumedType>> assumedTypeMap =
        assumedTypes.stream().collect(Collectors.groupingBy(t -> t.typeVariableName));
    for (Map.Entry<String, List<GlobalFunctionAssumedType>> entry : assumedTypeMap.entrySet()) {
      List<GlobalFunctionAssumedType> assumedTypesEntryList = entry.getValue();
      GlobalFunctionAssumedType sample = assumedTypesEntryList.get(0);
      for (GlobalFunctionAssumedType assumedType : assumedTypesEntryList) {
        if (!sample.assumedType.equals(assumedType.assumedType)) {
          throw new IllegalStateException(
              String.format(
                  "Global function parameter assumes conflicting types. Type Variable name: [%s] Flow: [%s] Function/Call: [%s] Type1 [%s] Type2 [%s]",
                  entry.getKey(),
                  flowName,
                  functionOrCallName,
                  sample.assumedType,
                  assumedType.assumedType));
        }
      }
    }

    return parameters;
  }
}
