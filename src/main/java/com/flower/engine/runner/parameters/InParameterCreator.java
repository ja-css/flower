package com.flower.engine.runner.parameters;

import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.events.InFromFlow;
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
import com.flower.engine.runner.parameters.comparison.context.InCallFunctionArgumentToGlobalFunctionContext;
import com.flower.engine.runner.parameters.comparison.context.InFlowFieldToFlowFunctionContext;
import com.flower.engine.runner.parameters.comparison.context.InFlowFieldToStepsGlobalTransitionerContext;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

//TODO: looks like InOut logic here is not used, due to InOutParameterCreator
public class InParameterCreator extends ParameterCreator {
  static final InRetParameterCreator IN_RET_PARAMETER_CREATOR = new InRetParameterCreator();

  ParameterCreationResult fromBaseParameter(
      FlowTypeRecord flowTypeRecord,
      Class<?> functionFlowType,
      String functionOrCallName,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord baseParameter,
      StateAccessConfig stateAccess) {
    final String parameterName = baseParameter.name;

    final String flowStateFieldName;
    final ParameterType functionParameterType;
    final boolean checkNotNull;

    Type parameterType = baseParameter.genericParameterType;

    final String typeStr;
    if (baseParameter.inAnnotation != null) {
      In inAnnotation = baseParameter.inAnnotation;
      functionParameterType = ParameterType.IN;
      flowStateFieldName = StringUtils.defaultIfEmpty(inAnnotation.from().trim(), parameterName);
      typeStr = "@In";
      checkNotNull = inAnnotation.throwIfNull();
    } else if (baseParameter.inFromFlowAnnotation != null) {
      InFromFlow inFromFlowAnnotation =
          Preconditions.checkNotNull(baseParameter.inFromFlowAnnotation);
      functionParameterType = ParameterType.IN_FROM_FLOW;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(inFromFlowAnnotation.from().trim(), parameterName);
      typeStr = "@InFromFlow";
      checkNotNull = false;
    } else {
      InOut inOutAnnotation = Preconditions.checkNotNull(baseParameter.inOutAnnotation);
      functionParameterType = ParameterType.IN_OUT;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(inOutAnnotation.fromAndTo().trim(), parameterName);
      typeStr = "@InOut";
      checkNotNull = inOutAnnotation.throwIfNull();

      parameterType = getInnerType(parameterType);
    }

    List<GlobalFunctionAssumedType> assumedTypes;
    StateField stateFieldRecord = stateAccess.getFieldRecord(flowStateFieldName);
    if (stateFieldRecord == null) {
      if (functionParameterType != ParameterType.IN_FROM_FLOW) {
        throw new IllegalStateException(
            getStateFieldNotFoundMessage(
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                flowStateFieldName));
      }

      assumedTypes = ImmutableList.of();
    } else {
      Type flowFieldType = stateFieldRecord.field.getGenericType();

      GenericComparisonContext inContext;
      if (globalFunctionMethod == null) {
        inContext =
            new InFlowFieldToFlowFunctionContext(
                flowTypeRecord.genericParameters,
                stateFieldRecord.stateFieldClass,
                functionFlowType);
      } else {
        inContext =
            new InFlowFieldToStepsGlobalTransitionerContext(
                flowTypeRecord.genericParameters,
                stateFieldRecord.stateFieldClass,
                functionFlowType,
                globalFunctionMethod);
      }

      if (!TypeComparator.isTypeAssignable1(flowFieldType, parameterType, inContext))
        throw new IllegalStateException(
            getParameterValueMismatchMessage(
                typeStr,
                parameterType,
                flowFieldType,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                flowStateFieldName));

      assumedTypes = Lists.newArrayList(inContext.getAssumedType());
    }

    return new ParameterCreationResult(
        new FunctionCallParameter(
            flowStateFieldName,
            null,
            parameterName,
            functionParameterType,
            parameterType,
            null,
            baseParameter.nullableAnnotation != null,
            checkNotNull
            ),
        assumedTypes);
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
    if (parameterOverrideFromCall.inRetAnnotation != null) {
      // reuse InParameterCreator for cross-override
      return IN_RET_PARAMETER_CREATOR.fromCallParameter(
          flowTypeRecord,
          functionFlowType,
          functionOrCallMethod,
          functionOrCallName,
          globalFunctionMethod,
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          Preconditions.checkNotNull(stepFunctionReturnValue));
    }

    final String parameterName = baseParameter.name;
    final ParameterType functionParameterType;
    final String flowStateFieldName;
    final String typeStr;
    final boolean checkNotNull;
    Type callParameterType = parameterOverrideFromCall.genericParameterType;

    if (parameterOverrideFromCall.inAnnotation != null) {
      In inOverrideAnnotation = parameterOverrideFromCall.inAnnotation;
      functionParameterType = ParameterType.IN;
      flowStateFieldName = StringUtils.defaultIfEmpty(inOverrideAnnotation.from(), parameterName);
      typeStr = "@In";
      checkNotNull = inOverrideAnnotation.throwIfNull();

    } else if (parameterOverrideFromCall.inFromFlowAnnotation != null) {
      InFromFlow inFromFlowOverrideAnnotation =
          Preconditions.checkNotNull(parameterOverrideFromCall.inFromFlowAnnotation);
      functionParameterType = ParameterType.IN_FROM_FLOW;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(inFromFlowOverrideAnnotation.from(), parameterName);
      typeStr = "@InFromFlow";

      checkNotNull = false;
    } else {
      InOut inOutOverrideAnnotation =
          Preconditions.checkNotNull(parameterOverrideFromCall.inOutAnnotation);
      functionParameterType = ParameterType.IN_OUT;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(inOutOverrideAnnotation.fromAndTo(), parameterName);
      typeStr = "@InOut";
      checkNotNull = inOutOverrideAnnotation.throwIfNull();

      callParameterType = getInnerType(callParameterType);
    }

    StateField stateFieldRecord = stateAccess.getFieldRecord(flowStateFieldName);
    if (stateFieldRecord == null)
      throw new IllegalStateException(
          getStateFieldNotFoundMessage(
              flowTypeRecord.flowTypeName, functionOrCallName, parameterName, flowStateFieldName));

    // field -> call -> global

    // field -> call
    GenericComparisonContext fieldCallContext =
        new InFlowFieldToFlowFunctionContext(
            flowTypeRecord.genericParameters, stateFieldRecord.stateFieldClass, functionFlowType);
    Type flowFieldType = stateFieldRecord.field.getGenericType();
    if (!TypeComparator.isTypeAssignable1(flowFieldType, callParameterType, fieldCallContext))
      throw new IllegalStateException(
          getParameterValueMismatchMessage(
              typeStr,
              parameterOverrideFromCall.genericParameterType,
              flowFieldType,
              flowTypeRecord.flowTypeName,
              functionOrCallName,
              parameterName,
              flowStateFieldName));

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
            null,
            parameterName,
            functionParameterType,
            callParameterType,
            null,
            baseParameter.nullableAnnotation != null,
            checkNotNull),
        Lists.newArrayList(fieldCallContext.getAssumedType(), callGlobalContext.getAssumedType()));
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
      TransitParameterOverrideRecord transitParameterOverride) {
    if (transitParameterOverride.transitInRetPrmAnnotation != null) {
      // reuse InParameterCreator for cross-override
      return IN_RET_PARAMETER_CREATOR.fromTransitOverrideParameter(
          flowTypeRecord,
          functionFlowType,
          functionOrCallMethod,
          functionOrCallName,
          stepFunctionMethodForTransitionerReference,
          Preconditions.checkNotNull(stepFunctionReturnValue),
          globalFunctionMethod,
          baseParameter,
          stateAccess,
          parameterOverrideFromCall,
          transitParameterOverride);
    }

    // problem: which flow defines transit parameter override?
    // answer: the same that defines step function

    final String flowName = flowTypeRecord.flowTypeName;
    final String parameterName = baseParameter.name;

    final String flowStateFieldName;
    final ParameterType functionParameterType;
    final String typeStr;
    final boolean checkNotNull;
    Type parameterType = baseParameter.genericParameterType;
    Type callParameterType =
        parameterOverrideFromCall == null ? null : parameterOverrideFromCall.genericParameterType;

    Preconditions.checkNotNull(transitParameterOverride);

    if (transitParameterOverride.transitInPrmAnnotation != null) {
      functionParameterType = ParameterType.IN;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(
              transitParameterOverride.transitInPrmAnnotation.from(), parameterName);
      typeStr = "@In";
      checkNotNull = transitParameterOverride.transitInPrmAnnotation.throwIfNull();
    } else {
      functionParameterType = ParameterType.IN_OUT;
      flowStateFieldName =
          StringUtils.defaultIfEmpty(
              Preconditions.checkNotNull(transitParameterOverride.transitInOutPrmAnnotation)
                  .fromAndTo(),
              parameterName);
      typeStr = "@InOut";
      checkNotNull = transitParameterOverride.transitInOutPrmAnnotation.throwIfNull();

      parameterType = getInnerType(parameterType);
      if (callParameterType != null) callParameterType = getInnerType(callParameterType);
    }

    StateField stateFieldRecord = stateAccess.getFieldRecord(flowStateFieldName);
    if (stateFieldRecord == null)
      throw new IllegalStateException(
          getStateFieldNotFoundMessage(
              flowName, functionOrCallName, parameterName, flowStateFieldName));

    Type flowFieldType = stateFieldRecord.field.getGenericType();
    List<GlobalFunctionAssumedType> assumedTypes;

    if (parameterOverrideFromCall == null) {
      if (globalFunctionMethod == null) {
        GenericComparisonContext context =
            new InFlowFieldToFlowFunctionContext(
                flowTypeRecord.genericParameters,
                stateFieldRecord.stateFieldClass,
                functionFlowType);
        // Transit function
        if (!TypeComparator.isTypeAssignable1(flowFieldType, parameterType, context))
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
            new InFlowFieldToStepsGlobalTransitionerContext(
                flowTypeRecord.genericParameters,
                stateFieldRecord.stateFieldClass,
                functionFlowType,
                Preconditions.checkNotNull(globalFunctionMethod));
        if (!TypeComparator.isTypeAssignable1(flowFieldType, parameterType, context))
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

      // field -> call -> global

      // field -> call
      GenericComparisonContext fieldCallContext =
          new InFlowFieldToFlowFunctionContext(
              flowTypeRecord.genericParameters, stateFieldRecord.stateFieldClass, functionFlowType);
      if (!TypeComparator.isTypeAssignable1(
          flowFieldType, Preconditions.checkNotNull(callParameterType), fieldCallContext))
        throw new IllegalStateException(
            getParameterValueMismatchMessage(
                typeStr,
                parameterOverrideFromCall.genericParameterType,
                flowFieldType,
                flowTypeRecord.flowTypeName,
                functionOrCallName,
                parameterName,
                flowStateFieldName));

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
            null,
            parameterName,
            functionParameterType,
            parameterType,
            null,
            baseParameter.nullableAnnotation != null,
            checkNotNull),
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
          @Nullable Type genericReturnType, // NOT USED
          List<InternalTransition> stepRefPrms, // NOT USED
          List<Pair<String, String>> flowFactories,
          List<Pair<String, String>> flowRepos
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
    } else {
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
          Preconditions.checkNotNull(transitParameterOverride));
    }
  }
}
