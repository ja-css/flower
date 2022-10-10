package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.conf.InOutPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UniqueFunctionParameterNameTest {
  /*
     +Function Parameter names must be unique;
     +    - Step;
     +    - StepCall;
     +    - Transitioner;
     +    - TransitionerCall;
     +    - GlobalFunction;
  */
  // TODO: Also Transit parameter overrides
  @Test
  public void uniqueStepParametersTest() {
    Flower flower = new Flower();
    flower.registerFlow(U3_TestFlow1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void uniqueStepCallParametersTest() {
    Flower flower = new Flower();
    flower.registerFlow(U3_TestFlow2.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void uniqueTransitionerParametersTest() {
    Flower flower = new Flower();
    flower.registerFlow(U3_TestFlow3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void uniqueTransitionerCallParametersTest() {
    Flower flower = new Flower();
    flower.registerFlow(U3_TestFlow4.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }

  @Test
  public void uniqueGlobalFunctionParametersTest() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(U3_TestFlow5.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Conflicting ParameterNames"));
  }
}

@FlowType(name = "Test", firstStep = "step")
class U3_TestFlow1 {
  @StepFunction(transit = "transit")
  static void step(@In(name = "i") int i1, @Out(name = "i") OutPrm<Integer> i2) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "stepCall")
class U3_TestFlow2 {
  @GlobalFunction()
  static void glob(@In(name = "i1") int i1, @Out(name = "i2") OutPrm<Integer> i2) {}

  @StepCall(globalFunctionName = "glob", transit = "transit")
  static void stepCall(@In(name = "i1") int i1, @Out(name = "i1") OutPrm<Integer> i2) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step")
class U3_TestFlow3 {
  @StepFunction(transit = "transit")
  static void step() {}

  @TransitFunction()
  static Transition transit(@In(name = "i") int i1, @InOut(name = "i") InOutPrm<Integer> i2) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step")
class U3_TestFlow4 {
  @StepFunction(transit = "transit")
  static void step() {}

  @TransitCall(globalFunctionName = "glob")
  static Transition transit(@In(name = "i") int i1, @InOut(name = "i") InOutPrm<Integer> i2) {
    return null;
  }

  @GlobalFunction()
  static Transition glob(@In(name = "i1") int i1, @Out(name = "i2") OutPrm<Integer> i2) {
    return null;
  }
}

@GlobalFunctionContainer
@FlowType(name = "Test", firstStep = "step1")
class U3_TestFlow5 {
  @GlobalFunction()
  static void glob(@In(name = "i") int i1, @InOut(name = "i") InOutPrm<Integer> i2) {}

  @StepCall(globalFunctionName = "glob", transit = "transit")
  static void step1(@In int i) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}
