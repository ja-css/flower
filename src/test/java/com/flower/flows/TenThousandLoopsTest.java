package com.flower.flows;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

public class TenThousandLoopsTest {
  @Test
  public void testFlower() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(TenThousandLoopsFlow.class);
    flower.initialize();

    FlowExec<TenThousandLoopsFlow> flowExec = flower.getFlowExec(TenThousandLoopsFlow.class);
    FlowFuture<TenThousandLoopsFlow> flowFuture = flowExec.runFlow(new TenThousandLoopsFlow());
    TenThousandLoopsFlow flow = flowFuture.getFuture().get();

    assertEquals(10000, flow.iterations);
  }
}

/** Test Flow */
@FlowType(firstStep = "step")
class TenThousandLoopsFlow {
  @State
  int iterations = 0;

  @SimpleStepFunction
  static Transition step(
      @StepRef Transition step,
      @Terminal Transition end,
      @InOut NullableInOutPrm<Integer> iterations) {
    int i = iterations.getInValue() + 1;
    iterations.setOutValue(i);

    if (i < 10000) return step;
    else return end;
  }
}
