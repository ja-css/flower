package com.flower.events;

import com.flower.anno.event.EventProfiles;
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

public class EventCallsTest {
  @Test
  public void test() throws ExecutionException, InterruptedException {
    int maxIterations = 10;

    Flower flower = new Flower();
    flower.registerFlow(TestFlow_C.class);
    flower.registerEventProfile(TestEventProfileCall.class);
    flower.registerGlobalFunctions(TestEventGlobalFunctionContainer.class);
    flower.initialize();

    FlowExec<TestFlow_C> flowExec = flower.getFlowExec(TestFlow_C.class);

    TestFlow_C testFlow = new TestFlow_C(maxIterations, "jamirov");
    FlowFuture<TestFlow_C> flowFuture = flowExec.runFlow(testFlow);
    System.out.println("Flow created. Id: " + flowFuture.getFlowId());

    TestFlow_C flow = flowFuture.getFuture().get();

    System.out.println("Flow done. " + flow);

    Assertions.assertEquals(10, flow.currentIteration);
  }
}

/** Test Flow */
@FlowType(firstStep = "printStats")
@EventProfiles({TestEventProfileCall.class})
class TestFlow_C {
  private static final int MAX_DELAY_MS = 5000;
  private static final int FIRST_DELAY = 50;

  // TODO: State tag missing validation
  @State
  private static final String FINISH = "Finish!";

  @State private final String username;
  @State private final int maxIterations;
  @State private int counter;
  @State int currentIteration;
  @State private int currentDelay;

  public TestFlow_C(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
    counter = 0;
  }

  @TransitStepRefPrm(paramName = "nextStep", stepName = "printGreeting")
  @StepFunction(transit = "nextStepTransit")
  static void printStats(
      @In int maxIterations, @Nullable @In int currentIteration, @In String username) {
    System.out.printf(
        "This is iteration #%d / %d for user %s.%n", currentIteration, maxIterations, username);
  }

  // TODO: step name check fails
  // @TransitParametersOverride(
  // stepRef = {@TransitStepRefPrm(paramName = "firstStep", stepName = "printStats"),
  // @TransitStepRefPrm(paramName = "end", stepName = "times7"), },
  @TransitParametersOverride(
      stepRef = {
        @TransitStepRefPrm(paramName = "firstStep", stepName = "printStats"),
        @TransitStepRefPrm(paramName = "end", stepName = "last7Times"),
      },
      in = @TransitInPrm(paramName = "maxIterations", from = "maxIterations"))
  @StepFunction(transit = "nextIterationOrEnd")
  static void printGreeting(@In String username) {
    System.out.printf("Hello from Flower, %s.%n", username);
  }

  @StepFunction(transit = "times7")
  static void last7Times(@In String username) {
    System.out.printf("Last 7 times, %s.%n", username);
  }

  @TransitFunction
  static Transition times7(
      @InOut NullableInOutPrm<Integer> counter,
      @StepRef Transition last7Times,
      @Terminal Transition end,
      @In String FINISH) {
    int cnt = counter.getInValue() + 1;
    counter.setOutValue(cnt);

    System.out.printf("TIMES %d%n", cnt);
    if (cnt >= 7) {
      System.out.printf(FINISH + "%n");
      return end;
    } else return last7Times.setDelay(Duration.ofMillis(100));
  }

  @TransitFunction
  static Transition nextStepTransit(@StepRef Transition nextStep) {
    System.out.printf("Transition to: %s.%n", nextStep);
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
      System.out.printf("Transition to: %s.%n", firstStep);

      currentIteration.setOutValue(currentIteration.getInValue() + 1);

      return firstStep.setDelay(Duration.ofMillis(currentDelay.getInValue()));
    }
  }
}
