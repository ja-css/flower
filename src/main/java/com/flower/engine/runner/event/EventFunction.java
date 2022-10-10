package com.flower.engine.runner.event;

import com.flower.anno.event.Concurrency;
import com.flower.engine.runner.callfunc.FunctionCallContext;

public class EventFunction {
  public final String functionName;
  public final Concurrency concurrency;
  public final FunctionCallContext functionCallContext;

  public EventFunction(
      String functionName, Concurrency concurrency, FunctionCallContext functionCallContext) {
    this.functionName = functionName;
    this.concurrency = concurrency;
    this.functionCallContext = functionCallContext;
  }
}
