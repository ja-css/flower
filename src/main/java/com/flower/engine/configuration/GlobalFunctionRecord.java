package com.flower.engine.configuration;

import com.flower.anno.functions.GlobalFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GlobalFunctionRecord extends FunctionRecord {
  public final Class<?> globalFunctionContainerType;

  public final Method method;
  final GlobalFunction annotation;
  public final String functionName;

  public FunctionReturnValueRecord returnValue;
  public List<FunctionParameterRecord> functionSignature;

  public GlobalFunctionRecord(
      Class<?> globalFunctionContainerType,
      Method method,
      GlobalFunction annotation,
      String functionName) {
    this.globalFunctionContainerType = globalFunctionContainerType;
    this.method = method;
    this.annotation = annotation;
    this.functionName = functionName;

    returnValue = loadFunctionReturnValue(null, method);
    functionSignature = new ArrayList<>();
  }

  public void initialize() {
    functionSignature = loadGlobalFunctionSignature(method);
  }
}
