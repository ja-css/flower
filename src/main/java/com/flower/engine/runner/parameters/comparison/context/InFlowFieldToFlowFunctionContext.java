package com.flower.engine.runner.parameters.comparison.context;

import com.flower.engine.configuration.FlowGenericParametersRecord;
import com.flower.engine.runner.parameters.comparison.TypeComparator;
import com.google.common.base.Preconditions;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

/** Flow field injected into Flow function parameter */
public class InFlowFieldToFlowFunctionContext implements GenericComparisonContext {
  public final FlowGenericParametersRecord fromFlowRecord;
  public final Class<?> fromFlowFieldFlowType;

  public final Class<?> toFunctionFlowType;

  public InFlowFieldToFlowFunctionContext(
      FlowGenericParametersRecord fromFlowRecord,
      Class<?> fromFlowFieldFlowType,
      Class<?> toFunctionFlowType) {
    this.fromFlowRecord = fromFlowRecord;
    this.fromFlowFieldFlowType = fromFlowFieldFlowType;
    this.toFunctionFlowType = toFunctionFlowType;
  }

  /** get current flow's parameter record for field for field's flow type */
  Type getTopLevelFieldType(String flowFieldName) {
    return Preconditions.checkNotNull(
        fromFlowRecord.getCorrespondingType(fromFlowFieldFlowType, flowFieldName));
  }

  /** get current flow's parameter record for function for function's parameter type */
  Type getTopLevelFunctionType(String functionParameterName) {
    return Preconditions.checkNotNull(
        fromFlowRecord.getCorrespondingType(toFunctionFlowType, functionParameterName));
  }

  boolean equalsOrExtends(TypeVariable child, TypeVariable parent) {
    if (child.equals(parent)) return true;
    for (Type bound : child.getBounds()) {
      if (bound.equals(parent)) return true;
      if (bound instanceof TypeVariable) {
        if (equalsOrExtends((TypeVariable) bound, parent)) return true;
      }
    }
    return false;
  }

  @Override
  public boolean isAssignableVariable(
      TypeVariable fromFlowFieldType, TypeVariable toFunctionParameterType) {
    // Get corresponding types from the top flow level
    Type flowTypeVariableTopLevelType = getTopLevelFieldType(fromFlowFieldType.getName());
    Type functionTypeVariableTopLevelType =
        getTopLevelFunctionType((toFunctionParameterType).getName());

    if (flowTypeVariableTopLevelType.equals(functionTypeVariableTopLevelType)) {
      // 1. If both are the same type on top level, we consider it assignable
      return true;
    } else {
      // 2. Otherwise we further analyze top level types:
      if (flowTypeVariableTopLevelType instanceof TypeVariable
          && functionTypeVariableTopLevelType instanceof TypeVariable) {
        // 2.1. If the types are different TypeVariables, they're not compatible, unless from
        // extends to
        return equalsOrExtends(
            (TypeVariable) flowTypeVariableTopLevelType,
            (TypeVariable) functionTypeVariableTopLevelType);
      } else if (flowTypeVariableTopLevelType instanceof TypeVariable
          && !(functionTypeVariableTopLevelType instanceof TypeVariable)) {
        // 2.2. If the function type is materialized at the top, we compare original flow type with
        // that materialized type directly
        return isAssignableFromVariableToMaterialized(
            fromFlowFieldType, functionTypeVariableTopLevelType);
      } else if (!(flowTypeVariableTopLevelType instanceof TypeVariable)
          && functionTypeVariableTopLevelType instanceof TypeVariable) {
        // 2.3. Similarly, if the flow field type is materialized at the top, we compare the
        // materialized flow field type directly with the original function type
        return isAssignableMaterializedToVariable(
            flowTypeVariableTopLevelType, toFunctionParameterType);
      } else {
        // 2.4. Finally, if both types are materialized at the top level, we compare them directly
        // as non-variable types
        return TypeComparator.isTypeAssignable1(
            flowTypeVariableTopLevelType,
            functionTypeVariableTopLevelType,
            new InFlowFieldToFlowFunctionContext(
                fromFlowRecord, fromFlowRecord.flowType, fromFlowRecord.flowType));
      }
    }
  }

  boolean typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
      TypeVariable fromAtLeastOneBound, Type isAssignable, GenericComparisonContext context) {
    for (Type fromBound : fromAtLeastOneBound.getBounds()) {
      if (fromBound instanceof TypeVariable) {
        if (typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
            (TypeVariable) fromBound, isAssignable, context)) return true;
      } else if (TypeComparator.isTypeAssignable1(fromBound, isAssignable, context)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isAssignableFromVariableToMaterialized(
      TypeVariable fromFlowFieldType, Type toFunctionParameterType) {
    // Get corresponding flow field type from the top flow level
    Type flowTypeVariableTopLevelType = getTopLevelFieldType(fromFlowFieldType.getName());

    if (flowTypeVariableTopLevelType instanceof TypeVariable) {
      // 1. If flow field type is still TypeVariable on top flow level, we check if the function
      // type is assignable from any of it's bounds
      // If no bounds are assignable to the function type, we consider that function type can't be
      // assigned
      return typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
          (TypeVariable) flowTypeVariableTopLevelType,
          toFunctionParameterType,
          new InFlowFieldToFlowFunctionContext(
              fromFlowRecord, fromFlowRecord.flowType, toFunctionFlowType));
    } else {
      // 2. If flow field type is materialized at the top level, we compare non-variable types
      return TypeComparator.isTypeAssignable1(
          flowTypeVariableTopLevelType,
          toFunctionParameterType,
          new InFlowFieldToFlowFunctionContext(
              fromFlowRecord, fromFlowRecord.flowType, toFunctionFlowType));
    }
  }

  @Override
  public boolean isAssignableMaterializedToVariable(
      Type fromFlowFieldType, TypeVariable toFunctionParameterType) {
    // Get corresponding function type from the top flow level
    Type functionTypeVariableTopLevelType =
        getTopLevelFunctionType(toFunctionParameterType.getName());

    if (functionTypeVariableTopLevelType instanceof TypeVariable) {
      // 1. If function type is TypeVariable at the top level, we can't assign a non-variable type
      // to it.
      return false;
    } else {
      // 2. If function type is materialized at the top level, we compare non-variable types
      return TypeComparator.isTypeAssignable1(
          fromFlowFieldType,
          functionTypeVariableTopLevelType,
          new InFlowFieldToFlowFunctionContext(
              fromFlowRecord, fromFlowFieldFlowType, fromFlowRecord.flowType));
    }
  }

  // Assumed type is always null, because Global Functions are not involved in this call
  @Override
  @Nullable
  public GlobalFunctionAssumedType getAssumedType() {
    return null;
  }
}
