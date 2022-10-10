package com.flower.engine.runner.parameters.comparison.context;

import java.lang.reflect.Type;

public class GlobalFunctionAssumedType {
  public final String typeVariableName;
  public final Type assumedType;

  public GlobalFunctionAssumedType(String typeVariableName, Type assumedType) {
    this.typeVariableName = typeVariableName;
    this.assumedType = assumedType;
  }
}
