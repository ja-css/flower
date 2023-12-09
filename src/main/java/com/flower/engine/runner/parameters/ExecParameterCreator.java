package com.flower.engine.runner.parameters;

import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;

public class ExecParameterCreator extends ParameterCreator {
  final Executor executor;

  ExecParameterCreator(Executor executor) {
    this.executor = executor;
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
          @Nullable Type genericInRetType, // NOT USED
          List<InternalTransition> stepRefPrms // NOT USED
      ) {
    final String parameterName = baseParameter.name;

    ParameterType functionParameterType = null;
    Type parameterType = null;

    if (baseParameter.execAnnotation != null) {
      functionParameterType = ParameterType.EXEC;
      parameterType = Executor.class;
    }

    Preconditions.checkNotNull(functionParameterType);
    Preconditions.checkNotNull(parameterType);

    if (transitParameterOverride != null) {
      throw new IllegalStateException(
          String.format(
              "Function parameter of type [%s] can't be overridden by transit parameter. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
              functionParameterType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName));
    }

    validateFixedTypeParameter(
        baseParameter,
        parameterOverrideFromCall,
        null,
        parameterType,
        flowTypeRecord.flowTypeName,
        functionOrCallName);

    return new ParameterCreationResult(
        new FunctionCallParameter(
            null,
            null,
            parameterName,
            functionParameterType,
            parameterType,
            executor,
            baseParameter.nullableAnnotation != null,
            false),
        ImmutableList.of());
  }
}
