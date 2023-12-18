package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.params.common.In;
import com.flower.anno.params.transit.InRet;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class InRetParameterCreatorGenericBase3Test {
  @Test
  void test_INRET_Flow_Base_Child0() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base.class);
    flower.registerFlow(INRET_Flow_Base_Child0.class);
    flower.initialize();

    FlowExec<INRET_Flow_Base_Child0> helloWorldExec =
        flower.getFlowExec(INRET_Flow_Base_Child0.class);
    FlowFuture<INRET_Flow_Base_Child0> flowFuture =
        helloWorldExec.runFlow(
            new INRET_Flow_Base_Child0(ImmutableList.of("Hello"), ImmutableList.of(" world!")));

    INRET_Flow_Base_Child0 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_INRET_Flow_Base_Child1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base.class);
    flower.registerFlow(INRET_Flow_Base_Child0.class);
    flower.registerFlow(INRET_Flow_Base_Child1.class);
    flower.initialize();

    FlowExec<INRET_Flow_Base_Child1> helloWorldExec =
        flower.getFlowExec(INRET_Flow_Base_Child1.class);
    FlowFuture<INRET_Flow_Base_Child1> flowFuture =
        helloWorldExec.runFlow(
            new INRET_Flow_Base_Child1(ImmutableList.of("Hello"), ImmutableList.of(" world!")));

    INRET_Flow_Base_Child1 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_INRET_Flow_Base_Child2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base.class);
    flower.registerFlow(INRET_Flow_Base_Child0.class);
    flower.registerFlow(INRET_Flow_Base_Child1.class);
    flower.registerFlow(INRET_Flow_Base_Child2.class);
    flower.initialize();

    FlowExec<INRET_Flow_Base_Child2> helloWorldExec =
        flower.getFlowExec(INRET_Flow_Base_Child2.class);
    FlowFuture<INRET_Flow_Base_Child2> flowFuture =
        helloWorldExec.runFlow(
            new INRET_Flow_Base_Child2(ImmutableList.of("Hello"), ImmutableList.of(" world!")));

    INRET_Flow_Base_Child2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_INRET_Flow_Base_Child4() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base.class);
    flower.registerFlow(INRET_Flow_Base_Child4.class);
    flower.initialize();

    FlowExec<INRET_Flow_Base_Child4> helloWorldExec =
        flower.getFlowExec(INRET_Flow_Base_Child4.class);
    FlowFuture<INRET_Flow_Base_Child4> flowFuture =
        helloWorldExec.runFlow(new INRET_Flow_Base_Child4("Hello", " world!", "Hello", " world!"));

    INRET_Flow_Base_Child4 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRET_Flow_Base_Child3() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRET_GlobalFunctionContainer3.class);
    flower.registerFlow(INRET_Flow_Call_Base3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("Global function parameter assumes conflicting types"));
  }

  @Test
  void test_INRET_Flow_Base_Child3_1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRET_GlobalFunctionContainer3.class);
    flower.registerFlow(INRET_Flow_Call_Base3_1.class);
    flower.initialize();

    FlowExec<INRET_Flow_Call_Base3_1> helloWorldExec =
        flower.getFlowExec(INRET_Flow_Call_Base3_1.class);
    FlowFuture<INRET_Flow_Call_Base3_1> flowFuture =
        helloWorldExec.runFlow(new INRET_Flow_Call_Base3_1("Hello", " world!"));

    INRET_Flow_Call_Base3_1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE2", extendz = INRET_Flow_Base.class)
class INRET_Flow_Base_Child0 extends INRET_Flow_Base<List<String>> {
  public INRET_Flow_Base_Child0(List<String> hello, List<String> world) {
    super(hello, world);
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE3", extendz = INRET_Flow_Base_Child0.class)
class INRET_Flow_Base_Child1 extends INRET_Flow_Base_Child0 {
  public INRET_Flow_Base_Child1(List<String> hello, List<String> world) {
    super(hello, world);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = INRET_Flow_Base_Child1.class)
class INRET_Flow_Base_Child2 extends INRET_Flow_Base_Child1 {
  public INRET_Flow_Base_Child2(List<String> hello, List<String> world) {
    super(hello, world);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = INRET_Flow_Base.class)
class INRET_Flow_Base_Child4 extends INRET_Flow_Base<String> {
  @State final String hello;
  @State final String world;

  public INRET_Flow_Base_Child4(String hello0, String world0, String hello, String world) {
    super(hello0, world0);
    this.hello = hello;
    this.world = world;
  }
}

@GlobalFunctionContainer
class INRET_GlobalFunctionContainer3 {
  @GlobalFunction
  static <X extends String> X HELLO_GLOBAL(@In X hello, @In X world) {
    System.out.println(hello + " " + world);
    return hello;
  }

  @GlobalFunction
  static <X extends String> Transition HELLO_TRANSIT(
      @InRet X helloRet, @In X world, @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class INRET_Flow_Call_Base3<C extends String> {
  @State
  final C hello;
  @State final String world;

  public INRET_Flow_Call_Base3(C hello, String world) {
    this.hello = hello;
    this.world = world;
  }

  @StepCall(globalFunctionContainer = INRET_GlobalFunctionContainer3.class, globalFunctionName = "HELLO_GLOBAL", transit = "HELLO_TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In String world) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRET_GlobalFunctionContainer3.class, globalFunctionName = "HELLO_TRANSIT")
  static <C extends String> Transition HELLO_TRANSIT(
      @InRet C helloRet, @In String world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class INRET_Flow_Call_Base3_1<C extends String> {
  @State final C hello;
  @State final C world;

  public INRET_Flow_Call_Base3_1(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepCall(globalFunctionContainer = INRET_GlobalFunctionContainer3.class, globalFunctionName = "HELLO_GLOBAL", transit = "HELLO_TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    return hello;
  }

  @TransitCall(globalFunctionContainer = INRET_GlobalFunctionContainer3.class, globalFunctionName = "HELLO_TRANSIT")
  static <C extends String> Transition HELLO_TRANSIT(
      @InRet C helloRet, @In C world, @Terminal Transition END) {
    return null;
  }
}
