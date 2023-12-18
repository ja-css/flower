package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.SimpleStepCall;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.InOutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class InOutParameterCreatorGenericBase3Test {
  static Supplier<String> STRINGS() {
    return new OutParameterCreatorGenericBaseTest.Factory<>("Hello", " world!");
  }

  static Supplier<String> STRINGSD() {
    return new OutParameterCreatorGenericBaseTest.Factory<>("HelloD", " worldD!");
  }

  static Supplier<List<String>> LISTS() {
    return new OutParameterCreatorGenericBaseTest.Factory<>(
        ImmutableList.of("Hello"), ImmutableList.of(" world!"));
  }

  @Test
  void test_IN_Flow_Base_Child0() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Base.class);
    flower.registerFlow(INOUT_Flow_Base_Child0.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Base_Child0> helloWorldExec =
        flower.getFlowExec(INOUT_Flow_Base_Child0.class);
    FlowFuture<INOUT_Flow_Base_Child0> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Base_Child0(LISTS()));

    INOUT_Flow_Base_Child0 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_IN_Flow_Base_Child1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Base.class);
    flower.registerFlow(INOUT_Flow_Base_Child0.class);
    flower.registerFlow(INOUT_Flow_Base_Child1.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Base_Child1> helloWorldExec =
        flower.getFlowExec(INOUT_Flow_Base_Child1.class);
    FlowFuture<INOUT_Flow_Base_Child1> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Base_Child1(LISTS()));

    INOUT_Flow_Base_Child1 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_IN_Flow_Base_Child2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Base.class);
    flower.registerFlow(INOUT_Flow_Base_Child0.class);
    flower.registerFlow(INOUT_Flow_Base_Child1.class);
    flower.registerFlow(INOUT_Flow_Base_Child2.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Base_Child2> helloWorldExec =
        flower.getFlowExec(INOUT_Flow_Base_Child2.class);
    FlowFuture<INOUT_Flow_Base_Child2> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Base_Child2(LISTS()));

    INOUT_Flow_Base_Child2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_IN_Flow_Base_Child4() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Base.class);
    flower.registerFlow(INOUT_Flow_Base_Child4.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Base_Child4> helloWorldExec =
        flower.getFlowExec(INOUT_Flow_Base_Child4.class);
    FlowFuture<INOUT_Flow_Base_Child4> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Base_Child4(STRINGS(), "Hello", " world!"));

    INOUT_Flow_Base_Child4 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Base_Child3() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INOUT_GlobalFunctionContainer3.class);
    flower.registerFlow(INOUT_Flow_Call_Base3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("Global function parameter assumes conflicting types"));
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE2", extendz = INOUT_Flow_Base.class)
class INOUT_Flow_Base_Child0 extends INOUT_Flow_Base<List<String>> {
  public INOUT_Flow_Base_Child0(Supplier<List<String>> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE3", extendz = INOUT_Flow_Base_Child0.class)
class INOUT_Flow_Base_Child1 extends INOUT_Flow_Base_Child0 {
  public INOUT_Flow_Base_Child1(Supplier<List<String>> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = INOUT_Flow_Base_Child1.class)
class INOUT_Flow_Base_Child2 extends INOUT_Flow_Base_Child1 {
  public INOUT_Flow_Base_Child2(Supplier<List<String>> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = INOUT_Flow_Base.class)
class INOUT_Flow_Base_Child4 extends INOUT_Flow_Base<String> {
  @State
  String hello;
  @State String world;

  public INOUT_Flow_Base_Child4(Supplier<String> supplier, String hello, String world) {
    super(supplier);
    this.hello = hello;
    this.world = world;
  }
}

@GlobalFunctionContainer
class INOUT_GlobalFunctionContainer3 {
  @GlobalFunction
  static <X extends String> Transition HELLO_GLOBAL(
      @InOut InOutPrm<X> hello,
      @InOut InOutPrm<X> world,
      @In Supplier<X> supplier,
      @Terminal Transition END) {
    hello.setOutValue(supplier.get());
    world.setOutValue(supplier.get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class INOUT_Flow_Call_Base3<C extends String> {
  @State final Supplier<C> supplier;
  @State C hello;
  @State String world;

  public INOUT_Flow_Call_Base3(Supplier<C> supplier) {
    this.supplier = supplier;
  }

  @SimpleStepCall(globalFunctionContainer = INOUT_GlobalFunctionContainer3.class, globalFunctionName = "HELLO_GLOBAL")
  static <C extends String> Transition HELLO_STEP(
      @InOut InOutPrm<C> hello,
      @InOut InOutPrm<String> world,
      @In Supplier<C> supplier,
      @Terminal Transition END) {
    return null;
  }
}
