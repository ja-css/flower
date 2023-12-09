package com.flower.engine.runner.parameters;

import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.common.Output;
import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.function.ParameterType;
import com.flower.engine.function.StateField;
import com.flower.engine.runner.parameters.comparison.TypeComparator;
import com.flower.engine.runner.parameters.comparison.context.GenericComparisonContext;
import com.flower.engine.runner.parameters.comparison.context.GlobalFunctionAssumedType;
import com.flower.engine.runner.parameters.comparison.context.OutFlowFunctionToFlowFieldContext;
import com.flower.engine.runner.parameters.comparison.context.OutGlobalFunctionToCallFunctionArgumentContext;
import com.flower.engine.runner.parameters.comparison.context.OutStepsGlobalTransitionerToFlowFieldContext;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class OutParameterCreator extends ParameterCreator {
  OutParameterCreator() {}

  ParameterCreationResult fromBaseParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      String functionOrCallName,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess) {
    final String flowName = flowTypeRecord.flowTypeName;
    final String parameterName = baseParameter.name;
    final ParameterType functionParameterType;
    final String flowStateFieldName, typeStr;

    Output output;
    if (baseParameter.outAnnotation != null) {
      Out outAnnotation = baseParameter.outAnnotation;
      output = outAnnotation.out();
      functionParameterType = ParameterType.OUT;
      flowStateFieldName = StringUtils.defaultIfEmpty(outAnnotation.to().trim(), parameterName);
      typeStr = "@Out";
    } else {
      InOut inOutAnnotation = Preconditions.checkNotNull(baseParameter.inOutAnnotation);
      output = inOutAnnotation.out();
      functionParameterType = ParameterType.IN_OUT;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(inOutAnnotation.fromAndTo().trim(), parameterName);
      typeStr = "@InOut";
    }

    StateField stateFieldRecord = stateAccess.getFieldRecord(flowStateFieldName);
    if (stateFieldRecord == null)
      throw new IllegalStateException(
          getStateFieldNotFoundMessage(
              flowName, functionOrCallName, parameterName, flowStateFieldName));

    if (stateFieldRecord.isFinal)
      throw new IllegalStateException(
          getFinalFieldNotAllowedMessage(
              typeStr, flowName, functionOrCallName, parameterName, flowStateFieldName));

    Type parameterType = getInnerType(baseParameter.genericParameterType);
    Type flowFieldType = stateFieldRecord.field.getGenericType();

    GenericComparisonContext outContext;
    if (globalFunctionMethod == null) {
      outContext =
          new OutFlowFunctionToFlowFieldContext(
              flowTypeRecord.genericParameters, stateFieldRecord.stateFieldClass, functionFlowType);
    } else {
      outContext =
          new OutStepsGlobalTransitionerToFlowFieldContext(
              flowTypeRecord.genericParameters,
              stateFieldRecord.stateFieldClass,
              Preconditions.checkNotNull(functionFlowType),
              globalFunctionMethod);
    }

    if (!TypeComparator.isTypeAssignable1(parameterType, flowFieldType, outContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              typeStr,
              parameterType,
              flowFieldType,
              flowName,
              functionOrCallName,
              parameterName,
              flowStateFieldName));
    return new ParameterCreationResult(
        new FunctionCallParameter(
            flowStateFieldName,
            output,
            parameterName,
            functionParameterType,
            parameterType,
            null,
            baseParameter.nullableAnnotation != null,
            false),
        Lists.newArrayList(outContext.getAssumedType()));
  }

  ParameterCreationResult fromCallParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      Method functionOrCallMethod,
      String functionOrCallName,
      Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess,
      FunctionParameterRecord parameterOverrideFromCall) {
    final String flowName = flowTypeRecord.flowTypeName;
    final String parameterName = baseParameter.name;
    final ParameterType functionParameterType;
    final String flowStateFieldName;
    final String typeStr;
    Output output;
    if (parameterOverrideFromCall.outAnnotation != null) {
      Out outAnnotation = parameterOverrideFromCall.outAnnotation;
      output = outAnnotation.out();
      flowStateFieldName = StringUtils.defaultIfEmpty(outAnnotation.to(), parameterName);
      functionParameterType = ParameterType.OUT;
      typeStr = "@Out";
    } else {
      InOut inOutAnnotation = Preconditions.checkNotNull(parameterOverrideFromCall.inOutAnnotation);
      output = inOutAnnotation.out();
      flowStateFieldName = StringUtils.defaultIfEmpty(inOutAnnotation.fromAndTo(), parameterName);
      functionParameterType = ParameterType.IN_OUT;
      typeStr = "@InOut";
    }

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
    if (!TypeComparator.isTypeAssignable1(
        getInnerType(parameterOverrideFromCall.genericParameterType),
        flowFieldType,
        fieldCallContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              typeStr,
              getInnerType(parameterOverrideFromCall.genericParameterType),
              flowFieldType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              flowStateFieldName));

    // call <- global
    GenericComparisonContext callGlobalContext =
        new OutGlobalFunctionToCallFunctionArgumentContext(
            flowTypeRecord.genericParameters,
            functionOrCallMethod,
            Preconditions.checkNotNull(functionFlowType),
            globalFunctionMethod);
    if (!TypeComparator.isTypeAssignable1(
        baseParameter.genericParameterType,
        parameterOverrideFromCall.genericParameterType,
        callGlobalContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              typeStr,
              parameterOverrideFromCall.genericParameterType,
              flowFieldType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              flowStateFieldName));

    return new ParameterCreationResult(
        new FunctionCallParameter(
            flowStateFieldName,
            output,
            parameterName,
            functionParameterType,
            baseParameter.genericParameterType,
            null,
            baseParameter.nullableAnnotation != null,
            false),
        Lists.newArrayList(fieldCallContext.getAssumedType(), callGlobalContext.getAssumedType()));
  }

  ParameterCreationResult fromTransitOverrideParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      @Nullable Method stepFunctionMethodForTransitionerReference,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess,
      @Nullable FunctionParameterRecord parameterOverrideFromCall,
      @Nullable TransitParameterOverrideRecord transitParameterOverride) {
    final String flowName = flowTypeRecord.flowTypeName;
    final String parameterName = baseParameter.name;
    final String flowStateFieldName;
    final ParameterType functionParameterType;
    String typeStr;

    Preconditions.checkNotNull(transitParameterOverride);

    if (transitParameterOverride.transitOutPrmAnnotation != null) {
      functionParameterType = ParameterType.OUT;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(
              Preconditions.checkNotNull(transitParameterOverride.transitOutPrmAnnotation).to(),
              parameterName);
      typeStr = "@Out";
    } else {
      functionParameterType = ParameterType.IN_OUT;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(
              Preconditions.checkNotNull(transitParameterOverride.transitInOutPrmAnnotation)
                  .fromAndTo(),
              parameterName);
      typeStr = "@InOut";
    }

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
    List<GlobalFunctionAssumedType> assumedTypes;

    Output output;
    if (baseParameter.outAnnotation != null) {
      output = baseParameter.outAnnotation.out();
    } else {
      output = Preconditions.checkNotNull(baseParameter.inOutAnnotation).out();
    }

    if (parameterOverrideFromCall == null) {
      if (globalFunctionMethod == null) {
        GenericComparisonContext context =
            new OutFlowFunctionToFlowFieldContext(
                flowTypeRecord.genericParameters,
                stateFieldRecord.stateFieldClass,
                functionFlowType);
        // Transit function
        if (!TypeComparator.isTypeAssignable1(
            getInnerType(baseParameter.genericParameterType), flowFieldType, context))
          throw new IllegalStateException(
              getParameterValueMismatchMessage(
                  typeStr,
                  baseParameter.genericParameterType,
                  flowFieldType,
                  flowTypeRecord.flowTypeName,
                  functionOrCallName,
                  parameterName,
                  flowStateFieldName));
        assumedTypes = Lists.newArrayList(context.getAssumedType());
      } else {
        // Global function reference
        checkValidTransitOverride(
            baseParameter, transitParameterOverride, flowName, functionOrCallName);
        GenericComparisonContext context =
            new OutStepsGlobalTransitionerToFlowFieldContext(
                flowTypeRecord.genericParameters,
                stateFieldRecord.stateFieldClass,
                functionFlowType,
                Preconditions.checkNotNull(globalFunctionMethod));
        if (!TypeComparator.isTypeAssignable1(
            getInnerType(baseParameter.genericParameterType), flowFieldType, context))
          throw new IllegalStateException(
              getParameterValueMismatchMessage(
                  typeStr,
                  baseParameter.genericParameterType,
                  flowFieldType,
                  flowName,
                  functionOrCallName,
                  parameterName,
                  flowStateFieldName));
        assumedTypes = Lists.newArrayList(context.getAssumedType());
      }
    } else { // if (parameterOverrideFromCall != null) {
      checkValidTransitOverride(
          parameterOverrideFromCall, transitParameterOverride, flowName, functionOrCallName);

      // field <- call <- global

      // field <- call
      GenericComparisonContext fieldCallContext =
          new OutFlowFunctionToFlowFieldContext(
              flowTypeRecord.genericParameters, stateFieldRecord.stateFieldClass, functionFlowType);
      if (!TypeComparator.isTypeAssignable1(
          getInnerType(parameterOverrideFromCall.genericParameterType),
          flowFieldType,
          fieldCallContext))
        throw new IllegalStateException(
            getParameterValueMismatchMessage(
                typeStr,
                parameterOverrideFromCall.genericParameterType,
                flowFieldType,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                flowStateFieldName));

      // call <- global
      GenericComparisonContext callGlobalContext =
          new OutGlobalFunctionToCallFunctionArgumentContext(
              flowTypeRecord.genericParameters,
              Preconditions.checkNotNull(functionOrCallMethod),
              Preconditions.checkNotNull(functionFlowType),
              Preconditions.checkNotNull(globalFunctionMethod));
      if (!TypeComparator.isTypeAssignable1(
          baseParameter.genericParameterType,
          parameterOverrideFromCall.genericParameterType,
          callGlobalContext))
        throw new IllegalStateException(
            getParameterValueMismatchMessage(
                typeStr,
                parameterOverrideFromCall.genericParameterType,
                flowFieldType,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                flowStateFieldName));

      assumedTypes =
          Lists.newArrayList(fieldCallContext.getAssumedType(), callGlobalContext.getAssumedType());
    }

    return new ParameterCreationResult(
        new FunctionCallParameter(
            flowStateFieldName,
            output,
            parameterName,
            functionParameterType,
            baseParameter.genericParameterType,
            null,
            baseParameter.nullableAnnotation != null,
            false),
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
          parameterOverrideFromCall);
    } else { // if (transitParameterOverride != null) {
      return fromTransitOverrideParameter(
          flowTypeRecord,
          Preconditions.checkNotNull(functionFlowType),
          functionOrCallMethod,
          functionOrCallName,
          stepFunctionMethodForTransitionerReference,
          globalFunctionMethod,
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          transitParameterOverride);
    }
  }
}
