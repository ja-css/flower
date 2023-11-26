package com.flower.engine.runner.parameters;

import com.flower.anno.params.step.FlowFactory;
import com.flower.conf.FactoryOfFlowTypeFactories;
import com.flower.conf.FlowFactoryPrm;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.FlowRunner;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class FlowTypeFactoryParameterCreator extends ParameterCreator {
  final FlowRunner flowRunner;

  FlowTypeFactoryParameterCreator(FlowRunner flowRunner) {
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
          @Nullable TransitParameterOverrideRecord transitParameterOverride,
          @Nullable Type genericInRetType, // NOT USED
          List<InternalTransition> stepRefPrms // NOT USED
      ) {
    final String parameterName = baseParameter.name;
    final ParameterType functionParameterType = ParameterType.CHILD_FLOW_FACTORY_REF;
    String childFlowName;
    Class<?> flowType;
    FlowFactory flowFactoryAnnotation;
    final Type genericParameterType;

    if (transitParameterOverride != null) {
      throw new IllegalStateException(
          String.format(
              "Function parameter of type [%s] can't be overridden by transit parameter. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
              ParameterType.CHILD_FLOW_FACTORY_REF,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName));
    }

    validateFixedTypeParameter(
        baseParameter,
        parameterOverrideFromCall,
        null,
        FlowFactoryPrm.class,
        flowTypeRecord.flowTypeName,
        functionOrCallName);

    if (parameterOverrideFromCall == null) {
      flowFactoryAnnotation = Preconditions.checkNotNull(baseParameter.flowFactoryAnnotation);
      genericParameterType = baseParameter.genericParameterType;
    } else {
      flowFactoryAnnotation =
          Preconditions.checkNotNull(parameterOverrideFromCall.flowFactoryAnnotation);
      genericParameterType = parameterOverrideFromCall.genericParameterType;
    }

    childFlowName = flowFactoryAnnotation.flowTypeName();
    flowType = flowFactoryAnnotation.flowType();
    boolean dynamic = flowFactoryAnnotation.dynamic();

    if (!dynamic) {
      if (StringUtils.isBlank(childFlowName) && flowType.equals(void.class)) {
        throw new IllegalStateException(
            String.format(
                "Function parameter of type [%s] should refer to a ChildFlow by name or type. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
                ParameterType.CHILD_FLOW_FACTORY_REF,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName));
      }
    }

    if (!(genericParameterType instanceof ParameterizedType)) {
      throw new IllegalStateException(
          String.format(
              "Function parameter of type [%s] should be a parameterized type FlowFactoryPrm<FLOW_TYPE>. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
              ParameterType.CHILD_FLOW_FACTORY_REF,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName));
    }

    FactoryOfFlowTypeFactories specialObject =
        new FactoryOfFlowTypeFactories(
            flowTypeRecord.flowTypeName,
            functionOrCallName,
            parameterName,
            flowRunner,
            childFlowName,
            flowType,
            dynamic,
            (ParameterizedType) genericParameterType);

    // We can't ensure that FLOW_TYPE used in a parameter FlowFactoryPrm<FLOW_TYPE> is the right
    // type at this point,
    // but we register the Factory here to perform this validation on Flower initialization
    // (see FactoryOfFlowTypeFactories.initFlowExec(...))
    flowRunner.registerFactoryOfFlowTypeFactories(specialObject);

    return new ParameterCreationResult(
        new FunctionCallParameter(
            null,
            null,
            parameterName,
            functionParameterType,
            genericParameterType,
            specialObject,
            baseParameter.nullableAnnotation != null),
        ImmutableList.of());
  }
}
