package com.flower.engine.runner.parameters;

import com.flower.engine.function.FunctionCallParameter;
import com.flower.engine.runner.parameters.comparison.context.GlobalFunctionAssumedType;

import java.util.List;

public class ParameterCreationResult {
  public final FunctionCallParameter parameter;
  public final List<GlobalFunctionAssumedType> assumedTypes;

  public ParameterCreationResult(
      FunctionCallParameter parameter, List<GlobalFunctionAssumedType> assumedTypes) {
    this.parameter = parameter;
    this.assumedTypes = assumedTypes;
  }
}
