package com.flower.ooms;

import com.flower.anno.flow.FlowType;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.Transition;

@FlowType(firstStep = "INFINITE_LOOP_STEP")
public class InfiniteLoopFlow {
  static int i = 0;

  @SimpleStepFunction
  public static Transition INFINITE_LOOP_STEP(
      @StepRef Transition INFINITE_LOOP_STEP, @StepRef Transition END_STEP) {
    System.out.println("Infinite " + (i++));
    //        return END_STEP;
    return INFINITE_LOOP_STEP;
    // return INFINITE_LOOP_STEP.setDelay(Duration.ofMillis(1));
    //        return INFINITE_LOOP_STEP.setDelay(Duration.ofMillis(10));
  }

  @SimpleStepFunction
  public static Transition END_STEP(@Terminal Transition END) {
    return END;
  }
}
