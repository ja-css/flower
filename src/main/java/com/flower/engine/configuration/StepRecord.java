package com.flower.engine.configuration;

import com.flower.anno.functions.StepFunction;
import com.google.common.base.Strings;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StepRecord extends StepFunctionRecord {
  public final Class<?> flowType;
  public final Method method;
  public final StepFunction annotation;
  public final String stepName;
  public final String transitionerName;
  public final Class<?> globalTransitionerContainer;
  public final String globalTransitionerName;
  public final String returnTo;
  public final boolean isFirstStep;

  public FunctionReturnValueRecord returnValue;
  public List<FunctionParameterRecord> functionSignature;
  public List<TransitParameterOverrideRecord> transitParameterOverrides;

  public StepRecord(
      Class<?> flowType,
      Method method,
      StepFunction annotation,
      String stepName,
      boolean isFirstStep) {
    this.flowType = flowType;
    this.method = method;
    this.annotation = annotation;
    this.stepName = stepName;
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

    returnValue = loadFunctionReturnValue(flowType, method);
    functionSignature = new ArrayList<>();
    transitParameterOverrides = new ArrayList<>();
  }

  public void initialize() {
    functionSignature = loadFunctionSignature(flowType, method, FunctionType.STEP);
    transitParameterOverrides = loadTransitParameterOverrides(method);
  }
}
