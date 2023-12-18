package com.flower.flows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.step.transitOverride.TransitStepRefPrm;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.InOutPrm;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowInheritanceTest {
  @Test
  public void testNoFirstStepParent_Error() {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlowNoFirstStep.class);
    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("Root level flow (parent in inheritance) must have firstStep defined"));
  }

  @Test
  public void testFirstStepChildOverride_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(ChildTransitOverrideAndAdd2.class);
    flower.initialize();

    FlowExec<ChildTransitOverrideAndAdd2> flowExec =
        flower.getFlowExec(ChildTransitOverrideAndAdd2.class);

    FlowFuture<ChildTransitOverrideAndAdd2> future =
        flowExec.runFlow(new ChildTransitOverrideAndAdd2());
    ChildTransitOverrideAndAdd2 child = future.getFuture().get();
    assertEquals(0, child.getParentIterations()); // we start with step 2 and step 1 doesn't execute
    assertEquals(3, child.getChildIterations());
  }

  @Test
  public void testParentFlowNotFound() {
    Flower flower = new Flower();
    flower.registerFlow(BadChild.class);
    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parent flow not found"));
  }

  @Test
  public void testChildFlowDoesNotInherit() {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(BadChild.class);
    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("must be an immediate superclass"));
  }

  @Test
  public void testInheritanceWithoutOverrides() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(Child.class);

    flower.initialize();

    FlowExec<Child> flowExec = flower.getFlowExec(Child.class);

    FlowFuture<Child> future = flowExec.runFlow(new Child());
    Child child = future.getFuture().get();

    assertEquals(6, child.getParentIterations());
  }

  @Test
  public void testInheritanceWithStateOverride() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(ChildStateFieldOverride.class);

    flower.initialize();

    FlowExec<ChildStateFieldOverride> flowExec = flower.getFlowExec(ChildStateFieldOverride.class);

    FlowFuture<ChildStateFieldOverride> future = flowExec.runFlow(new ChildStateFieldOverride());
    ChildStateFieldOverride child = future.getFuture().get();

    assertEquals(6, child.getChildIterations());
    assertEquals(0, child.getParentIterations());
  }

  @Test
  public void testChildStepOverride() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(ChildStepOverride.class);
    flower.initialize();

    FlowExec<ChildStepOverride> flowExec = flower.getFlowExec(ChildStepOverride.class);

    FlowFuture<ChildStepOverride> future = flowExec.runFlow(new ChildStepOverride());
    ChildStepOverride child = future.getFuture().get();
    assertEquals(6, child.getParentIterations());
  }

  @Test
  public void testChildTransitOverride() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(ChildTransitOverride.class);
    flower.initialize();

    FlowExec<ChildTransitOverride> flowExec = flower.getFlowExec(ChildTransitOverride.class);

    FlowFuture<ChildTransitOverride> future = flowExec.runFlow(new ChildTransitOverride());
    ChildTransitOverride child = future.getFuture().get();
    assertEquals(11, child.getParentIterations());
  }

  @Test
  public void testChildTransitOverrideDifferentType()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(ChildTransitOverrideDifferentType.class);
    flower.initialize();

    FlowExec<ChildTransitOverrideDifferentType> flowExec =
        flower.getFlowExec(ChildTransitOverrideDifferentType.class);

    FlowFuture<ChildTransitOverrideDifferentType> future =
        flowExec.runFlow(new ChildTransitOverrideDifferentType());
    ChildTransitOverrideDifferentType child = future.getFuture().get();
    assertEquals(9, child.getChildIterations());
  }

  @Test
  public void testChildTransitOverrideAndAdd() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(ChildTransitOverrideAndAdd.class);
    flower.initialize();

    FlowExec<ChildTransitOverrideAndAdd> flowExec =
        flower.getFlowExec(ChildTransitOverrideAndAdd.class);

    FlowFuture<ChildTransitOverrideAndAdd> future =
        flowExec.runFlow(new ChildTransitOverrideAndAdd());
    ChildTransitOverrideAndAdd child = future.getFuture().get();
    assertEquals(6, child.getParentIterations());
    assertEquals(3, child.getChildIterations());
  }

  @Test
  public void testReuseStaticParameters() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(ReuseStaticParameters.class);
    flower.initialize();

    FlowExec<ReuseStaticParameters> flowExec = flower.getFlowExec(ReuseStaticParameters.class);

    FlowFuture<ReuseStaticParameters> future = flowExec.runFlow(new ReuseStaticParameters());
    ReuseStaticParameters child = future.getFuture().get();
    assertEquals(6, child.getParentIterations());
  }

  @Test
  public void testOverrideStaticParameters() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ParentFlow.class);
    flower.registerFlow(OverrideStaticParameters.class);
    flower.initialize();

    FlowExec<OverrideStaticParameters> flowExec =
        flower.getFlowExec(OverrideStaticParameters.class);

    FlowFuture<OverrideStaticParameters> future = flowExec.runFlow(new OverrideStaticParameters());
    OverrideStaticParameters child = future.getFuture().get();
    assertEquals(3, child.getParentIterations());
  }

  // +State fields override
  // +Override state field with the same name and type
  // +Override state field with the same name and a different type
  // +   - Possible, but its hard to override methods with InOut

  // Function/call override and parameter override
  // Override function with a call
  // Override a call with a function
  // +Override a transit parameter

  // +Adding new steps / functions / reroute

  //    +- Functions/Calls are always inherited
  //    +- Functions/Calls can be overridden (by name, with the same signature)
  //    +- new Functions/Calls can be added
  //
  //    +- State fields are inherited since Java inheritance is in place
  //    +- State fields can be overridden (by name)
  //    +- new State fields can be added
}

@FlowType(name = "ParentFlow", firstStep = "step")
class ParentFlow {
  @State private static final int MAX_ATTEMPTS = 5;

  @State private String message;
  @State private int iteration;

  public int getParentIterations() {
    return iteration;
  }

  @StepFunction(transit = "transit")
  static void step(@Nullable @In String message, @Nullable @In int iteration) {
    System.out.printf("Hello from Parent [%s] iteration [%d]%n", message, iteration);
  }

  @TransitFunction()
  static Transition transit(
      @StepRef Transition step,
      @Terminal Transition end,
      @InOut NullableInOutPrm<Integer> iteration,
      @In int MAX_ATTEMPTS) {
    int iter = iteration.getInValue();
    iteration.setOutValue(iter + 1);
    if (iter < MAX_ATTEMPTS) return step.setDelay(Duration.ofMillis(iter * 50));
    else return end;
  }
}

@FlowType(extendz = ParentFlow.class)
class BadChild {}

@FlowType(extendz = ParentFlow.class)
class Child extends ParentFlow {}

@FlowType(extendz = ParentFlow.class)
class ChildStateFieldOverride extends ParentFlow {
  @State private int iteration;

  public int getChildIterations() {
    return iteration;
  }
}

// Override existing step
@FlowType(name = "ChildStepOverride", extendz = ParentFlow.class)
class ChildStepOverride extends ParentFlow {
  @StepFunction(transit = "transit")
  static void step(@Nullable @In String message, @Nullable @In int iteration) {
    System.out.printf("Hello from ChildFlow [%s] iteration [%d]%n", message, iteration);
  }
}

// Override existing transit
@FlowType(name = "ChildTransitOverride", extendz = ParentFlow.class)
class ChildTransitOverride extends ParentFlow {
  private static final int MAX_ATTEMPTS = 10;

  @TransitFunction()
  static Transition transit(
      @StepRef Transition step,
      @Terminal Transition end,
      @InOut NullableInOutPrm<Integer> iteration) {
    int iter = iteration.getInValue();
    iteration.setOutValue(iter + 1);
    if (iter < MAX_ATTEMPTS) return step.setDelay(Duration.ofMillis(iter * 50));
    else return end;
  }
}

// Override existing transit
@FlowType(name = "ChildTransitOverride", extendz = ParentFlow.class)
class ChildTransitOverrideDifferentType extends ParentFlow {
  private static final int MAX_ATTEMPTS = 8;

  @State private long iteration;

  public long getChildIterations() {
    return iteration;
  }

  @StepFunction(transit = "transit")
  static void step(@Nullable @In String message, @Nullable @In long iteration) {
    System.out.printf(
        "Hello from ChildTransitOverrideDifferentType [%s] iteration [%d]%n", message, iteration);
  }

  @TransitFunction()
  static Transition transit(
      @StepRef Transition step,
      @Terminal Transition end,
      @Nullable @In Long iteration,
      @Out(to = "iteration") OutPrm<Long> iterOut) {
    iterOut.setOutValue(iteration + 1);
    if (iteration < MAX_ATTEMPTS) return step.setDelay(Duration.ofMillis(iteration * 50));
    else return end;
  }
}

// Override existing transit
@FlowType(name = "ChildTransitOverride", extendz = ParentFlow.class)
class ChildTransitOverrideAndAdd extends ParentFlow {
  private static final int MAX_ATTEMPTS = 2;

  @State
  private long iteration2;

  public long getChildIterations() {
    return iteration2;
  }

  @TransitStepRefPrm(paramName = "end", stepName = "step2")
  @StepFunction(transit = "transit")
  static void step(@Nullable @In String message, @Nullable @In int iteration) {
    ParentFlow.step(message, iteration);
  }

  @StepFunction(transit = "transit2")
  static void step2(@Nullable @In String message, @Nullable @In long iteration2) {
    System.out.printf(
        "Hello from ChildTransitOverrideAndAdd step2 [%s] iteration [%d]%n", message, iteration2);
  }

  @TransitFunction()
  static Transition transit2(
      @StepRef Transition step2,
      @Terminal Transition end,
      @InOut NullableInOutPrm<Long> iteration2) {
    long iter = iteration2.getInValue();
    iteration2.setOutValue(iter + 1);
    if (iter < MAX_ATTEMPTS) return step2.setDelay(Duration.ofMillis(iter * 50));
    else return end;
  }
}

// Reuse static parameter
@FlowType(name = "ReuseChildStep", extendz = ParentFlow.class)
class ReuseStaticParameters extends ParentFlow {
  @StepFunction(transit = "transit")
  static void step(
      @Nullable @In String message, @Nullable @In int iteration, @In int MAX_ATTEMPTS) {
    System.out.printf(
        "Hello from ChildFlow [%s] iteration [%d] MAX_ATTEMPTS [%d] %n",
        message, iteration, MAX_ATTEMPTS);
  }
}

// Override static parameter
@FlowType(name = "OverrideStaticParameters", extendz = ParentFlow.class)
class OverrideStaticParameters extends ParentFlow {
  @State private static final int MAX_ATTEMPTS = 2;

  @StepFunction(transit = "transit")
  static void step(
      @Nullable @In String message, @Nullable @In int iteration, @In int MAX_ATTEMPTS) {
    System.out.printf(
        "Hello from ChildFlow [%s] iteration [%d] MAX_ATTEMPTS [%d] %n",
        message, iteration, MAX_ATTEMPTS);
  }
}

@FlowType(name = "ParentFlowNoFirstStep")
class ParentFlowNoFirstStep {
  @State private static final int MAX_ATTEMPTS = 5;

  @State private String message;
  @State private int iteration;

  public int getParentIterations() {
    return iteration;
  }

  @StepFunction(transit = "transit")
  static void step(@In String message, @In int iteration) {
    System.out.printf("Hello from Parent [%s] iteration [%d]%n", message, iteration);
  }

  @TransitFunction()
  static Transition transit(
      @StepRef Transition step,
      @Terminal Transition end,
      @InOut InOutPrm<Integer> iteration,
      @In int MAX_ATTEMPTS) {
    int iter = iteration.getInValue();
    iteration.setOutValue(iter + 1);
    if (iter < MAX_ATTEMPTS) return step.setDelay(Duration.ofMillis(iter * 50));
    else return end;
  }
}

@FlowType(name = "ChildTransitOverride", extendz = ParentFlow.class, firstStep = "step2")
class ChildTransitOverrideAndAdd2 extends ParentFlow {
  private static final int MAX_ATTEMPTS = 2;

  @State private long iteration2;

  public long getChildIterations() {
    return iteration2;
  }

  @TransitStepRefPrm(paramName = "end", stepName = "step2")
  @StepFunction(transit = "transit")
  static void step(@In String message, @Nullable @In int iteration) {
    ParentFlow.step(message, iteration);
  }

  @StepFunction(transit = "transit2")
  static void step2(@Nullable @In String message, @Nullable @In long iteration2) {
    System.out.printf(
        "Hello from ChildTransitOverrideAndAdd step2 [%s] iteration [%d]%n", message, iteration2);
  }

  @TransitFunction()
  static Transition transit2(
      @StepRef Transition step2,
      @Terminal Transition end,
      @InOut NullableInOutPrm<Long> iteration2) {
    long iter = iteration2.getInValue();
    iteration2.setOutValue(iter + 1);
    if (iter < MAX_ATTEMPTS) return step2.setDelay(Duration.ofMillis(iter * 50));
    else return end;
  }
}
