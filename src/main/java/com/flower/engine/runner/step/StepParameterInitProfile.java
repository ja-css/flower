package com.flower.engine.runner.step;

import com.flower.anno.params.common.Output;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.runner.callfunc.FunctionCallContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class StepParameterInitProfile {
  public final Set<String> fieldsInitializedByStep;
  public final Set<ExpectedInitializedField> stepExpectsTheFollowingFieldsInitialized;

  public StepParameterInitProfile(FunctionCallContext stepAndTransitFunctionCall) {
    fieldsInitializedByStep = new HashSet<>();
    stepExpectsTheFollowingFieldsInitialized = new HashSet<>();

    // 1. Fields that need to be initialized in SimpleStepFunction
    stepExpectsTheFollowingFieldsInitialized.addAll(
        getFieldsFunctionExpectsToBeInitialized(ImmutableSet.of(), stepAndTransitFunctionCall));

    // 2. Fields initialized by SimpleStepFunction
    fieldsInitializedByStep.addAll(getFieldsInitializedByFunction(stepAndTransitFunctionCall));
  }

  public StepParameterInitProfile(
      FunctionCallContext stepFunctionCall,
      FunctionCallContext transitFunctionCall,
      @Nullable String stepReturnValueToFlowParameterName) {
    fieldsInitializedByStep = new HashSet<>();
    stepExpectsTheFollowingFieldsInitialized = new HashSet<>();

    boolean isTransitExceptionHandler = isTransitExceptionHandler(transitFunctionCall);
    if (!isTransitExceptionHandler) {
      // 1. Fields that need to be initialized in StepFunction
      stepExpectsTheFollowingFieldsInitialized.addAll(
          getFieldsFunctionExpectsToBeInitialized(ImmutableSet.of(), stepFunctionCall));

      // 2. Fields initialized by StepFunction
      fieldsInitializedByStep.addAll(getFieldsInitializedByFunction(stepFunctionCall));

      // 3. Field initialized by "returnTo" of StepFunction
      if (stepFunctionCall.function.getAnnotation(Nullable.class) == null
          && stepReturnValueToFlowParameterName != null) {
        fieldsInitializedByStep.add(stepReturnValueToFlowParameterName);
      }

      // 4. Fields that need to be initialized in TransitFunction. The Fields pre-initialized by
      // StepFunction are considered initialized as well.
      stepExpectsTheFollowingFieldsInitialized.addAll(
          getFieldsFunctionExpectsToBeInitialized(fieldsInitializedByStep, transitFunctionCall));

      // 5. Fields initialized by TransitFunction
      fieldsInitializedByStep.addAll(getFieldsInitializedByFunction(transitFunctionCall));
    } else {
      // 1. Fields that need to be initialized in StepFunction
      stepExpectsTheFollowingFieldsInitialized.addAll(
          getFieldsFunctionExpectsToBeInitialized(ImmutableSet.of(), stepFunctionCall));

      // In this case Fields initialized by StepFunction or by "returnTo" of StepFunction are
      // ignored,
      // because of possible Exception in StepFunction that will be caught by TransitFunction.

      // 2. Fields initialized by TransitFunction
      fieldsInitializedByStep.addAll(getFieldsInitializedByFunction(transitFunctionCall));

      // 3. Fields that need to be initialized in TransitFunction
      stepExpectsTheFollowingFieldsInitialized.addAll(
          getFieldsFunctionExpectsToBeInitialized(ImmutableSet.of(), transitFunctionCall));
    }
  }

  Set<String> getFieldsInitializedByFunction(FunctionCallContext functionCall) {
    Set<String> initializedFields = new HashSet<>();
    for (FunctionCallParameter param : functionCall.functionParameters) {
      ParameterType paramType = param.getFunctionParameterType();
      if (paramType.equals(ParameterType.OUT)) {
        if (Preconditions.checkNotNull(param.getOutput()).equals(Output.MANDATORY)) {
          initializedFields.add(param.getStateFieldName());
        }
      } else if (paramType.equals(ParameterType.IN_OUT)) {
        if (Preconditions.checkNotNull(param.getOutput()).equals(Output.MANDATORY) || (param.isCheckNotNull())) {
          initializedFields.add(param.getStateFieldName());
        }
      } else if (paramType.equals(ParameterType.IN)) {
        if (param.isCheckNotNull()) {
          initializedFields.add(param.getStateFieldName());
        }
      }
    }
    return initializedFields;
  }

  Set<ExpectedInitializedField> getFieldsFunctionExpectsToBeInitialized(
      Set<String> preInitializedFields, FunctionCallContext functionCall) {
    Set<ExpectedInitializedField> expectedFields = new HashSet<>();
    for (FunctionCallParameter param : functionCall.functionParameters) {
      ParameterType paramType = param.getFunctionParameterType();
      if (paramType.equals(ParameterType.IN) || paramType.equals(ParameterType.IN_OUT)) {
        if (!param.isNullableParameter() && !param.isCheckNotNull()) {
          String filedName = Preconditions.checkNotNull(param.getStateFieldName());
          if (!preInitializedFields.contains(filedName)) {
            expectedFields.add(new ExpectedInitializedField(filedName, functionCall.functionName));
          }
        }
      }
    }

    return expectedFields;
  }

  boolean isTransitExceptionHandler(FunctionCallContext transitFunctionCall) {
    for (FunctionCallParameter param : transitFunctionCall.functionParameters) {
      if (param.getFunctionParameterType().equals(ParameterType.TRANSIT_IN_RET_OR_EXCEPTION)) {
        return true;
      }
    }
    return false;
  }
}
