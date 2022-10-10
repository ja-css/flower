package com.flower.flows;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.TransitCall;
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
import com.flower.conf.InOutPrm;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowerCallsTest {
  @Test
  public void testFlowerNonexistentParameter() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsNonexistentParameter.class);
    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("Function argument references nonexistent global function argument"));
  }

  @Test
  public void testFlowerParameterNotOverridden() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsGlobalParameterNotOverridden.class);
    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("Global function argument must be referenced in a call"));
  }

  @Test
  public void testCallsReturnValueMismatch() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsReturnValueMismatch.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("@ReturnTo or return value parameter type mismatch"));
  }

  @Test
  public void testCallsReturnValueNullableMismatch() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsNullableReturnValueMismatch.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "Return value @Nullable annotations should match in CallFunction and GlobalFunction"));
  }

  @Test
  public void testFlower() throws ExecutionException, InterruptedException {
    final int maxIterations = 10;

    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsFlowSimple.class);
    flower.initialize();

    TestCallsFlowSimple testFlow = new TestCallsFlowSimple(maxIterations, "jamirov");

    FlowExec<TestCallsFlowSimple> flowExec = flower.getFlowExec(TestCallsFlowSimple.class);

    FlowFuture<TestCallsFlowSimple> flowFuture = flowExec.runFlow(testFlow);
    System.out.println("Flow created. Id: " + flowFuture.getFlowId());

    TestCallsFlowSimple flow = flowFuture.getFuture().get();

    System.out.println("Flow done. " + flow);

    Assertions.assertEquals(10, flow.currentIteration);
  }

  @Test
  public void testTestCallsNullableParameterMismatch() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsNullableParameterMismatch.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("Call's @Nullable prm annotation should match Global"));
  }

  @Test
  public void testTestCallsInOutMismatch1() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsInOutMismatch1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "Call's @InOut type should match Global type (InOutPrm or NullableInOutPrm)"));
  }

  @Test
  public void testTestCallsInOutMismatch2() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalTestFunctionsContainer.class);
    flower.registerFlow(TestCallsInOutMismatch2.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("Call Parameter output type should match Global Parameter"));
  }
}

/** Test Flow */
@GlobalFunctionContainer
class GlobalTestFunctionsContainer {
  private static final int MAX_DELAY_MS = 10000;
  private static final int FIRST_DELAY = 50;

  @GlobalFunction
  static void printStats(
      @In int maxIterations, @Nullable @In int currentIteration, @In String username) {
    System.out.printf(
        "This is iteration #%d / %d for user %s.%n", currentIteration, maxIterations, username);
  }

  @GlobalFunction
  static Integer printStats5(@In int maxIterations, @In int currentIteration, @In String username) {
    return 55;
  }

  @GlobalFunction
  static void printStats6(@InOut InOutPrm<Integer> i) {
    i.setOutValue(55);
  }

  @GlobalFunction
  static void printGreeting(@In String username) {
    System.out.printf("Hello from Flower, %s.%n", username);
  }

  @GlobalFunction
  static Transition nextStepTransit(@StepRef Transition nextStep) {
    return nextStep;
  }

  @GlobalFunction
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
      currentDelay.setOutValue(123);
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

// 1. simple
// 2. parameter overload
// 3. transitioner global pointer: @StepFunction(transit
@FlowType(firstStep = "printStats")
class TestCallsFlowSimple {
  @State
  private final String username0;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsFlowSimple(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username0 = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @TransitStepRefPrm(paramName = "nextStep", stepName = "printGreeting")
  @StepCall(globalFunctionName = "printStats", transit = "nextStepTransit")
  static void printStats(
      @In int maxIterations,
      @Nullable @In int currentIteration,
      @In(from = "username0") String username) {}

  @TransitParametersOverride(
      stepRef = {@TransitStepRefPrm(paramName = "firstStep", stepName = "printStats")},
      in = @TransitInPrm(paramName = "maxIterations", from = "maxIterations"))
  @StepCall(globalFunctionName = "printGreeting", transit = "nextIterationOrEnd")
  static void printGreeting(@In(from = "username0") String username) {}

  @TransitCall(globalFunctionName = "nextStepTransit")
  static Transition nextStepTransit(@StepRef Transition nextStep) {
    return null;
  }

  @TransitCall(globalFunctionName = "nextIterationOrEnd")
  static Transition nextIterationOrEnd(
      @In int maxIterations,
      @InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> currentIteration,
      @InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> currentDelay,
      @StepRef Transition firstStep,
      @Terminal Transition end) {
    return null;
  }
}

@FlowType(firstStep = "printStats")
class TestCallsGlobalParameterNotOverridden {
  @State private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsGlobalParameterNotOverridden(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @StepCall(globalFunctionName = "printStats", transit = "transit")
  static void printStats() {}

  @TransitFunction
  static Transition transit() {
    return null;
  }
}

@FlowType(firstStep = "printStats")
class TestCallsReturnValueMismatch {
  @State private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsReturnValueMismatch(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @StepCall(globalFunctionName = "printStats", transit = "transit")
  static int printStats() {
    return 1;
  }

  @TransitFunction
  static Transition transit() {
    return null;
  }
}

@FlowType(firstStep = "printStats5")
class TestCallsNullableReturnValueMismatch {
  @State private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsNullableReturnValueMismatch(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @StepCall(globalFunctionName = "printStats5", transit = "transit")
  static @Nullable Integer printStats5() {
    return null;
  }

  @TransitFunction
  static Transition transit() {
    return null;
  }
}

@FlowType(firstStep = "printStats")
class TestCallsNonexistentParameter {
  @State private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsNonexistentParameter(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @StepCall(globalFunctionName = "printStats", transit = "transit")
  static void printStats(
      @In int maxIterations,
      @In int currentIteration,
      @In(from = "username0") String username,
      @In int currentDelay) {}

  @TransitFunction
  static Transition transit() {
    return null;
  }
}

@FlowType(firstStep = "printStats5")
class TestCallsNullableParameterMismatch {
  @State private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsNullableParameterMismatch(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @StepCall(globalFunctionName = "printStats5", transit = "transit")
  static Integer printStats5(
      @In int maxIterations, @Nullable @In int currentIteration, @In String username) {
    return null;
  }

  @TransitFunction
  static Transition transit() {
    return null;
  }
}

@FlowType(firstStep = "printStats6")
class TestCallsInOutMismatch1 {
  @State private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsInOutMismatch1(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @StepCall(globalFunctionName = "printStats6", transit = "transit")
  static void printStats6(@InOut NullableInOutPrm<Integer> i) {}

  @TransitFunction
  static Transition transit() {
    return null;
  }
}

@FlowType(firstStep = "printStats6")
class TestCallsInOutMismatch2 {
  @State private final String username;
  @State private final int maxIterations;
  @State int currentIteration;
  @State private int currentDelay;

  public TestCallsInOutMismatch2(int maxIterations, String username) {
    this.maxIterations = maxIterations;
    this.username = username;
    currentIteration = 1;
    currentDelay = 0;
  }

  @StepCall(globalFunctionName = "printStats6", transit = "transit")
  static void printStats6(@InOut(out = Output.OPTIONAL) InOutPrm<Integer> i) {}

  @TransitFunction
  static Transition transit() {
    return null;
  }
}
