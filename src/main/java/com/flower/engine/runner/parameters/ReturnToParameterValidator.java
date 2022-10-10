package com.flower.engine.runner.parameters;

import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.ParameterType;
import com.flower.engine.function.StateField;
import com.flower.engine.runner.parameters.comparison.TypeComparator;
import com.flower.engine.runner.parameters.comparison.context.GenericComparisonContext;
import com.flower.engine.runner.parameters.comparison.context.GlobalFunctionAssumedType;
import com.flower.engine.runner.parameters.comparison.context.OutFlowFunctionToFlowFieldContext;
import com.flower.engine.runner.parameters.comparison.context.OutGlobalFunctionToCallFunctionArgumentContext;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class ReturnToParameterValidator extends ParameterCreator {
  public ReturnToParameterValidator() {}

  public List<GlobalFunctionAssumedType> fromBaseParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      String functionOrCallName,
      StateAccessConfig stateAccess,
      String flowStateFieldName,
      Type returnType) {
    final String flowName = flowTypeRecord.flowTypeName;
    final String parameterName = "ReturnTo";
    // TODO: remove or fix functionParameterType
    final ParameterType functionParameterType = ParameterType.OUT;
    final String typeStr = "@ReturnTo";

    StateField stateFieldRecord = stateAccess.getFieldRecord(flowStateFieldName);
    if (stateFieldRecord == null)
      throw new IllegalStateException(
          getStateFieldNotFoundMessage(
              flowName, functionOrCallName, parameterName, flowStateFieldName));

    if (stateFieldRecord.isFinal)
      throw new IllegalStateException(
          getFinalFieldNotAllowedMessage(
              typeStr, flowName, functionOrCallName, parameterName, flowStateFieldName));

    Type flowFieldType = stateFieldRecord.field.getGenericType();

    GenericComparisonContext outContext =
        new OutFlowFunctionToFlowFieldContext(
            flowTypeRecord.genericParameters, stateFieldRecord.stateFieldClass, functionFlowType);

    if (!TypeComparator.isTypeAssignable1(returnType, flowFieldType, outContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              typeStr,
              returnType,
              flowFieldType,
              flowName,
              functionOrCallName,
              parameterName,
              flowStateFieldName));
    return Lists.newArrayList(outContext.getAssumedType());
  }

  public List<GlobalFunctionAssumedType> fromCallParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      Method functionOrCallMethod,
      String functionOrCallName,
      Method globalFunctionMethod,
      StateAccessConfig stateAccess,
      @Nullable String flowStateFieldName,
      Type callReturnType,
      Type globalReturnType) {
    final String flowName = flowTypeRecord.flowTypeName;
    final String parameterName = "ReturnTo";
    final String typeStr = "@ReturnTo or return value";

    List<GlobalFunctionAssumedType> assumedTypes = new ArrayList<>();

    if (flowStateFieldName != null) {
      StateField stateFieldRecord = stateAccess.getFieldRecord(flowStateFieldName);
      if (stateFieldRecord == null)
        throw new IllegalStateException(
            getStateFieldNotFoundMessage(
                flowName, functionOrCallName, parameterName, flowStateFieldName));

      if (stateFieldRecord.isFinal)
        throw new IllegalStateException(
            getFinalFieldNotAllowedMessage(
                typeStr, flowName, functionOrCallName, parameterName, flowStateFieldName));

      // field <- call <- global

      // field <- call
      GenericComparisonContext fieldCallContext =
          new OutFlowFunctionToFlowFieldContext(
              flowTypeRecord.genericParameters, stateFieldRecord.stateFieldClass, functionFlowType);
      Type flowFieldType = stateFieldRecord.field.getGenericType();
      if (!TypeComparator.isTypeAssignable1(callReturnType, flowFieldType, fieldCallContext))
        throw new IllegalStateException(
            getParameterValueMismatchMessage(
                typeStr,
                globalReturnType,
                flowFieldType,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                flowStateFieldName));

      assumedTypes.add(fieldCallContext.getAssumedType());
    }

    // call <- global
    GenericComparisonContext callGlobalContext =
        new OutGlobalFunctionToCallFunctionArgumentContext(
            flowTypeRecord.genericParameters,
            functionOrCallMethod,
            Preconditions.checkNotNull(functionFlowType),
            globalFunctionMethod);
    if (!TypeComparator.isTypeAssignable1(globalReturnType, callReturnType, callGlobalContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              typeStr,
              globalReturnType,
              callReturnType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              flowStateFieldName == null ? "null" : flowStateFieldName));
    assumedTypes.add(callGlobalContext.getAssumedType());

    return assumedTypes;
  }

  // This method shouldn't be used
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
    throw new UnsupportedOperationException();
  }
}
