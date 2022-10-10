package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransitFunctionsTest {
  @Test
  public void transitReturnValueTest1() {
    Flower flower = new Flower();
    flower.registerFlow(T1_TestFlow1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value type mismatch"));
  }

  @Test
  public void transitReturnValueTest2() {
    Flower flower = new Flower();
    flower.registerFlow(T1_TestFlow2.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value type mismatch"));
  }

  @Test
  public void transitReturnValueTest3() {
    Flower flower = new Flower();
    flower.registerFlow(T1_TestFlow3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value type mismatch"));
  }

  @Test
  public void transitReturnValueTest4() {
    Flower flower = new Flower();
    flower.registerFlow(T1_TestFlow4.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value type mismatch"));
  }

  @Test
  public void transitReturnValueTest5() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions.class);
    flower.registerFlow(T1_TestFlow5.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("@ReturnTo or return value parameter type mismatch"));
  }

  @Test
  public void transitReturnValueTest6() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions.class);
    flower.registerFlow(T1_TestFlow6.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("@ReturnTo or return value parameter type mismatch"));
  }

  @Test
  public void transitReturnValueTest7() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions.class);
    flower.registerFlow(T1_TestFlow7.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value type mismatch"));
  }

  @Test
  public void transitReturnValueTest8() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions.class);
    flower.registerFlow(T1_TestFlow8.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value type mismatch"));
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T1_TestFlow1 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitFunction()
  static void transit() {}
}

@FlowType(name = "Test", firstStep = "transit")
class T1_TestFlow2 {
  @SimpleStepFunction
  static void transit() {}
}

@FlowType(name = "Test", firstStep = "step2")
class T1_TestFlow3 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitFunction()
  static ListenableFuture<Integer> transit() {
    return Futures.immediateFuture(5);
  }
}

@FlowType(name = "Test", firstStep = "transit")
class T1_TestFlow4 {
  @SimpleStepFunction
  static ListenableFuture<Integer> transit() {
    return Futures.immediateFuture(5);
  }
}

@GlobalFunctionContainer
class GlobalFunctions {
  @GlobalFunction
  static void global1() {}

  @GlobalFunction
  static ListenableFuture<Integer> global2() {
    return Futures.immediateFuture(5);
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T1_TestFlow5 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitCall(globalFunctionName = "global1")
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T1_TestFlow6 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitCall(globalFunctionName = "global2")
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T1_TestFlow7 {
  @StepFunction(globalTransit = "global1")
  static void step2() {}
}

@FlowType(name = "Test", firstStep = "step2")
class T1_TestFlow8 {
  @StepFunction(globalTransit = "global2")
  static void step2() {}
}
