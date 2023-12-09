package com.flower.engine.runner.parameters;

import com.flower.conf.NullableInOutPrm;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class InOutParameterCreator extends ParameterCreator {
  static final InParameterCreator IN_PARAMETER_CREATOR = new InParameterCreator();
  static final OutParameterCreator OUT_PARAMETER_CREATOR = new OutParameterCreator();

  ParameterCreationResult mergeParameterCreationResult(
      ParameterCreationResult inResult,
      ParameterCreationResult outResult,
      FunctionParameterRecord baseParameter) {
    String stateFieldName = inResult.parameter.getStateFieldName();
    String functionParameterName = inResult.parameter.getFunctionParameterName();
    ParameterType functionParameterType = inResult.parameter.getFunctionParameterType();

    assert (stateFieldName.equals(outResult.parameter.getStateFieldName()));
    assert (functionParameterName.equals(outResult.parameter.getFunctionParameterName()));
    assert (functionParameterType.equals(outResult.parameter.getFunctionParameterType()));

    // TODO: is it implemented?
    // TODO: implement isNullableParameter
    Type rawType =
        baseParameter.genericParameterType instanceof ParameterizedType
            ? ((ParameterizedType) baseParameter.genericParameterType).getRawType()
            : baseParameter.genericParameterType;
    boolean isNullableParameter = rawType.equals(NullableInOutPrm.class);
    boolean checkNotNull = inResult.parameter.isCheckNotNull();

    return new ParameterCreationResult(
        new FunctionCallParameter(
            stateFieldName,
            outResult.parameter.getOutput(),
            functionParameterName,
            functionParameterType,
            inResult.parameter.getParameterType(),
            // out parameters don't have special objects
            inResult.parameter.getSpecialObject(),
            isNullableParameter,
            checkNotNull),
        Stream.concat(inResult.assumedTypes.stream(), outResult.assumedTypes.stream())
            .collect(Collectors.toList()));
  }

  ParameterCreationResult fromBaseParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      String functionOrCallName,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess) {
    ParameterCreationResult inResult =
        IN_PARAMETER_CREATOR.fromBaseParameter(
            flowTypeRecord,
            functionFlowType,
            functionOrCallName,
            globalFunctionMethod,
            baseParameter,
            stateAccess);
    ParameterCreationResult outResult =
        OUT_PARAMETER_CREATOR.fromBaseParameter(
            flowTypeRecord,
            functionFlowType,
            functionOrCallName,
            globalFunctionMethod,
            baseParameter,
            stateAccess);

    return mergeParameterCreationResult(inResult, outResult, baseParameter);
  }

  ParameterCreationResult fromCallParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      Method functionOrCallMethod,
      String functionOrCallName,
      Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess,
      FunctionParameterRecord parameterOverrideFromCall,
      @Nullable FunctionReturnValueRecord stepFunctionReturnValue) {
    ParameterCreationResult inResult =
        IN_PARAMETER_CREATOR.fromCallParameter(
            flowTypeRecord,
            functionFlowType,
            functionOrCallMethod,
            functionOrCallName,
            globalFunctionMethod,
            baseParameter,
            stateAccess,
            parameterOverrideFromCall,
            stepFunctionReturnValue);
    ParameterCreationResult outResult =
        OUT_PARAMETER_CREATOR.fromCallParameter(
            flowTypeRecord,
            functionFlowType,
            functionOrCallMethod,
            functionOrCallName,
            globalFunctionMethod,
            baseParameter,
            stateAccess,
            parameterOverrideFromCall);

    return mergeParameterCreationResult(inResult, outResult, baseParameter);
  }

  ParameterCreationResult fromTransitOverrideParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      @Nullable Method stepFunctionMethodForTransitionerReference,
      @Nullable FunctionReturnValueRecord stepFunctionReturnValue,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess,
      @Nullable FunctionParameterRecord parameterOverrideFromCall,
      @Nullable TransitParameterOverrideRecord transitParameterOverride) {
    ParameterCreationResult inResult =
        IN_PARAMETER_CREATOR.fromTransitOverrideParameter(
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
            Preconditions.checkNotNull(transitParameterOverride));
    ParameterCreationResult outResult =
        OUT_PARAMETER_CREATOR.fromTransitOverrideParameter(
            flowTypeRecord,
            functionFlowType,
            functionOrCallMethod,
            functionOrCallName,
            stepFunctionMethodForTransitionerReference,
            globalFunctionMethod,
            baseParameter,
            stateAccess,
            parameterOverrideFromCall,
            transitParameterOverride);

    return mergeParameterCreationResult(inResult, outResult, baseParameter);
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
          @Nullable Type genericInRetType, // NOT USED
          List<InternalTransition> stepRefPrms // NOT USED
      ) {
    if (parameterOverrideFromCall == null && transitParameterOverride == null) {
      return fromBaseParameter(
          flowTypeRecord,
          Preconditions.checkNotNull(functionFlowType),
          functionOrCallName,
          globalFunctionMethod,
          baseParameter,
          stateAccess);
    } else if (parameterOverrideFromCall != null && transitParameterOverride == null) {
      return fromCallParameter(
          flowTypeRecord,
          Preconditions.checkNotNull(functionFlowType),
          Preconditions.checkNotNull(functionOrCallMethod),
          functionOrCallName,
          Preconditions.checkNotNull(globalFunctionMethod),
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          stepFunctionReturnValue);
    } else { // if (transitParameterOverride != null) {
      return fromTransitOverrideParameter(
          flowTypeRecord,
          Preconditions.checkNotNull(functionFlowType),
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
  }
}
