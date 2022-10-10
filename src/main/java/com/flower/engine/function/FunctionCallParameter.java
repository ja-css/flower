package com.flower.engine.function;

import com.flower.anno.params.common.Output;
import java.lang.reflect.Type;
import javax.annotation.Nullable;

public class FunctionCallParameter {
  final @Nullable String stateFieldName;
  final @Nullable Output output;
  final String functionParameterName;
  final ParameterType functionParameterType;
  final @Nullable Object specialObject;
  // The only reason we have this here is to enable Defaults for @InFromFlow here: FunctionCallUtil:
  // line 386
  final Type parameterType;
  final boolean isNullableParameter;

  public FunctionCallParameter(
      @Nullable String stateFieldName,
      @Nullable Output output,
      String functionParameterName,
      ParameterType functionParameterType,
      Type parameterType,
      @Nullable Object specialObject,
      boolean isNullableParameter) {
    this.stateFieldName = stateFieldName;
    this.output = output;
    this.functionParameterName = functionParameterName;
    this.functionParameterType = functionParameterType;
    this.parameterType = parameterType;
    this.specialObject = specialObject;
    this.isNullableParameter = isNullableParameter;
  }

  public @Nullable String getStateFieldName() {
    return stateFieldName;
  }

  public @Nullable Output getOutput() {
    return output;
  }

  public String getFunctionParameterName() {
    return functionParameterName;
  }

  public ParameterType getFunctionParameterType() {
    return functionParameterType;
  }

  public Type getParameterType() {
    return parameterType;
  }

  public @Nullable Object getSpecialObject() {
    return specialObject;
  }

  public boolean isNullableParameter() {
    return isNullableParameter;
  }
}
