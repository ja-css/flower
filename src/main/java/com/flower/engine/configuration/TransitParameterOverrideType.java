package com.flower.engine.configuration;

public enum TransitParameterOverrideType {
  TRANSIT_IN(FunctionParameterType.IN),
  TRANSIT_OUT(FunctionParameterType.OUT),
  TRANSIT_IN_OUT(FunctionParameterType.IN_OUT),
  TRANSIT_IN_RET(FunctionParameterType.IN_RET),
  TRANSIT_STEP_REF(FunctionParameterType.STEP_REF),
  TRANSIT_TERMINAL(FunctionParameterType.TERMINAL);

  private final FunctionParameterType functionParameterType;

  TransitParameterOverrideType(FunctionParameterType functionParameterType) {
    this.functionParameterType = functionParameterType;
  }

  public FunctionParameterType functionParameterType() {
    return functionParameterType;
  }
}
