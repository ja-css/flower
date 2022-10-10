package com.flower.engine.runner.parameters.comparison.context;

import com.flower.engine.configuration.FlowGenericParametersRecord;
import com.flower.engine.runner.parameters.comparison.TypeComparator;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

/** Flow field injected into Global function parameter */
// what's the difference between this and FlowFieldToStepsGlobalTransitionerContext???
// the difference is that there is no method depicting call for GlobalTransitioner
public class InCallFunctionArgumentToGlobalFunctionContext implements GenericComparisonContext {
  public final FlowGenericParametersRecord fromFlowRecord;
  public final Method fromCallFunction;
  public final Class<?> fromCallFunctionFlowType;

  public final Method toGlobalFunction;

  public InCallFunctionArgumentToGlobalFunctionContext(
      FlowGenericParametersRecord fromFlowRecord,
      Method fromCallFunction,
      Class<?> fromCallFunctionFlowType,
      Method toGlobalFunction) {
    this.fromFlowRecord = fromFlowRecord;
    this.fromCallFunction = fromCallFunction;
    this.fromCallFunctionFlowType = fromCallFunctionFlowType;
    this.toGlobalFunction = toGlobalFunction;
  }

  @Override
  public boolean isAssignableVariable(
      TypeVariable fromCallFunctionParameterType, TypeVariable toGlobalFunctionParameterType) {
    // 1. We try to determine TypeVariable indexes in CallFunction and GlobalFunction declarations
    int callFunctionParameterIndex = -1;
    for (int i = 0; i < fromCallFunction.getTypeParameters().length; i++) {
      if (fromCallFunction.getTypeParameters()[i].equals(fromCallFunctionParameterType)) {
        callFunctionParameterIndex = i;
        break;
      }
    }

    int globalFunctionParameterIndex = -1;
    for (int i = 0; i < toGlobalFunction.getTypeParameters().length; i++) {
      if (toGlobalFunction.getTypeParameters()[i].equals(toGlobalFunctionParameterType)) {
        globalFunctionParameterIndex = i;
        break;
      }
    }

    if (globalFunctionParameterIndex != callFunctionParameterIndex
        || callFunctionParameterIndex == -1) {
      // 1.1 If indexes are not found or don't match, we consider this not assignable
      return false;
    }

    // 2. Global function variable type should be able to assume the type imposed by call function
    // type.
    // Therefore, each of global function type bounds must be assignable from at least one of call
    // function type bounds.
    // Call function type >= Global function type
    if (eachBoundIsAssignableFromAtLeastOneBound(
        fromCallFunctionParameterType, toGlobalFunctionParameterType, this)) {
      // And in the context of the call, global function's TypeVariable assumes the top level Flow
      // representation of CallFunction type
      Type callFunctionVariableTopLevelType =
          Preconditions.checkNotNull(
              fromFlowRecord.getCorrespondingType(
                  fromCallFunctionFlowType, fromCallFunctionParameterType.getName()));
      assumedType =
          new GlobalFunctionAssumedType(
              toGlobalFunctionParameterType.getName(), callFunctionVariableTopLevelType);
      return true;
    }

    return false;
  }

  static boolean eachBoundIsAssignableFromAtLeastOneBound(
      TypeVariable fromAtLeastOneBound,
      TypeVariable eachBoundIsAssignable,
      GenericComparisonContext context) {
    for (Type bound : eachBoundIsAssignable.getBounds()) {
      if (bound instanceof TypeVariable) {
        if (!eachBoundIsAssignableFromAtLeastOneBound(
            fromAtLeastOneBound, (TypeVariable) bound, context)) return false;
      } else if (!typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
          fromAtLeastOneBound, bound, context)) {
        return false;
      }
    }
    return true;
  }

  static boolean typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
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
      TypeVariable fromCallFunctionParameterType, Type toGlobalFunctionParameterType) {
    // We check if non-variable global function type is assignable from one of it's bounds.
    // Since GlobalFunction type is non-variable, there is no type assumption/reversing and this
    // comparison is regular.
    return typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
        fromCallFunctionParameterType, toGlobalFunctionParameterType, this);

    // Since global function type is not variable, there is no related type assumption.
  }

  static boolean typeIsAssignableToAllBoundsOfTypeVariable(
      Type fromCallType,
      TypeVariable allGlobalBoundsAreAssignable,
      GenericComparisonContext context) {
    for (Type boundMustBeAssignable : allGlobalBoundsAreAssignable.getBounds()) {
      if (boundMustBeAssignable instanceof TypeVariable) {
        if (!typeIsAssignableToAllBoundsOfTypeVariable(
            fromCallType, (TypeVariable) boundMustBeAssignable, context)) return false;
      } else if (!TypeComparator.isTypeAssignable1(fromCallType, boundMustBeAssignable, context)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean isAssignableMaterializedToVariable(
      Type fromCallFunctionParameterType, TypeVariable toGlobalFunctionParameterType) {
    // Global function variable type should be able to assume the type imposed by call function
    // type.
    // Therefore, all global function type bounds must be assignable FROM call function type.
    // Call function type >= Global function type
    boolean result =
        typeIsAssignableToAllBoundsOfTypeVariable(
            fromCallFunctionParameterType, toGlobalFunctionParameterType, this);

    // And in the context of the call, global function's TypeVariable assumes the given CallFunction
    // type.
    assumedType =
        new GlobalFunctionAssumedType(
            toGlobalFunctionParameterType.getName(), fromCallFunctionParameterType);
    return result;
  }

  @Nullable private GlobalFunctionAssumedType assumedType = null;

  @Override
  @Nullable
  public GlobalFunctionAssumedType getAssumedType() {
    return assumedType;
  }
}
