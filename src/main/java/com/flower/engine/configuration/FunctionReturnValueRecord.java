package com.flower.engine.configuration;

import java.lang.reflect.Type;
import javax.annotation.Nullable;

public class FunctionReturnValueRecord {
  @Nullable public final Class<?> flowType;
  public final Type genericReturnValueType;
  public final boolean isNullable;

  public FunctionReturnValueRecord(
      @Nullable Class<?> flowType, Type genericReturnValueType, boolean isNullable) {
    this.flowType = flowType;
    this.genericReturnValueType = genericReturnValueType;
    this.isNullable = isNullable;
  }
}
