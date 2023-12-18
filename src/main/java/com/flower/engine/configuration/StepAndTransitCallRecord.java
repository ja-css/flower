package com.flower.engine.configuration;

import com.flower.anno.functions.SimpleStepCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StepAndTransitCallRecord extends FunctionRecord {
  public final Class<?> flowType;
  public final Method method;
  final SimpleStepCall annotation;
  public final String stepName;
  public final Class<?> globalFunctionContainer;
  public final String globalFunctionName;
  public final boolean isFirstStep;

  public FunctionReturnValueRecord returnValueOverride;
  public List<FunctionParameterRecord> stepParameterOverrides;

  public StepAndTransitCallRecord(
      Class<?> flowType,
      Method method,
      SimpleStepCall annotation,
      String stepName,
      boolean isFirstStep) {
    this.flowType = flowType;
    this.method = method;
    this.annotation = annotation;
    this.stepName = stepName;
    this.globalFunctionContainer = annotation.globalFunctionContainer();
    this.globalFunctionName = annotation.globalFunctionName();
    this.isFirstStep = isFirstStep;

    returnValueOverride = loadFunctionReturnValue(null, method);
    stepParameterOverrides = new ArrayList<>();
  }

  public void initialize() {
    stepParameterOverrides = loadFunctionSignature(flowType, method, FunctionType.STEP_AND_TRANSIT);
  }
}
