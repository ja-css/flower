package com.flower.engine.runner.parameters;

import com.flower.anno.params.step.FlowFactory;
import com.flower.anno.params.step.FlowRepo;
import com.flower.conf.FactoryOfFlowTypeFactories;
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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
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
          List<InternalTransition> stepRefPrms, // NOT USED
          List<Pair<String, String>> flowFactories,
          List<Pair<String, String>> flowRepos
      ) {
    final String parameterName = baseParameter.name;
    final ParameterType functionParameterType = ParameterType.FLOW_REPO;
    FlowRepo flowRepoAnnotation;
    final Type genericParameterType;

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

    if (parameterOverrideFromCall == null) {
      flowRepoAnnotation = Preconditions.checkNotNull(baseParameter.flowRepoAnnotation);
      genericParameterType = baseParameter.genericParameterType;
    } else {
      flowRepoAnnotation =
              Preconditions.checkNotNull(parameterOverrideFromCall.flowRepoAnnotation);
      genericParameterType = parameterOverrideFromCall.genericParameterType;
    }

    if (!(genericParameterType instanceof ParameterizedType)) {
      throw new IllegalStateException(
              String.format(
                  "Function parameter of type [%s] should be a parameterized type FlowRepoPrm<FLOW_TYPE>. Flow: [%s] Function/Call: [%s] Parameter: [%s]",
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
                    false,
                    (ParameterizedType) genericParameterType);

    Type genericParameterFlowType = ((ParameterizedType)genericParameterType).getActualTypeArguments()[0];
    flowRepos.add(Pair.of(genericParameterFlowType.getTypeName(), flowRepoAnnotation.desc()));

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
            baseParameter.genericParameterType,
            //TODO: here we return flowRunner which can return futures for all types and cause issues,
            // consider restricting returns to flows of this particular type
            specialObject,
            baseParameter.nullableAnnotation != null,
            false),
        ImmutableList.of());
  }
}
