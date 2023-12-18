package com.flower.engine.configuration;

import com.flower.anno.functions.TransitCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TransitionerCallRecord extends FunctionRecord {
  public final Class<?> flowType;
  public final Method method;
  final TransitCall annotation;
  public final Class<?> globalFunctionContainer;
  public final String globalFunctionName;
  public final String transitionerName;

  public FunctionReturnValueRecord returnValueOverride;
  public List<FunctionParameterRecord> transitParameterOverrides;

  public TransitionerCallRecord(
      Class<?> flowType, Method method, TransitCall annotation, String transitionerName) {
    this.flowType = flowType;
    this.method = method;
    this.annotation = annotation;
    this.globalFunctionContainer = annotation.globalFunctionContainer();
    this.globalFunctionName = annotation.globalFunctionName();
    this.transitionerName = transitionerName;

    returnValueOverride = loadFunctionReturnValue(null, method);
    transitParameterOverrides = new ArrayList<>();
  }

  public void initialize() {
    transitParameterOverrides = loadFunctionSignature(flowType, method, FunctionType.TRANSIT);
  }
}
