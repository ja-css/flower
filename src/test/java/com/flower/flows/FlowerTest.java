package com.flower.flows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Output;
import com.flower.anno.params.step.transitOverride.TransitInPrm;
import com.flower.anno.params.step.transitOverride.TransitParametersOverride;
import com.flower.anno.params.step.transitOverride.TransitStepRefPrm;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowerTest {
  @Test
  public void testFlower() throws ExecutionException, InterruptedException {
    int maxIterations = 10;

    Flower flower = new Flower();
    flower.registerFlow(TestFlow.class);
    flower.initialize();

    FlowExec<TestFlow> flowExec = flower.getFlowExec(TestFlow.class);

    TestFlow testFlow = new TestFlow(maxIterations, "jamirov");
    FlowFuture<TestFlow> flowFuture = flowExec.runFlow(testFlow);
    System.out.println("Flow created. Id: " + flowFuture.getFlowId());

    TestFlow flow = flowFuture.getFuture().get();

    System.out.println("Flow done. " + flow);

    Assertions.assertEquals(10, flow.currentIteration);
  }
}

/** Test Flow */
@FlowType(firstStep = "printStats")
class TestFlow {
  private static final int MAX_DELAY_MS = 10000;
  private static final int FIRST_DELAY = 50;

  @State
  private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestFlow(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  // Input parameters / Mappings
  // Output parameters / Mappings
  // Return value / Mappings
  @TransitStepRefPrm(paramName = "nextStep", stepName = "printGreeting")
  @StepFunction(transit = "nextStepTransit")
  static void printStats(
      @In int maxIterations, @Nullable @In int currentIteration, @In String username) {
    System.out.printf(
        "This is iteration #%d / %d for user %s.%n", currentIteration, maxIterations, username);
  }

  @TransitParametersOverride(
      stepRef = {@TransitStepRefPrm(paramName = "firstStep", stepName = "printStats")},
      in = @TransitInPrm(paramName = "maxIterations", from = "maxIterations"))
  @StepFunction(transit = "nextIterationOrEnd")
  static void printGreeting(@In String username) {
    System.out.printf("Hello from Flower, %s.%n", username);
  }

  @TransitFunction
  static Transition nextStepTransit(@StepRef Transition nextStep) {
    return nextStep;
  }

  @TransitFunction
  static Transition nextIterationOrEnd(
      @In int maxIterations,
      @InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> currentIteration,
      @InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> currentDelay,
      @StepRef Transition firstStep,
      @Terminal Transition end) {
    if (currentIteration.getInValue() + 1 > maxIterations) {
      System.out.printf(
          "Iteration #%d done. Max iterations %d. Finalizing%n",
          currentIteration.getInValue(), maxIterations);
      return end;
    } else {
      if (currentDelay.getInValue() < FIRST_DELAY) currentDelay.setOutValue(FIRST_DELAY);
      else if (currentDelay.getInValue() < MAX_DELAY_MS) {
        currentDelay.setOutValue(currentDelay.getInValue() * 2);
        if (currentDelay.getInValue() > MAX_DELAY_MS) currentDelay.setOutValue(MAX_DELAY_MS);
      }

      System.out.printf(
          "Iteration #%d done. Sleeping for %d%n",
          currentIteration.getInValue(), currentDelay.getInValue());

      currentIteration.setOutValue(currentIteration.getInValue() + 1);

      return firstStep.setDelay(Duration.ofMillis(currentDelay.getInValue()));
    }
  }
}
