package com.flower.engine.configuration;

import com.flower.anno.functions.SimpleStepFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StepAndTransitRecord extends FunctionRecord {
  public final Class<?> flowType;
  public final Method method;
  final SimpleStepFunction annotation;
  public final String stepName;
  public final boolean isFirstStep;

  public FunctionReturnValueRecord returnValue;
  public List<FunctionParameterRecord> functionSignature;

  public StepAndTransitRecord(
      Class<?> flowType,
      Method method,
      SimpleStepFunction annotation,
      String stepName,
      boolean isFirstStep) {
    this.flowType = flowType;
    this.method = method;
    this.annotation = annotation;
    this.stepName = stepName;
    this.isFirstStep = isFirstStep;

    returnValue = loadFunctionReturnValue(null, method);
    functionSignature = new ArrayList<>();
  }

  public void initialize() {
    functionSignature = loadFunctionSignature(flowType, method, FunctionType.STEP_AND_TRANSIT);
  }
}
