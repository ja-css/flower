package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UniqueTransitionerNameTest {
  /*
     +4. Flow's TransitionFunction unique within a flow;
     +    - Transitioner;
     +    - TransitionerCall;
     +    - Transitioner and TransitionerCall;
  */
  @Test
  public void testUniqueStepName() {
    Flower flower = new Flower();
    flower.registerFlow(U6_TestFlow1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Duplicate Step name for a Flow"));
  }

  @Test
  public void testUniqueStepCallName() {
    Flower flower = new Flower();
    flower.registerFlow(U6_TestFlow2.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Duplicate StepCall name for a Flow"));
  }

  @Test
  public void testUniqueStepAndStepCallName() {
    Flower flower = new Flower();
    flower.registerFlow(U6_TestFlow3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting Function name for a Flow"));
  }

  @Test
  public void testUniqueStepAndTransitName() {
    Flower flower = new Flower();
    flower.registerFlow(U6_TestFlow4.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting Function name for a Flow"));
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U6_TestFlow1 {
  @StepFunction(transit = "transit")
  static void step1() {}

  @StepFunction(name = "step1", transit = "transit")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step2")
class U6_TestFlow2 {
  @GlobalFunction()
  static void glob() {}

  @StepCall(name = "step2", globalFunctionName = "glob", transit = "transit")
  static void step1() {}

  @StepCall(globalFunctionName = "glob", transit = "transit")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U6_TestFlow3 {
  @GlobalFunction()
  static void glob() {}

  @StepFunction(transit = "transit")
  static void step1() {}

  @StepCall(name = "step1", globalFunctionName = "glob", transit = "transit")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U6_TestFlow4 {
  @GlobalFunction()
  static void glob() {}

  @StepFunction(transit = "transit")
  static void step1() {}

  @StepCall(name = "transit", globalFunctionName = "glob", transit = "transit2")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }

  @TransitFunction()
  static Transition transit2() {
    return null;
  }
}
