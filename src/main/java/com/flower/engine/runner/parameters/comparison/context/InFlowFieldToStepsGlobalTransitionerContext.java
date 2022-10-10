package com.flower.engine.runner.parameters.comparison.context;

import com.flower.engine.configuration.FlowGenericParametersRecord;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

/** Flow field injected into */
// what's the difference between this and CallFunctionArgumentToGlobalFunctionContext???
// the difference is that there is no method depicting call for GlobalTransitioner
public class InFlowFieldToStepsGlobalTransitionerContext implements GenericComparisonContext {
  public final FlowGenericParametersRecord fromFlowRecord;
  public final Class<?> fromFlowFieldFlowType;

  public final Class<?> fromStepFunctionFlowType;

  public final Method toGlobalFunction;

  public InFlowFieldToStepsGlobalTransitionerContext(
      FlowGenericParametersRecord fromFlowRecord,
      Class<?> fromFlowFieldFlowType,
      Class<?> fromStepFunctionFlowType,
      Method toGlobalFunction) {
    this.fromFlowRecord = fromFlowRecord;
    this.fromFlowFieldFlowType = fromFlowFieldFlowType;
    this.fromStepFunctionFlowType = fromStepFunctionFlowType;
    this.toGlobalFunction = toGlobalFunction;
  }

  @Override
  public boolean isAssignableVariable(
      TypeVariable fromFlowFieldType, TypeVariable toGlobalTransitionerParameterType) {
    // 2. Global function variable type should be able to assume the type imposed by Flow Field
    // type.
    // Therefore, each of global function type bounds must be assignable from at least one of Flow
    // Field type bounds.
    // Flow Field type >= Global function type
    if (InCallFunctionArgumentToGlobalFunctionContext.eachBoundIsAssignableFromAtLeastOneBound(
        fromFlowFieldType, toGlobalTransitionerParameterType, this)) {
      // And in the context of the call, global function's TypeVariable assumes the top level Flow
      // representation of Field type
      Type flowFieldVariableTopLevelType =
          Preconditions.checkNotNull(
              fromFlowRecord.getCorrespondingType(
                  fromFlowFieldFlowType, fromFlowFieldType.getName()));
      assumedType =
          new GlobalFunctionAssumedType(
              toGlobalTransitionerParameterType.getName(), flowFieldVariableTopLevelType);
      return true;
    }

    return false;
  }

  @Override
  public boolean isAssignableFromVariableToMaterialized(
      TypeVariable fromFlowFieldType, Type toGlobalTransitionerParameterType) {
    // We check if non-variable global function type is assignable from one of Flow Field type's
    // bounds.
    // Since GlobalFunction type is non-variable, there is no type assumption/reversing and this
    // comparison is regular.
    return InCallFunctionArgumentToGlobalFunctionContext
        .typeIsAssignableFromAtLeastOneBoundOfTypeVariable(
            fromFlowFieldType, toGlobalTransitionerParameterType, this);

    // Since global function type is non-variable, there is no related type assumption.
  }

  @Override
  public boolean isAssignableMaterializedToVariable(
      Type fromFlowFieldType, TypeVariable toGlobalTransitionerParameterType) {
    // Global function variable type should be able to assume the type imposed by Flow Field type.
    // Therefore, all global function type bounds must be assignable FROM Flow Field type.
    // Flow field type >= Global function type
    boolean result =
        InCallFunctionArgumentToGlobalFunctionContext.typeIsAssignableToAllBoundsOfTypeVariable(
            fromFlowFieldType, toGlobalTransitionerParameterType, this);

    // And in the context of the call, global function's TypeVariable assumes the given FlowField
    // type.
    assumedType =
        new GlobalFunctionAssumedType(
            toGlobalTransitionerParameterType.getName(), fromFlowFieldType);
    return result;
  }

  @Nullable private GlobalFunctionAssumedType assumedType = null;

  @Override
  @Nullable
  public GlobalFunctionAssumedType getAssumedType() {
    return assumedType;
  }
}
