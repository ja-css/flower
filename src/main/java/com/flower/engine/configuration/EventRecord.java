package com.flower.engine.configuration;

import com.flower.anno.event.EventFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventRecord extends FunctionRecord {
  public final Method method;
  public final EventFunction annotation;
  public final String eventFunctionName;

  public FunctionReturnValueRecord returnValue;
  public List<FunctionParameterRecord> functionSignature;

  public EventRecord(Method method, EventFunction annotation, String eventFunctionName) {
    this.method = method;
    this.annotation = annotation;
    this.eventFunctionName = eventFunctionName;

    returnValue = loadFunctionReturnValue(null, method);
    functionSignature = new ArrayList<>();
  }

  public void initialize() {
    functionSignature = loadEventFunctionSignature(method, annotation.types());
  }
}
