package com.flower.engine.runner.parameters;

import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.parameters.comparison.TypeComparator;
import com.flower.engine.runner.parameters.comparison.context.GenericComparisonContext;
import com.flower.engine.runner.parameters.comparison.context.GlobalFunctionAssumedType;
import com.flower.engine.runner.parameters.comparison.context.InCallFunctionArgumentToGlobalFunctionContext;
import com.flower.engine.runner.parameters.comparison.context.InFlowFieldToFlowFunctionContext;
import com.flower.engine.runner.parameters.comparison.context.InFlowFieldToStepsGlobalTransitionerContext;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;

public class InRetParameterCreator extends ParameterCreator {
  static final InParameterCreator IN_PARAMETER_CREATOR = new InParameterCreator();

  /** Create an error message for Parameter Type mismatch */
  String getNullableMismatchMessage(
      String annotationType,
      boolean parameterIsNullable,
      boolean functionIsNullable,
      String flowName,
      String functionOrCallName,
      String parameterName,
      String flowFieldName) {
    return String.format(
        "%s parameter Nullable mismatch: parameter isNullable [%s] function isNullable [%s]. Flow [%s] Function [%s] Parameter name [%s] Flow field name [%s]",
        annotationType,
        parameterIsNullable,
        functionIsNullable,
        flowName,
        functionOrCallName,
        parameterName,
        flowFieldName);
  }

  ParameterCreationResult fromBaseParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      String functionOrCallName,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess,
      @Nullable Method globalFunctionMethod,
      FunctionReturnValueRecord stepFunctionReturnValue) {
    final String parameterName = baseParameter.name;

    final ParameterType functionParameterType;

    Preconditions.checkNotNull(baseParameter.inRetAnnotation);
    Type parameterType = baseParameter.genericParameterType;

    functionParameterType = ParameterType.TRANSIT_IN_RET;

    Type genericReturnType = stepFunctionReturnValue.genericReturnValueType;
    if (genericReturnType instanceof ParameterizedType) {
      if (((ParameterizedType) genericReturnType).getRawType().equals(ListenableFuture.class)) {
        genericReturnType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
      }
    }

    GenericComparisonContext inRetContext;
    if (globalFunctionMethod == null) {
      inRetContext =
          new InFlowFieldToFlowFunctionContext(
              flowTypeRecord.genericParameters,
              Preconditions.checkNotNull(stepFunctionReturnValue.flowType),
              functionFlowType);

    } else {
      inRetContext =
          new InFlowFieldToStepsGlobalTransitionerContext(
              flowTypeRecord.genericParameters,
              Preconditions.checkNotNull(stepFunctionReturnValue.flowType),
              Preconditions.checkNotNull(functionFlowType),
              globalFunctionMethod);
    }

    if (!TypeComparator.isTypeAssignable1(genericReturnType, parameterType, inRetContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              "@InRet",
              parameterType,
              genericReturnType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              "ReturnValue"));

    return new ParameterCreationResult(
        new FunctionCallParameter(
            null,
            null,
            parameterName,
            functionParameterType,
            parameterType,
            null,
            baseParameter.nullableAnnotation != null),
        Lists.newArrayList(inRetContext.getAssumedType()));
  }

  ParameterCreationResult fromCallParameter(
      FlowTypeRecord flowTypeRecord,
      @Nullable Class<?> functionFlowType,
      Method functionOrCallMethod,
      String functionOrCallName,
      Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess,
      FunctionParameterRecord parameterOverrideFromCall,
      FunctionReturnValueRecord stepFunctionReturnValue) {
    if (parameterOverrideFromCall.inAnnotation != null) {
      // reuse InParameterCreator for cross-override
      return IN_PARAMETER_CREATOR.fromCallParameter(
          flowTypeRecord,
          Preconditions.checkNotNull(functionFlowType),
          functionOrCallMethod,
          functionOrCallName,
          globalFunctionMethod,
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          stepFunctionReturnValue);
    }

    final String parameterName = baseParameter.name;

    final ParameterType functionParameterType = ParameterType.TRANSIT_IN_RET;

    Preconditions.checkNotNull(parameterOverrideFromCall.inRetAnnotation);

    Type parameterType = parameterOverrideFromCall.genericParameterType;

    Type genericReturnType = stepFunctionReturnValue.genericReturnValueType;
    if (genericReturnType instanceof ParameterizedType) {
      if (((ParameterizedType) genericReturnType).getRawType().equals(ListenableFuture.class)) {
        genericReturnType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
      }
    }

    // retVal -> call -> global

    // retVal -> call
    GenericComparisonContext fieldCallContext =
        new InFlowFieldToFlowFunctionContext(
            flowTypeRecord.genericParameters,
            Preconditions.checkNotNull(stepFunctionReturnValue.flowType),
            Preconditions.checkNotNull(functionFlowType));
    if (!TypeComparator.isTypeAssignable1(
        genericReturnType, parameterOverrideFromCall.genericParameterType, fieldCallContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              "@InRet",
              parameterOverrideFromCall.genericParameterType,
              genericReturnType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              "ReturnValue"));

    // call -> global
    GenericComparisonContext callGlobalContext =
        new InCallFunctionArgumentToGlobalFunctionContext(
            flowTypeRecord.genericParameters,
            functionOrCallMethod,
            Preconditions.checkNotNull(functionFlowType),
            globalFunctionMethod);
    if (!TypeComparator.isTypeAssignable1(
        parameterOverrideFromCall.genericParameterType,
        baseParameter.genericParameterType,
        callGlobalContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              "@InRet",
              parameterOverrideFromCall.genericParameterType,
              genericReturnType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              "ReturnValue"));

    return new ParameterCreationResult(
        new FunctionCallParameter(
            null,
            null,
            parameterName,
            functionParameterType,
            parameterType,
            null,
            baseParameter.nullableAnnotation != null),
        Lists.newArrayList(fieldCallContext.getAssumedType(), callGlobalContext.getAssumedType()));
  }

  ParameterCreationResult fromTransitOverrideParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      @Nullable Method stepFunctionMethodForTransitionerReference,
      FunctionReturnValueRecord stepFunctionReturnValue,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess,
      @Nullable FunctionParameterRecord parameterOverrideFromCall,
      TransitParameterOverrideRecord transitParameterOverride) {
    if (transitParameterOverride.transitInPrmAnnotation != null) {
      // reuse InParameterCreator for cross-override
      return IN_PARAMETER_CREATOR.fromTransitOverrideParameter(
          flowTypeRecord,
          functionFlowType,
          functionOrCallMethod,
          functionOrCallName,
          stepFunctionMethodForTransitionerReference,
          stepFunctionReturnValue,
          globalFunctionMethod,
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          transitParameterOverride);
    }

    final String flowName = flowTypeRecord.flowTypeName;
    final String parameterName = baseParameter.name;

    final ParameterType functionParameterType = ParameterType.TRANSIT_IN_RET;
    final String typeStr = "@InRet";
    Type parameterType = baseParameter.genericParameterType;
    Type callParameterType =
        parameterOverrideFromCall == null ? null : parameterOverrideFromCall.genericParameterType;

    Preconditions.checkNotNull(transitParameterOverride);

    Type genericReturnType = stepFunctionReturnValue.genericReturnValueType;
    if (genericReturnType instanceof ParameterizedType) {
      if (((ParameterizedType) genericReturnType).getRawType().equals(ListenableFuture.class)) {
        genericReturnType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
      }
    }

    List<GlobalFunctionAssumedType> assumedTypes;

    if (parameterOverrideFromCall == null) {
      if (globalFunctionMethod == null) {
        GenericComparisonContext context =
            new InFlowFieldToFlowFunctionContext(
                flowTypeRecord.genericParameters,
                Preconditions.checkNotNull(stepFunctionReturnValue.flowType),
                functionFlowType);

        // Transit function
        if (!TypeComparator.isTypeAssignable1(genericReturnType, parameterType, context))
          throw new IllegalStateException(
              getParameterValueMismatchMessage(
                  typeStr,
                  baseParameter.genericParameterType,
                  genericReturnType,
                  flowTypeRecord.flowTypeName,
                  functionOrCallName,
                  parameterName,
                  "ReturnValue"));
        assumedTypes = Lists.newArrayList(context.getAssumedType());
      } else {
        // Global function reference
        checkValidTransitOverride(
            baseParameter, transitParameterOverride, flowName, functionOrCallName);

        GenericComparisonContext context =
            new InFlowFieldToStepsGlobalTransitionerContext(
                flowTypeRecord.genericParameters,
                Preconditions.checkNotNull(stepFunctionReturnValue.flowType),
                Preconditions.checkNotNull(functionFlowType),
                globalFunctionMethod);

        if (!TypeComparator.isTypeAssignable1(genericReturnType, parameterType, context))
          throw new IllegalStateException(
              getParameterValueMismatchMessage(
                  typeStr,
                  baseParameter.genericParameterType,
                  genericReturnType,
                  flowName,
                  functionOrCallName,
                  parameterName,
                  "ReturnValue"));
        assumedTypes = Lists.newArrayList(context.getAssumedType());
      }
    } else {
      checkValidTransitOverride(
          parameterOverrideFromCall, transitParameterOverride, flowName, functionOrCallName);

      // field -> call -> global

      // field -> call
      GenericComparisonContext fieldCallContext =
          new InFlowFieldToFlowFunctionContext(
              flowTypeRecord.genericParameters,
              Preconditions.checkNotNull(stepFunctionReturnValue.flowType),
              functionFlowType);
      if (!TypeComparator.isTypeAssignable1(
          genericReturnType, Preconditions.checkNotNull(callParameterType), fieldCallContext))
        throw new IllegalStateException(
            getParameterValueMismatchMessage(
                typeStr,
                parameterOverrideFromCall.genericParameterType,
                genericReturnType,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                "ReturnValue"));

      // call -> global
      GenericComparisonContext callGlobalContext =
          new InCallFunctionArgumentToGlobalFunctionContext(
              flowTypeRecord.genericParameters,
              Preconditions.checkNotNull(functionOrCallMethod),
              Preconditions.checkNotNull(functionFlowType),
              Preconditions.checkNotNull(globalFunctionMethod));
      if (!TypeComparator.isTypeAssignable1(
          parameterOverrideFromCall.genericParameterType,
          baseParameter.genericParameterType,
          callGlobalContext))
        throw new IllegalStateException(
            getParameterValueMismatchMessage(
                typeStr,
                parameterOverrideFromCall.genericParameterType,
                genericReturnType,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                "ReturnValue"));

      assumedTypes =
          Lists.newArrayList(fieldCallContext.getAssumedType(), callGlobalContext.getAssumedType());
    }

    return new ParameterCreationResult(
        new FunctionCallParameter(
            "",
            null,
            parameterName,
            functionParameterType,
            parameterType,
            null,
            baseParameter.nullableAnnotation != null),
        assumedTypes);
  }

  @Override
  public ParameterCreationResult createParameter(
          FlowTypeRecord flowTypeRecord,
          @Nullable Class<?> functionFlowType,
          @Nullable Method functionOrCallMethod,
          String functionOrCallName,
          @Nullable Method stepFunctionMethodForTransitionerReference,
          @Nullable FunctionReturnValueRecord stepFunctionReturnValue,
          @Nullable Method globalFunctionMethod,
          FunctionParameterRecord baseParameter,
          StateAccessConfig stateAccess,
          @Nullable FunctionParameterRecord parameterOverrideFromCall,
          @Nullable TransitParameterOverrideRecord transitParameterOverride,
          @Nullable Type genericReturnType,
          List<InternalTransition> stepRefPrms // NOT USED
      ) {
    boolean parameterIsNullable = baseParameter.nullableAnnotation != null;
    boolean functionReturnValueIsNullable =
        Preconditions.checkNotNull(stepFunctionReturnValue).isNullable;
    if (!parameterIsNullable && functionReturnValueIsNullable) {
      throw new IllegalStateException(
          getNullableMismatchMessage(
              "@InRet",
              parameterIsNullable,
              functionReturnValueIsNullable,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              baseParameter.name,
              "ReturnValue"));
    }

    if (parameterOverrideFromCall == null && transitParameterOverride == null) {
      return fromBaseParameter(
          flowTypeRecord,
          Preconditions.checkNotNull(functionFlowType),
          functionOrCallName,
          baseParameter,
          stateAccess,
          globalFunctionMethod,
          Preconditions.checkNotNull(stepFunctionReturnValue));
    } else if (parameterOverrideFromCall != null && transitParameterOverride == null) {
      return fromCallParameter(
          flowTypeRecord,
          functionFlowType,
          Preconditions.checkNotNull(functionOrCallMethod),
          functionOrCallName,
          Preconditions.checkNotNull(globalFunctionMethod),
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          Preconditions.checkNotNull(stepFunctionReturnValue));
    } else { // if (transitParameterOverride != null) {
      return fromTransitOverrideParameter(
          flowTypeRecord,
          Preconditions.checkNotNull(functionFlowType),
          functionOrCallMethod,
          functionOrCallName,
          stepFunctionMethodForTransitionerReference,
          Preconditions.checkNotNull(stepFunctionReturnValue),
          globalFunctionMethod,
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          Preconditions.checkNotNull(transitParameterOverride));
    }
  }
}
