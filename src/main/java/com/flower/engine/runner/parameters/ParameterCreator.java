package com.flower.engine.runner.parameters;

import com.flower.engine.configuration.FlowTypeRecord;
import com.flower.engine.configuration.FunctionParameterRecord;
import com.flower.engine.configuration.FunctionParameterType;
import com.flower.engine.configuration.FunctionReturnValueRecord;
import com.flower.engine.configuration.TransitParameterOverrideRecord;
import com.flower.engine.runner.state.StateAccessConfig;
import com.flower.engine.runner.step.InternalTransition;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Nullable;

public abstract class ParameterCreator {
  String getStateFieldNotFoundMessage(
      String flowName, String functionOrCallName, String parameterName, String flowStateFieldName) {
    return String.format(
        "State field doesn't exist: Flow [%s] Function [%s] ParameterName [%s] State field name [%s] (Note: @State annotation must be present on a field)",
        flowName, functionOrCallName, parameterName, flowStateFieldName);
  }

  /** Create an error message for Parameter Type mismatch */
  String getParameterValueMismatchMessage(
      String annotationType,
      Type parameterType,
      Type flowFieldType,
      String flowName,
      String functionOrCallName,
      String parameterName,
      String flowFieldName) {
    return String.format(
        "%s parameter type mismatch: parameter type [%s] flow field type [%s]. Flow [%s] Function [%s] Parameter name [%s] Flow field name [%s]",
        annotationType,
        parameterType,
        flowFieldType,
        flowName,
        functionOrCallName,
        parameterName,
        flowFieldName);
  }

  /** Create an error message that a final field is not allowed */
  String getFinalFieldNotAllowedMessage(
      String annotationType,
      String flowName,
      String functionOrCallName,
      String parameterName,
      String flowFieldName) {
    return String.format(
        "%s parameter can't refer to final flow state field: Flow [%s] Function [%s] Parameter name [%s] Flow field name [%s]",
        annotationType, flowName, functionOrCallName, parameterName, flowFieldName);
  }

  /**
   * True, if type of newGenericParameterType is incompatible with oldGenericParameterType
   * (oldGenericParameterType is assignable from newGenericParameterType).
   */
  /*boolean isNewTypeCompatible(Type oldGenericParameterType, Type newGenericParameterType) {
    return ParametrizedTypeComparisonUtil.isTypeAssignable(
        oldGenericParameterType, newGenericParameterType);
  }*/

  /**
   * Get first generic &lt;T&gt; type for a generic class. Use case - get inner type for
   * OutPrm&lt;T&gt;, InOutPrm&lt;T&gt;
   */
  Type getInnerType(Type wrapperType) {
    return wrapperType instanceof ParameterizedType
        ? ((ParameterizedType) wrapperType).getActualTypeArguments()[0]
        : Object.class;
  }

  /** True if only one of "o1" or "o2" is present, but not neither and not both. */
  void checkOneOf(
      @Nullable Object o1,
      @Nullable Object o2,
      String flowName,
      String functionName,
      String parameterName,
      String arg1,
      String arg2) {
    if (o1 != null && o2 != null) {
      throw new IllegalStateException(
          String.format(
              "Parameter can't be annotated with both %s or %s. Flow [%s] Function [%s] ParameterName [%s]",
              arg1, arg2, flowName, functionName, parameterName));
    }

    if (o1 == null && o2 == null) {
      throw new IllegalStateException(
          String.format(
              "Parameter must be overridden with %s or %s. Flow [%s] Function [%s] ParameterName [%s]",
              arg1, arg2, flowName, functionName, parameterName));
    }
  }

  void validateFixedTypeParameter(
      FunctionParameterRecord baseParameter,
      @Nullable FunctionParameterRecord parameterOverrideFromCall,
      @Nullable TransitParameterOverrideRecord transitParameterOverride,
      Type parameterType,
      String flowName,
      String functionOrCallName) {
    Type parameterTypeFromGenericType = baseParameter.genericParameterType;
    if (baseParameter.genericParameterType instanceof ParameterizedType)
      parameterTypeFromGenericType =
          ((ParameterizedType) baseParameter.genericParameterType).getRawType();

    if (!parameterTypeFromGenericType.equals(parameterType)) {
      throw new IllegalStateException(
          String.format(
              "Function parameter type is incompatible. Expected type: [%s] Parameter name: [%s] Flower Type [%s] type [%s] Flow: [%s] Function/Call: [%s]",
              parameterType,
              baseParameter.name,
              baseParameter.type,
              baseParameter.genericParameterType,
              flowName,
              functionOrCallName));
    }

    if (parameterOverrideFromCall != null) {
      if (!baseParameter.name.equals(parameterOverrideFromCall.name)) {
        throw new IllegalStateException(
            String.format(
                "Function override parameter name mismatch. Base parameter name: [%s] Call parameter name: [%s] Flow: [%s] Function/Call: [%s]",
                baseParameter.name, parameterOverrideFromCall.name, flowName, functionOrCallName));
      }

      if (!FunctionParameterType.isCompatible(baseParameter.type, parameterOverrideFromCall.type)) {
        throw new IllegalStateException(
            String.format(
                "Function override parameter Flower Type is incompatible. Base parameter name: [%s] type [%s] Call parameter name: [%s] type [%s] Flow: [%s] Function/Call: [%s]",
                baseParameter.name,
                baseParameter.type,
                parameterOverrideFromCall.name,
                parameterOverrideFromCall.type,
                flowName,
                functionOrCallName));
      }

      if (!parameterOverrideFromCall.genericParameterType.equals(parameterType)) {
        throw new IllegalStateException(
            String.format(
                "Function override parameter types are incompatible. Expected type: [%s] Base parameter name: [%s] type [%s] Call parameter name [%s] type [%s] Flow: [%s] Function/Call: [%s]",
                parameterType,
                baseParameter.name,
                baseParameter.genericParameterType,
                parameterOverrideFromCall.name,
                parameterOverrideFromCall.genericParameterType,
                flowName,
                functionOrCallName));
      }
    }

    if (transitParameterOverride != null) {
      if (!baseParameter.name.equals(transitParameterOverride.paramName)) {
        throw new IllegalStateException(
            String.format(
                "Function transit override parameter name mismatch. Base parameter name: [%s] Transit parameter name: [%s] Flow: [%s] Function/Call: [%s]",
                baseParameter.name,
                transitParameterOverride.paramName,
                flowName,
                functionOrCallName));
      }

      if (!FunctionParameterType.isCompatible(
          baseParameter.type, transitParameterOverride.type.functionParameterType())) {
        throw new IllegalStateException(
            String.format(
                "Function transit override parameter Flower Type is incompatible. Base parameter name: [%s] type [%s] Transit parameter name: [%s] type [%s] Flow: [%s] Function/Call: [%s]",
                baseParameter.name,
                baseParameter.type,
                transitParameterOverride.paramName,
                transitParameterOverride.type,
                flowName,
                functionOrCallName));
      }
    }
  }

  void checkValidTransitOverride(
      FunctionParameterRecord originalPrm,
      TransitParameterOverrideRecord overridingPrm,
      String flowName,
      String functionOrCallName) {
    if (!originalPrm.name.equals(overridingPrm.paramName)) {
      throw new IllegalStateException(
          String.format(
              "Function transit override parameter name mismatch. Parameter name 1: [%s] Parameter name 2: [%s] Flow: [%s] Function/Call: [%s]",
              originalPrm.name, overridingPrm.paramName, flowName, functionOrCallName));
    }

    if (!FunctionParameterType.isCompatible(
        originalPrm.type, overridingPrm.type.functionParameterType())) {
      throw new IllegalStateException(
          String.format(
              "Function transit override parameter Flower Types is incompatible. Parameter name 1: [%s] type 1 [%s] Parameter name 2: [%s] type 2 [%s] Flow: [%s] Function/Call: [%s]",
              originalPrm.name,
              originalPrm.type,
              overridingPrm.paramName,
              overridingPrm.type,
              flowName,
              functionOrCallName));
    }
  }

  /** Throws if newPrm is incompatible with oldPrm (name, FlowerType, java type) */
  /*  void checkValidOverride(
      FunctionParameterRecord originalPrm,
      FunctionParameterRecord overridingPrm,
      String flowName,
      String functionOrCallName) {
    if (!originalPrm.name.equals(overridingPrm.name)) {
      throw new IllegalStateException(
          String.format(
              "Function override parameter name mismatch. Parameter name 1: [%s] Parameter name 2: [%s] Flow: [%s] Function/Call: [%s]",
              originalPrm.name, overridingPrm.name, flowName, functionOrCallName));
    }

    if (!FunctionParameterType.isCompatible(originalPrm.type, overridingPrm.type)) {
      throw new IllegalStateException(
          String.format(
              "Function override parameter Flower Type is incompatible. Parameter name 1: [%s] type 1 [%s] Parameter name 2: [%s] type 2 [%s] Flow: [%s] Function/Call: [%s]",
              originalPrm.name, originalPrm.type, overridingPrm.name, overridingPrm.type, flowName, functionOrCallName));
    }

    if (!isNewTypeCompatible(originalPrm.genericParameterType, overridingPrm.genericParameterType)) {
      throw new IllegalStateException(
          String.format(
              "Function override parameter types are incompatible. Parameter name 1: [%s] type 1 [%s] Parameter name 2: [%s] type 2 [%s] Flow: [%s] Function/Call: [%s]",
              originalPrm.name,
              originalPrm.genericParameterType,
              overridingPrm.name,
              overridingPrm.genericParameterType,
              flowName,
              functionOrCallName));
    }
  }*/

  public abstract ParameterCreationResult createParameter(
      FlowTypeRecord flowTypeRecord,
      @Nullable Class<?> functionFlowType,
      @Nullable Method functionOrCallMethod,
      String functionOrCallName,
      @Nullable Method stepFunctionMethodForTransitionerReference,
      @Nullable FunctionReturnValueRecord stepFunctionReturnValue,
      @Nullable Method globalFunctionMethod,
      FunctionParameterRecord functionParameter,
      StateAccessConfig stateAccess,
      @Nullable FunctionParameterRecord parameterOverrideFromCall,
      @Nullable TransitParameterOverrideRecord transitParameterOverride,
      @Nullable Type genericInRetType,
      List<InternalTransition> stepRefPrms,
      List<Pair<String, String>> flowFactories,
      List<Pair<String, String>> flowRepos);
}
