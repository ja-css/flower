package com.flower.parameters;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.transit.InRet;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;

public class ReturnValuesTest {
  @Test
  public void test1() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow1.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow1.class).runFlow(new R_TestFlow1());
  }

  @Test
  public void test2() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow2.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow2.class).runFlow(new R_TestFlow2());
  }

  @Test
  public void test3() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow3.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow3.class).runFlow(new R_TestFlow3());
  }

  @Test
  public void test4() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow4.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow4.class).runFlow(new R_TestFlow4());
  }

  @Test
  public void test5_1() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow5_1.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow5_1.class).runFlow(new R_TestFlow5_1());
  }

  @Test
  public void test5_2() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow5_2.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow5_2.class).runFlow(new R_TestFlow5_2());
  }

  @Test
  public void test6_1() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow6_1.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow6_1.class).runFlow(new R_TestFlow6_1());
  }

  @Test
  public void test6_2() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow6_2.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow6_2.class).runFlow(new R_TestFlow6_2());
  }

  @Test
  public void test7() {
    Flower flower = new Flower();
    flower.registerFlow(R_TestFlow7.class);
    flower.initialize();

    flower.getFlowExec(R_TestFlow7.class).runFlow(new R_TestFlow7());
  }
}

@FlowType(firstStep = "step")
class R_TestFlow1 {
  @SimpleStepFunction
  static Transition step(@Terminal Transition end) {
    System.out.println("Step");
    return end;
  }
}

@FlowType(firstStep = "step")
class R_TestFlow2 {
  @SimpleStepFunction
  static ListenableFuture<Transition> step(@Terminal Transition end) {
    System.out.println("Step");
    return Futures.immediateFuture(end);
  }
}

@FlowType(firstStep = "step")
class R_TestFlow3 {
  @State
  int i = 3;

  @StepFunction(returnTo = "i", transit = "transit")
  static int step(@Nullable @In int i) {
    System.out.println("Step" + i);
    return 5;
  }

  @TransitFunction
  static Transition transit(@In int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class R_TestFlow4 {
  @State int i = 3;

  @StepFunction(returnTo = "i", transit = "transit")
  static ListenableFuture<Integer> step(@Nullable @In int i) {
    System.out.println("Step" + i);
    return Futures.immediateFuture(5);
  }

  @TransitFunction
  static Transition transit(@In int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class R_TestFlow5_1 {
  @StepFunction(transit = "transit")
  static ListenableFuture<Integer> step() {
    return Futures.immediateFuture(5);
  }

  @TransitFunction
  static Transition transit(@InRet int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class R_TestFlow5_2 {
  @StepFunction(transit = "transit")
  static int step() {
    return 5;
  }

  @TransitFunction
  static Transition transit(@InRet int i, @Terminal Transition end) {
    System.out.println("Step" + i);
    return end;
  }
}

@FlowType(firstStep = "step")
class R_TestFlow6_1 {
  @StepFunction(transit = "transit")
  static ListenableFuture<Integer> step() {
    return Futures.immediateFuture(5);
  }

  @TransitFunction
  static Transition transit(
          @InRetOrException ReturnValueOrException ret, @Terminal Transition end) {
    System.out.println("Step" + ret.returnValue().get());
    return end;
  }
}

@FlowType(firstStep = "step")
class R_TestFlow6_2 {
  @StepFunction(transit = "transit")
  static int step() {
    return 5;
  }

  @TransitFunction
  static Transition transit(
      @InRetOrException ReturnValueOrException ret, @Terminal Transition end) {
    System.out.println("Step" + ret.returnValue().get());
    return end;
  }
}

@FlowType(firstStep = "step")
class R_TestFlow7 {
  @StepFunction(transit = "transit")
  static ListenableFuture<Integer> step() {
    return Futures.immediateFailedFuture(new Exception("5"));
  }

  @TransitFunction
  static Transition transit(
      @InRetOrException ReturnValueOrException ret, @Terminal Transition end) {
    System.out.println("Step" + ret.exception().get().toString());
    return end;
  }
}
