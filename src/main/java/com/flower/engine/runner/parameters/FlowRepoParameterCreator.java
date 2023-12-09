package com.flower.engine.runner.parameters;

import com.flower.conf.FlowRepoPrm;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.FlowRunner;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;

public class FlowRepoParameterCreator extends ParameterCreator {
  final FlowRunner flowRunner;

  FlowRepoParameterCreator(FlowRunner flowRunner) {
    this.flowRunner = flowRunner;
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
          @Nullable TransitParameterOverrideRecord transitParameterOverride, // NOT USED
          @Nullable Type genericInRetType, // NOT USED
          List<InternalTransition> stepRefPrms // NOT USED
      ) {
    final String parameterName = baseParameter.name;
    final ParameterType functionParameterType = ParameterType.FLOW_REPO;

    if (transitParameterOverride != null) {
      throw new IllegalStateException(
          String.format(
              "Function parameter of type [%s] can't be overridden by transit parameter. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
              ParameterType.FLOW_REPO,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName));
    }

    validateFixedTypeParameter(
        baseParameter,
        parameterOverrideFromCall,
        null,
        FlowRepoPrm.class,
        flowTypeRecord.flowTypeName,
        functionOrCallName);

    return new ParameterCreationResult(
        new FunctionCallParameter(
            null,
            null,
            parameterName,
            functionParameterType,
            baseParameter.genericParameterType,
            flowRunner,
            baseParameter.nullableAnnotation != null,
            false),
        ImmutableList.of());
  }
}
