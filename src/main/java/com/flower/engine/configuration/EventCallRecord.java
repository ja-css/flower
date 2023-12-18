package com.flower.engine.configuration;

import com.flower.anno.event.EventCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventCallRecord extends FunctionRecord {
  public final Method method;
  public final EventCall annotation;
  public final String eventCallName;
  public final Class<?> globalFunctionContainer;
  public final String globalFunctionName;

  public FunctionReturnValueRecord returnValueOverride;
  public List<FunctionParameterRecord> eventFunctionParameterOverrides;

  public EventCallRecord(Method method, EventCall annotation, String eventCallName) {
    this.method = method;
    this.annotation = annotation;
    this.eventCallName = eventCallName;
    this.globalFunctionContainer = annotation.globalFunctionContainer();
    this.globalFunctionName = annotation.globalFunctionName();

    returnValueOverride = loadFunctionReturnValue(null, method);
    eventFunctionParameterOverrides = new ArrayList<>();
  }

  public void initialize() {
    eventFunctionParameterOverrides = loadEventFunctionSignature(method, annotation.types());
  }
}
