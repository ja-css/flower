package com.flower.engine.configuration;

import com.flower.anno.functions.StepCall;
import com.google.common.base.Strings;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StepCallRecord extends StepFunctionRecord {
  public final Class<?> flowType;
  public final Method method;
  final StepCall annotation;
  public final String stepName;
  public final Class<?> globalFunctionContainer;
  public final String globalFunctionName;
  public final String transitionerName;
  public final Class<?> globalTransitionerContainer;
  public final String globalTransitionerName;
  public final String returnTo;
  public final boolean isFirstStep;

  public FunctionReturnValueRecord returnValueOverride;
  public List<FunctionParameterRecord> stepParameterOverrides;
  public List<TransitParameterOverrideRecord> transitParameterOverrides;

  public StepCallRecord(
      Class<?> flowType, Method method, StepCall annotation, String stepName, boolean isFirstStep) {
    this.flowType = flowType;
    this.method = method;
    this.annotation = annotation;
    this.stepName = stepName;
    this.globalFunctionContainer = annotation.globalFunctionContainer();
    this.globalFunctionName = annotation.globalFunctionName();
    this.transitionerName = annotation.transit();
    this.globalTransitionerContainer = annotation.globalTransitContainer();
    this.globalTransitionerName = annotation.globalTransit();
    if (!globalTransitionerName.equalsIgnoreCase("") && globalTransitionerContainer == void.class) {
      throw new IllegalStateException(
          String.format("GlobalTransitionerName must be accompanied by GlobalTransitionerContainer. " +
              "Flow: %s, step: %s", flowType, stepName)
      );
    }
    this.returnTo = Strings.emptyToNull(annotation.returnTo());
    this.isFirstStep = isFirstStep;

    returnValueOverride = loadFunctionReturnValue(flowType, method);
    stepParameterOverrides = new ArrayList<>();
    transitParameterOverrides = new ArrayList<>();
  }

  public void initialize() {
    stepParameterOverrides = loadFunctionSignature(flowType, method, FunctionType.STEP);
    transitParameterOverrides = loadTransitParameterOverrides(method);
  }
}
