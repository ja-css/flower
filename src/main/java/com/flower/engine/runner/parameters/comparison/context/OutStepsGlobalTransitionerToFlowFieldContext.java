package com.flower.engine.runner.parameters.comparison.context;

import com.flower.engine.configuration.FlowGenericParametersRecord;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

/** Flow field injected into */
// what's the difference between this and CallFunctionArgumentToGlobalFunctionContext???
// the difference is that there is no method depicting call for GlobalTransitioner
public class OutStepsGlobalTransitionerToFlowFieldContext implements GenericComparisonContext {
  public final FlowGenericParametersRecord toFlowRecord;
  public final Class<?> toFlowFieldFlowType;

  public final Class<?> toStepFunctionFlowType;

  public final Method fromGlobalFunction;
  public final InFlowFieldToStepsGlobalTransitionerContext reverseContext;

  public OutStepsGlobalTransitionerToFlowFieldContext(
      FlowGenericParametersRecord toFlowRecord,
      Class<?> toFlowFieldFlowType,
      Class<?> toStepFunctionFlowType,
      Method fromGlobalFunction) {
    this.toFlowRecord = toFlowRecord;
    this.toFlowFieldFlowType = toFlowFieldFlowType;
    this.toStepFunctionFlowType = toStepFunctionFlowType;
    this.fromGlobalFunction = fromGlobalFunction;

    this.reverseContext =
        new InFlowFieldToStepsGlobalTransitionerContext(
            toFlowRecord, toFlowFieldFlowType, toStepFunctionFlowType, fromGlobalFunction);
  }

  @Override
  public boolean isAssignableVariable(
      TypeVariable fromGlobalTransitionerParameterType, TypeVariable toFlowFieldType) {
    // Global function type assumes call type, as defined in InContext
    boolean result =
        reverseContext.isAssignableVariable(toFlowFieldType, fromGlobalTransitionerParameterType);
    assumedType = reverseContext.getAssumedType();
    return result;
  }

  @Override
  public boolean isAssignableFromVariableToMaterialized(
      TypeVariable fromGlobalTransitionerParameterType, Type toFlowFieldType) {
    // Global function type assumes call type, as defined in InContext
    boolean result =
        reverseContext.isAssignableMaterializedToVariable(
            toFlowFieldType, fromGlobalTransitionerParameterType);
    assumedType = reverseContext.getAssumedType();
    return result;
  }

  @Override
  public boolean isAssignableMaterializedToVariable(
      Type fromGlobalTransitionerParameterType, TypeVariable toFlowFieldType) {
    // If call function type is TypeVariable, we can't assign a non-variable global function type to
    // it.
    // Type assumption is also impossible in this case.
    return false;
  }

  @Nullable private GlobalFunctionAssumedType assumedType = null;

  @Override
  @Nullable
  public GlobalFunctionAssumedType getAssumedType() {
    return assumedType;
  }
}
