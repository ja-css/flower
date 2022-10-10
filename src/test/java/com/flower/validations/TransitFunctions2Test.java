package com.flower.validations;

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
import org.junit.jupiter.api.Test;

public class TransitFunctions2Test {
  @Test
  public void transitReturnValueTest1() {
    Flower flower = new Flower();
    flower.registerFlow(T2_TestFlow1.class);
    flower.initialize();
  }

  @Test
  public void transitReturnValueTest2() {
    Flower flower = new Flower();
    flower.registerFlow(T2_TestFlow2.class);
    flower.initialize();
  }

  @Test
  public void transitReturnValueTest3() {
    Flower flower = new Flower();
    flower.registerFlow(T2_TestFlow3.class);
    flower.initialize();
  }

  @Test
  public void transitReturnValueTest4() {
    Flower flower = new Flower();
    flower.registerFlow(T2_TestFlow4.class);
    flower.initialize();
  }

  @Test
  public void transitReturnValueTest5() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions2.class);
    flower.registerFlow(T2_TestFlow5.class);
    flower.initialize();
  }

  @Test
  public void transitReturnValueTest6() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions2.class);
    flower.registerFlow(T2_TestFlow6.class);
    flower.initialize();
  }

  @Test
  public void transitReturnValueTest7() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions2.class);
    flower.registerFlow(T2_TestFlow7.class);
    flower.initialize();
  }

  @Test
  public void transitReturnValueTest8() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(GlobalFunctions2.class);
    flower.registerFlow(T2_TestFlow8.class);
    flower.initialize();
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T2_TestFlow1 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class T2_TestFlow2 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T2_TestFlow3 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitFunction()
  static ListenableFuture<Transition> transit() {
    return Futures.immediateFuture(null);
  }
}

@FlowType(name = "Test", firstStep = "transit")
class T2_TestFlow4 {
  @SimpleStepFunction
  static ListenableFuture<Transition> transit() {
    return Futures.immediateFuture(null);
  }
}

@GlobalFunctionContainer
class GlobalFunctions2 {
  @GlobalFunction
  static Transition global1() {
    return null;
  }

  @GlobalFunction
  static ListenableFuture<Transition> global2() {
    return Futures.immediateFuture(null);
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T2_TestFlow5 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitCall(globalFunctionName = "global1")
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T2_TestFlow6 {
  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitCall(globalFunctionName = "global2")
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step2")
class T2_TestFlow7 {
  @StepFunction(globalTransit = "global1")
  static void step2() {}
}

@FlowType(name = "Test", firstStep = "step2")
class T2_TestFlow8 {
  @StepFunction(globalTransit = "global2")
  static void step2() {}
}
