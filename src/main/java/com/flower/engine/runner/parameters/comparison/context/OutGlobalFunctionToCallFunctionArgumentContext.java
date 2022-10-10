package com.flower.engine.runner.parameters.comparison.context;

import com.flower.engine.configuration.FlowGenericParametersRecord;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javax.annotation.Nullable;

/** Flow field injected into Global function parameter */
public class OutGlobalFunctionToCallFunctionArgumentContext implements GenericComparisonContext {
  public final FlowGenericParametersRecord toFlowRecord;
  public final Method toCallFunction;
  public final Class<?> toCallFunctionFlowType;

  public final Method fromGlobalFunction;

  InCallFunctionArgumentToGlobalFunctionContext reverseContext;

  public OutGlobalFunctionToCallFunctionArgumentContext(
      FlowGenericParametersRecord toFlowRecord,
      Method toCallFunction,
      Class<?> toCallFunctionFlowType,
      Method fromGlobalFunction) {
    this.toFlowRecord = toFlowRecord;
    this.toCallFunction = toCallFunction;
    this.toCallFunctionFlowType = toCallFunctionFlowType;
    this.fromGlobalFunction = fromGlobalFunction;

    reverseContext =
        new InCallFunctionArgumentToGlobalFunctionContext(
            toFlowRecord, toCallFunction, toCallFunctionFlowType, fromGlobalFunction);
  }

  @Override
  public boolean isAssignableVariable(
      TypeVariable fromGlobalFunctionParameterType, TypeVariable toCallFunctionParameterType) {
    // Global function type assumes call type, as defined in InContext
    boolean result =
        reverseContext.isAssignableVariable(
            toCallFunctionParameterType, fromGlobalFunctionParameterType);
    assumedType = reverseContext.getAssumedType();
    return result;
  }

  @Override
  public boolean isAssignableFromVariableToMaterialized(
      TypeVariable fromGlobalFunctionParameterType, Type toCallFunctionParameterType) {
    // Global function type assumes call type, as defined in InContext
    boolean result =
        reverseContext.isAssignableMaterializedToVariable(
            toCallFunctionParameterType, fromGlobalFunctionParameterType);
    assumedType = reverseContext.getAssumedType();
    return result;
  }

  @Override
  public boolean isAssignableMaterializedToVariable(
      Type fromGlobalFunctionParameterType, TypeVariable toCallFunctionParameterType) {
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
