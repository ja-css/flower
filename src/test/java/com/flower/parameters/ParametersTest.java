package com.flower.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.common.Output;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.InOutPrm;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParametersTest {
  @Test
  public void test1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(P_TestFlow1.class);
    flower.initialize();

    Assertions.assertEquals(
        5, flower.getFlowExec(P_TestFlow1.class).runFlow(new P_TestFlow1()).getFuture().get().i);
  }

  @Test
  public void test2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(P_TestFlow2.class);
    flower.initialize();

    Assertions.assertEquals(
        5, flower.getFlowExec(P_TestFlow2.class).runFlow(new P_TestFlow2()).getFuture().get().i);
  }

  @Test
  public void test3() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(P_TestFlow3.class);
    flower.initialize();

    Assertions.assertEquals(
        5, flower.getFlowExec(P_TestFlow3.class).runFlow(new P_TestFlow3()).getFuture().get().i);
  }

  @Test
  public void test4() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(P_TestFlow4.class);
    flower.initialize();

    Assertions.assertEquals(
        5, flower.getFlowExec(P_TestFlow4.class).runFlow(new P_TestFlow4()).getFuture().get().i);
  }

  @Test
  public void test5() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(P_TestFlow5.class);
    flower.initialize();

    Assertions.assertEquals(
        5, flower.getFlowExec(P_TestFlow5.class).runFlow(new P_TestFlow5()).getFuture().get().i);
  }

  @Test
  public void test6() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(P_TestFlow6.class);
    flower.initialize();

    Assertions.assertEquals(
        5, flower.getFlowExec(P_TestFlow6.class).runFlow(new P_TestFlow6()).getFuture().get().i);
  }
}

@FlowType(firstStep = "step")
class P_TestFlow1 {
  @State int i = 3;

  @StepFunction(transit = "transit")
  static void step(@InOut NullableInOutPrm<Integer> i) {
    System.out.println("Step" + i.getInValue());
    i.setOutValue(5);
  }

  @TransitFunction
  static Transition transit(@In int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class P_TestFlow2 {
  @State
  int i = 3;

  @StepFunction(transit = "transit")
  static void step(@InOut NullableInOutPrm<Integer> i) {
    System.out.println("Step" + i.getInValue());
    i.setOutFuture(Futures.immediateFuture(5));
  }

  @TransitFunction
  static Transition transit(@In int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class P_TestFlow3 {
  @State int i;

  @StepFunction(transit = "transit")
  static void step(@Out OutPrm<Integer> i) {
    i.setOutValue(5);
  }

  @TransitFunction
  static Transition transit(@In int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class P_TestFlow4 {
  @State int i;

  @StepFunction(transit = "transit")
  static void step(@Out OutPrm<Integer> i) {
    i.setOutFuture(Futures.immediateFuture(5));
  }

  @TransitFunction
  static Transition transit(@In int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class P_TestFlow5 {
  @State int i = 3;

  @StepFunction(transit = "transit")
  static void step(@InOut NullableInOutPrm<Integer> i) {
    System.out.println("Step" + i.getInValue());
    i.setOutValue(5);
  }

  @TransitFunction
  static Transition transit(@InOut(out=Output.OPTIONAL) InOutPrm<Integer> i, @Terminal Transition end) {
    System.out.println("Step" + i.getInValue());
    return end;
  }
}

@FlowType(firstStep = "init")
class P_TestFlow6 {
  @State int i;

  @SimpleStepFunction
  static Transition init(@Out OutPrm<Integer> i, @StepRef Transition step) {
    i.setOutValue(3);
    return step;
  }

  @StepFunction(transit = "transit")
  static void step(@InOut InOutPrm<Integer> i) {
    System.out.println("Step" + i.getInValue());
    i.setOutValue(5);
  }

  @TransitFunction
  static Transition transit(@InOut(out=Output.OPTIONAL) InOutPrm<Integer> i, @Terminal Transition end) {
    System.out.println("Step" + i.getInValue());
    return end;
  }
}
