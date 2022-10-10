package com.flower.engine.runner.parameters.comparison.context;

import com.flower.engine.configuration.FlowGenericParametersRecord;
import com.flower.engine.runner.parameters.comparison.TypeComparator;
import com.google.common.base.Preconditions;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

/** Flow field injected into Flow function parameter */
public class OutFlowFunctionToFlowFieldContext implements GenericComparisonContext {
  public final FlowGenericParametersRecord toFlowRecord;
  public final Class<?> toFlowFieldFlowType;

  public final Class<?> fromFunctionFlowType;

  public OutFlowFunctionToFlowFieldContext(
      FlowGenericParametersRecord toFlowRecord,
      Class<?> toFlowFieldFlowType,
      Class<?> fromFunctionFlowType) {
    this.toFlowRecord = toFlowRecord;
    this.toFlowFieldFlowType = toFlowFieldFlowType;
    this.fromFunctionFlowType = fromFunctionFlowType;
  }

  /** get current flow's parameter record for field for field's flow type */
  Type getTopLevelFieldType(String flowFieldName) {
    return Preconditions.checkNotNull(
        toFlowRecord.getCorrespondingType(toFlowFieldFlowType, flowFieldName));
  }

  /** get current flow's parameter record for function for function's parameter type */
  Type getTopLevelFunctionType(String functionParameterName) {
    return Preconditions.checkNotNull(
        toFlowRecord.getCorrespondingType(fromFunctionFlowType, functionParameterName));
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
      TypeVariable fromFunctionParameterType, TypeVariable toFlowFieldType) {
    // Get corresponding types from the top flow level
    Type flowTypeVariableTopLevelType = getTopLevelFieldType(toFlowFieldType.getName());
    Type functionTypeVariableTopLevelType =
        getTopLevelFunctionType((fromFunctionParameterType).getName());

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
            (TypeVariable) functionTypeVariableTopLevelType,
            (TypeVariable) flowTypeVariableTopLevelType);
      } else if (!(flowTypeVariableTopLevelType instanceof TypeVariable)
          && functionTypeVariableTopLevelType instanceof TypeVariable) {
        // 2.2. If the flow field type is materialized at the top, we compare original function type
        // with that materialized type directly
        return isAssignableFromVariableToMaterialized(
            fromFunctionParameterType, flowTypeVariableTopLevelType);
      } else if (flowTypeVariableTopLevelType instanceof TypeVariable
          && !(functionTypeVariableTopLevelType instanceof TypeVariable)) {
        // 2.3. Similarly, if the function type is materialized at the top, we compare the
        // materialized function type directly with the original flow field type
        return isAssignableMaterializedToVariable(
            functionTypeVariableTopLevelType, toFlowFieldType);
      } else {
        // 2.4. Finally, if both types are materialized at the top level, we compare them directly
        // as non-variable types
        return TypeComparator.isTypeAssignable1(
            functionTypeVariableTopLevelType,
            flowTypeVariableTopLevelType,
            new OutFlowFunctionToFlowFieldContext(
                toFlowRecord, toFlowRecord.flowType, toFlowRecord.flowType));
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
      TypeVariable fromFunctionParameterType, Type toFlowFieldType) {
    // Get corresponding function type from the top flow level
    Type functionTypeVariableTopLevelType =
        getTopLevelFunctionType((fromFunctionParameterType).getName());

    if (functionTypeVariableTopLevelType instanceof TypeVariable) {
      // 1. If function type is still TypeVariable on top flow level, we check if the flow field
      // type is assignable from any of it's bounds
      // If no bounds are assignable to the flow field type, we consider that flow field type can't
      // be assigned
      return typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
          (TypeVariable) functionTypeVariableTopLevelType,
          toFlowFieldType,
          new OutFlowFunctionToFlowFieldContext(
              toFlowRecord, toFlowFieldFlowType, toFlowRecord.flowType));
    } else {
      // 2. If function type is materialized at the top level, we compare non-variable types
      return TypeComparator.isTypeAssignable1(
          functionTypeVariableTopLevelType,
          toFlowFieldType,
          new OutFlowFunctionToFlowFieldContext(
              toFlowRecord, toFlowFieldFlowType, toFlowRecord.flowType));
    }
  }

  @Override
  public boolean isAssignableMaterializedToVariable(
      Type fromFunctionParameterType, TypeVariable toFlowFieldType) {
    // Get corresponding flow field type from the top flow level
    Type flowTypeVariableTopLevelType = getTopLevelFieldType(toFlowFieldType.getName());

    if (flowTypeVariableTopLevelType instanceof TypeVariable) {
      // 1. If flow field type is TypeVariable at the top level, we can't assign a non-variable type
      // to it.
      return false;
    } else {
      // 2. If flow field type is materialized at the top level, we compare non-variable types
      return TypeComparator.isTypeAssignable1(
          fromFunctionParameterType,
          flowTypeVariableTopLevelType,
          new OutFlowFunctionToFlowFieldContext(
              toFlowRecord, toFlowRecord.flowType, fromFunctionFlowType));
    }
  }

  // Assumed type is always null, because Global Functions are not involved in this call
  @Override
  @Nullable
  public GlobalFunctionAssumedType getAssumedType() {
    return null;
  }
}
