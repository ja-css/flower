package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class ReturnToParameterCreatorGenericBase3Test {
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
  void test_RETTO_Flow_Base_Child0() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base.class);
    flower.registerFlow(RETTO_Flow_Base_Child0.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Base_Child0> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Base_Child0.class);
    FlowFuture<RETTO_Flow_Base_Child0> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Base_Child0(LISTS()));

    RETTO_Flow_Base_Child0 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
  }

  @Test
  void test_RETTO_Flow_Base_Child1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base.class);
    flower.registerFlow(RETTO_Flow_Base_Child0.class);
    flower.registerFlow(RETTO_Flow_Base_Child1.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Base_Child1> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Base_Child1.class);
    FlowFuture<RETTO_Flow_Base_Child1> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Base_Child1(LISTS()));

    RETTO_Flow_Base_Child1 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
  }

  @Test
  void test_RETTO_Flow_Base_Child2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base.class);
    flower.registerFlow(RETTO_Flow_Base_Child0.class);
    flower.registerFlow(RETTO_Flow_Base_Child1.class);
    flower.registerFlow(RETTO_Flow_Base_Child2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Base_Child2> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Base_Child2.class);
    FlowFuture<RETTO_Flow_Base_Child2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Base_Child2(LISTS()));

    RETTO_Flow_Base_Child2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
  }

  @Test
  void test_RETTO_Flow_Base_Child4() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base.class);
    flower.registerFlow(RETTO_Flow_Base_Child4.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Base_Child4> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Base_Child4.class);
    FlowFuture<RETTO_Flow_Base_Child4> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Base_Child4(STRINGS(), "Hello", " world!"));

    RETTO_Flow_Base_Child4 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_RETTO_Flow_Base_Child3() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer3.class);
    flower.registerFlow(RETTO_Flow_Call_Base3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("Global function parameter assumes conflicting types"));
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE2", extendz = "BASE")
class RETTO_Flow_Base_Child0 extends RETTO_Flow_Base<List<String>> {
  public RETTO_Flow_Base_Child0(Supplier<List<String>> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE3", extendz = "BASE2")
class RETTO_Flow_Base_Child1 extends RETTO_Flow_Base_Child0 {
  public RETTO_Flow_Base_Child1(Supplier<List<String>> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = "BASE3")
class RETTO_Flow_Base_Child2 extends RETTO_Flow_Base_Child1 {
  public RETTO_Flow_Base_Child2(Supplier<List<String>> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = "BASE")
class RETTO_Flow_Base_Child4 extends RETTO_Flow_Base<String> {
  @State
  String hello;
  @State String world;

  public RETTO_Flow_Base_Child4(Supplier<String> supplier, String hello, String world) {
    super(supplier);
    this.hello = hello;
    this.world = world;
  }
}

@GlobalFunctionContainer
class RETTO_GlobalFunctionContainer3 {
  @GlobalFunction
  static <X extends String> X HELLO_GLOBAL(@In Supplier<X> supplier) {
    return supplier.get();
  }
}

@FlowType(firstStep = "HELLO_STEP1")
class RETTO_Flow_Call_Base3<C extends String> {
  @State final Supplier<C> supplier;
  @State C hello;
  @State String world;

  public RETTO_Flow_Call_Base3(Supplier<C> supplier) {
    this.supplier = supplier;
  }

  @StepCall(globalFunctionName = "HELLO_GLOBAL", transit = "TRANSIT1", returnTo = "hello")
  static <C extends String> C HELLO_STEP1(@In Supplier<C> supplier) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT1(@StepRef Transition HELLO_STEP2) {
    return HELLO_STEP2;
  }

  @StepCall(globalFunctionName = "HELLO_GLOBAL", transit = "TRANSIT2", returnTo = "world")
  static <C extends String> String HELLO_STEP2(@In Supplier<C> supplier) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT2(@Terminal Transition END) {
    return END;
  }
}
