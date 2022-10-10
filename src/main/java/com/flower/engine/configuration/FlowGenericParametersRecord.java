package com.flower.engine.configuration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.annotation.Nullable;

public class FlowGenericParametersRecord {
  public final Class<?> flowType;

  private Map<String, Integer> genericParameterIndexByName;
  private List<TypeVariable<? extends Class<?>>> genericParameters;

  private List<Type> parentGenericParameters;
  public Map<String, Integer> parentGenericParameterIndexByName;

  private @Nullable FlowGenericParametersRecord parentGenericParametersRecord;

  public FlowGenericParametersRecord(Class<?> flowType) {
    this.flowType = flowType;
    genericParameterIndexByName = new HashMap<>();
    genericParameters = new ArrayList<>();

    int i = 0;
    for (TypeVariable<? extends Class<?>> prm : flowType.getTypeParameters()) {
      String prmName = prm.getName();

      genericParameterIndexByName.put(prmName, i);
      genericParameters.add(prm);

      parentGenericParametersRecord = null;
      i++;
    }

    parentGenericParameters = new ArrayList<>();
    parentGenericParameterIndexByName = new HashMap<>();
    Type genericParent = flowType.getGenericSuperclass();
    if (genericParent instanceof ParameterizedType) {
      int j = 0;
      for (Type argType : ((ParameterizedType) genericParent).getActualTypeArguments()) {
        parentGenericParameters.add(argType);
        parentGenericParameterIndexByName.put(argType.getTypeName(), j);
        j++;
      }
    }
  }

  @Nullable
  public FlowGenericParametersRecord getParentGenericParametersRecord() {
    return parentGenericParametersRecord;
  }

  public void mergeWithParentGenericParametersRecord(
      @Nullable FlowGenericParametersRecord parentGenericParametersRecord) {
    this.parentGenericParametersRecord = parentGenericParametersRecord;
  }

  /** Trace generic type from parent type to */
  @Nullable
  public Type getCorrespondingType(Class<?> fromFlowType, String functionParameterName) {
    FlowGenericParametersRecord cursor = this;
    Stack<FlowGenericParametersRecord> stack = new Stack<>();

    do {
      stack.push(cursor);
      if (cursor.flowType.equals(fromFlowType)) break;

      cursor = cursor.parentGenericParametersRecord;
    } while (cursor != null);

    if (stack.isEmpty() || !stack.peek().flowType.equals(fromFlowType)) {
      // TODO: flow not found
      return null;
    }

    FlowGenericParametersRecord functionFlowType = stack.pop();
    Integer parameterIndex =
        functionFlowType.genericParameterIndexByName.get(functionParameterName);

    while (!stack.isEmpty()) {
      functionFlowType = stack.pop();
      Type parentGenericParameter = functionFlowType.parentGenericParameters.get(parameterIndex);
      if (functionFlowType.genericParameterIndexByName.isEmpty()) return parentGenericParameter;

      String parameterName = parentGenericParameter.getTypeName();
      parameterIndex = functionFlowType.genericParameterIndexByName.get(parameterName);
    }

    return parameterIndex == null ? null : functionFlowType.genericParameters.get(parameterIndex);
  }

  @Nullable
  public Integer getGenericParameterIndex(String genericParameterName) {
    return genericParameterIndexByName.get(genericParameterName);
  }
}
