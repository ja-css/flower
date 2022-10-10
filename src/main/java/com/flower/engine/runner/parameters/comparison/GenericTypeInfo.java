package com.flower.engine.runner.parameters.comparison;

import java.lang.reflect.Type;

/** Type with mapped {generic argument declared type}s */
public class GenericTypeInfo {
  final Type rawType;
  final Type[] typeArguments;

  public GenericTypeInfo(Type rawType, Type[] typeArguments) {
    this.rawType = rawType;
    this.typeArguments = typeArguments;
  }
}
