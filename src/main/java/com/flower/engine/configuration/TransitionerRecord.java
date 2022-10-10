package com.flower.engine.configuration;

import com.flower.anno.functions.TransitFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TransitionerRecord extends FunctionRecord {
  public final Class<?> flowType;
  public final Method method;
  final TransitFunction annotation;
  public final String transitionerName;

  public FunctionReturnValueRecord returnValue;
  public List<FunctionParameterRecord> functionSignature;

  public TransitionerRecord(
      Class<?> flowType, Method method, TransitFunction annotation, String transitionerName) {
    this.flowType = flowType;
    this.method = method;
    this.annotation = annotation;
    this.transitionerName = transitionerName;

    returnValue = loadFunctionReturnValue(null, method);
    functionSignature = new ArrayList<>();
  }

  public void initialize() {
    functionSignature = loadFunctionSignature(flowType, method, FunctionType.TRANSIT);
  }
}
