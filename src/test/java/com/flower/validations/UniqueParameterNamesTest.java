package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UniqueParameterNamesTest {
  @Test
  public void testStepFunction() {
    Flower flower = new Flower();
    flower.registerFlow(U5_TestFlow1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void testTransitionFunction() {
    Flower flower = new Flower();
    flower.registerFlow(U5_TestFlow2.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void testGlobalFunction() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(U5_Container1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void testStepCall() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(U5_Container2.class);
    flower.registerFlow(U5_TestFlow3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void testTransitCall() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(U5_Container2.class);
    flower.registerFlow(U5_TestFlow4.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U5_TestFlow1 {
  @State int i1;
  @State int i2;

  @StepFunction(transit = "transit")
  static void step1(@In(name = "i", from = "i1") int i1, @In(name = "i", from = "i2") int i2) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U5_TestFlow2 {
  @State int i1;
  @State int i2;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(
      @In(name = "i", from = "i1") int i1, @In(name = "i", from = "i2") int i2) {
    return null;
  }
}

@GlobalFunctionContainer(name = "Container1")
class U5_Container1 {
  @GlobalFunction
  static void step1(@In(name = "i", from = "i1") int i1, @In(name = "i", from = "i2") int i2) {}
}

@GlobalFunctionContainer(name = "Container2")
class U5_Container2 {
  @GlobalFunction
  static Transition func1(
      @In(name = "i1", from = "i1") int i1, @In(name = "i2", from = "i2") int i2) {
    throw new UnsupportedOperationException();
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U5_TestFlow3 {
  @State
  int i1;
  @State int i2;

  @StepCall(globalFunctionContainer = U5_Container2.class, globalFunctionName = "func1", transit = "transit")
  static void step1(@In(name = "i", from = "i1") int i1, @In(name = "i", from = "i2") int i2) {}

  @TransitFunction()
  static Transition transit() {
    throw new UnsupportedOperationException();
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U5_TestFlow4 {
  @State int i1;
  @State int i2;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitCall(globalFunctionContainer = U5_Container2.class, globalFunctionName = "func1")
  static void transit(@In(name = "i", from = "i1") int i1, @In(name = "i", from = "i2") int i2) {}
}
