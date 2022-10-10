package com.flower.engine.runner.parameters;

import com.flower.conf.ReturnValueOrException;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.parameters.comparison.TypeComparator;
import com.flower.engine.runner.parameters.comparison.context.GenericComparisonContext;
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

public class InRetOrExceptionParameterCreator extends ParameterCreator {
  InRetOrExceptionParameterCreator() {}

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

    Preconditions.checkNotNull(baseParameter.inRetOrExceptionAnnotation);
    Type parameterType = getInnerType(baseParameter.genericParameterType);

    functionParameterType = ParameterType.TRANSIT_IN_RET_OR_EXCEPTION;

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
              "@InRetOrException",
              parameterType,
              genericReturnType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              "ReturnValueOrException"));

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
    final String parameterName = baseParameter.name;

    final ParameterType functionParameterType = ParameterType.TRANSIT_IN_RET_OR_EXCEPTION;

    Preconditions.checkNotNull(baseParameter.inRetOrExceptionAnnotation);

    Type parameterType = getInnerType(parameterOverrideFromCall.genericParameterType);

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
    if (!TypeComparator.isTypeAssignable1(genericReturnType, parameterType, fieldCallContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              "@InRetOrException",
              parameterType,
              genericReturnType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              "ReturnValueOrException"));

    // call -> global
    GenericComparisonContext callGlobalContext =
        new InCallFunctionArgumentToGlobalFunctionContext(
            flowTypeRecord.genericParameters,
            functionOrCallMethod,
            Preconditions.checkNotNull(functionFlowType),
            globalFunctionMethod);
    if (!(((ParameterizedType) baseParameter.genericParameterType)
            .getRawType()
            .equals(ReturnValueOrException.class))
        || !TypeComparator.isTypeAssignable1(
            parameterType, getInnerType(baseParameter.genericParameterType), callGlobalContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              "@InRetOrException",
              parameterType,
              genericReturnType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              "ReturnValueOrException"));

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
          StateAccessConfig stateAccess, // NOT USED
          @Nullable FunctionParameterRecord parameterOverrideFromCall,
          @Nullable TransitParameterOverrideRecord transitParameterOverride,
          @Nullable Type genericReturnType,
          List<InternalTransition> stepRefPrms // NOT USED
      ) {
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
      throw new IllegalStateException(
          String.format(
              "Function parameter of type [%s] can't be overridden by transit parameter. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
              ParameterType.TRANSIT_IN_RET_OR_EXCEPTION,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              baseParameter.name));
    }
  }
}
