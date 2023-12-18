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

public class UniqueFlowStepRefPrmNameTest {
  /*
     +3. Step name unique within a flow;
     +    - Step;
     +    - StepCall;
     +    - Step and StepCall;
  */
  @Test
  public void testUniqueStepName() {
    Flower flower = new Flower();
    flower.registerFlow(U2_TestFlow1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Duplicate Step name for a Flow"));
  }

  @Test
  public void testUniqueStepCallName() {
    Flower flower = new Flower();
    flower.registerFlow(U2_TestFlow2.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Duplicate StepCall name for a Flow"));
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U2_TestFlow1 {
  @StepFunction(transit = "transit")
  static void step1() {}

  @StepFunction(name = "step1", transit = "transit")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U2_TestFlow2 {
  @GlobalFunction()
  static void glob() {}

  @StepCall(globalFunctionContainer = U2_TestFlow2.class, name = "step2", globalFunctionName = "glob", transit = "transit")
  static void step1() {}

  @StepCall(globalFunctionContainer = U2_TestFlow2.class, globalFunctionName = "glob", transit = "transit")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}
