package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
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

public class ReturnToParameterCreatorGenericBase2Test {
  static Supplier<String> STRINGS() {
    return new OutParameterCreatorGenericBaseTest.Factory<>("Hello", " world!", "here");
  }

  static Supplier<String> STRINGSD() {
    return new OutParameterCreatorGenericBaseTest.Factory<>("HelloD", " worldD!", "hereD");
  }

  static Supplier<List<String>> LISTS() {
    return new OutParameterCreatorGenericBaseTest.Factory<>(
        ImmutableList.of("Hello"), ImmutableList.of(" world!"), ImmutableList.of("hereD"));
  }

  @Test
  void test_RETTO_Flow_Gen1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Gen1.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Gen1> helloWorldExec = flower.getFlowExec(RETTO_Flow_Gen1.class);
    FlowFuture<RETTO_Flow_Gen1> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Gen1<>(STRINGS(), STRINGSD()));

    RETTO_Flow_Gen1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.str);
    assertEquals(" worldD!", state.world);
  }

  @Test
  void test_RETTO_Flow_Gen1_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Gen1.class);
    flower.registerFlow(RETTO_Flow_Gen1_2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Gen1_2> helloWorldExec = flower.getFlowExec(RETTO_Flow_Gen1_2.class);
    FlowFuture<RETTO_Flow_Gen1_2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Gen1_2<>(STRINGS(), STRINGSD()));

    RETTO_Flow_Gen1_2 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.str);
    assertEquals(" worldD!", state.world);
  }

  @Test
  void test_RETTO_Flow_Gen2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Gen2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Gen2> helloWorldExec = flower.getFlowExec(RETTO_Flow_Gen2.class);
    FlowFuture<RETTO_Flow_Gen2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Gen2(LISTS(), STRINGSD()));

    RETTO_Flow_Gen2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.str);
    assertEquals(ImmutableList.of("Hello"), state.list);
    assertEquals(ImmutableList.of("hereD"), state.hello);
  }

  @Test
  void test_RETTO_Flow_Gen2_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Gen2.class);
    flower.registerFlow(RETTO_Flow_Gen2_2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Gen2_2> helloWorldExec = flower.getFlowExec(RETTO_Flow_Gen2_2.class);
    FlowFuture<RETTO_Flow_Gen2_2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Gen2_2(LISTS(), STRINGSD()));

    RETTO_Flow_Gen2_2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.str);
    assertEquals(ImmutableList.of("Hello"), state.list);
    assertEquals(ImmutableList.of("hereD"), state.hello);
  }

  // --------------------------------------------------------------------------

  @Test
  void test_RETTO_Flow_Gen_GlobalFunctionCall1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer2_1.class);
    flower.registerFlow(RETTO_Flow_Gen_GlobalFunctionCall1.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Gen_GlobalFunctionCall1> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Gen_GlobalFunctionCall1.class);
    FlowFuture<RETTO_Flow_Gen_GlobalFunctionCall1> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Gen_GlobalFunctionCall1<>(STRINGS(), STRINGSD()));

    RETTO_Flow_Gen_GlobalFunctionCall1 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.str);
    assertEquals("here", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_RETTO_Flow_Gen_GlobalFunctionCall2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer2_2.class);
    flower.registerFlow(RETTO_Flow_Gen_GlobalFunctionCall2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Gen_GlobalFunctionCall2> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Gen_GlobalFunctionCall2.class);
    FlowFuture<RETTO_Flow_Gen_GlobalFunctionCall2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Gen_GlobalFunctionCall2<>(LISTS(), STRINGS()));

    RETTO_Flow_Gen_GlobalFunctionCall2 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.str);
    assertEquals(ImmutableList.of("Hello"), state.list);
    assertEquals(ImmutableList.of("hereD"), state.hello);
  }

  @Test
  void test_RETTO_Flow_Gen_GlobalTransitionerCall2()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer2_1.class);
    flower.registerFlow(RETTO_Flow_Gen_GlobalFunctionCall1.class);
    flower.registerFlow(RETTO_Flow_Gen_GlobalTransitionerCall2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Gen_GlobalTransitionerCall2> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Gen_GlobalTransitionerCall2.class);
    FlowFuture<RETTO_Flow_Gen_GlobalTransitionerCall2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Gen_GlobalTransitionerCall2<>(STRINGS(), STRINGSD()));

    RETTO_Flow_Gen_GlobalTransitionerCall2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.str);
    assertEquals("here", state.hello);
    assertEquals(" world!", state.world);
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class RETTO_Flow_Gen1<C extends X, X extends String> {
  @State
  String str;
  @State C hello;
  @State X world;

  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  public RETTO_Flow_Gen1(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @StepFunction(transit = "TRANSIT1", returnTo = "str")
  static <X extends String> X STEP1(@In Supplier<X> supplierX) {
    return supplierX.get();
  }

  @TransitFunction
  static <X extends String> Transition TRANSIT1(@StepRef Transition STEP2) {
    return STEP2;
  }

  @StepFunction(transit = "TRANSIT2", returnTo = "str")
  static <C extends X, X extends String> C STEP2(@In Supplier<C> supplierC) {
    return supplierC.get();
  }

  @TransitFunction
  static <C extends X, X extends String> Transition TRANSIT2(@StepRef Transition STEP3) {
    return STEP3;
  }

  @StepFunction(transit = "TRANSIT3", returnTo = "world")
  static <C extends X, X extends String> C STEP3(@In Supplier<C> supplierC) {
    return supplierC.get();
  }

  @TransitFunction
  static <C extends X, X extends String> Transition TRANSIT3(@StepRef Transition STEP4) {
    return STEP4;
  }

  @StepFunction(transit = "TRANSIT4", returnTo = "world")
  static <X extends String> X STEP4(@In Supplier<X> supplierX) {
    return supplierX.get();
  }

  @TransitFunction
  static <C extends X, X extends String> Transition TRANSIT4(@Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", extendz = "BASE")
class RETTO_Flow_Gen1_2<C2 extends X2, X2 extends String> extends RETTO_Flow_Gen1<C2, X2> {
  @State C2 hello;
  @State X2 world;

  public RETTO_Flow_Gen1_2(Supplier<C2> supplierC, Supplier<X2> supplierX) {
    super(supplierC, supplierX);
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class RETTO_Flow_Gen2<C extends List<X>, X extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  @State String str;
  @State List<String> list;
  @State C hello;
  @State X world;

  public RETTO_Flow_Gen2(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @StepFunction(transit = "TRANSITION1", returnTo = "str")
  static <X extends String> X STEP1(@In Supplier<X> supplierX) {
    return supplierX.get();
  }

  @TransitFunction
  static <X extends String> Transition TRANSITION1(@StepRef Transition STEP2) {
    return STEP2;
  }

  @StepFunction(transit = "TRANSITION2", returnTo = "list")
  static <C extends List<X>, X extends String> C STEP2(@In Supplier<C> supplierC) {
    return supplierC.get();
  }

  @TransitFunction
  static <C extends List<X>, X extends String> Transition TRANSITION2(@StepRef Transition STEP3) {
    return STEP3;
  }

  @StepFunction(transit = "TRANSITION3", returnTo = "hello")
  static <C extends List<X>, X extends String> C STEP3(@In Supplier<C> supplierC) {
    return supplierC.get();
  }

  @TransitFunction
  static <C extends List<X>, X extends String> Transition TRANSITION3(@StepRef Transition STEP4) {
    return STEP4;
  }

  @StepFunction(transit = "TRANSITION4", returnTo = "hello")
  static <C extends List<X>, X extends String> C STEP4(@In Supplier<C> supplierC) {
    return supplierC.get();
  }

  @TransitFunction
  static <C extends List<X>, X extends String> Transition TRANSITION4(@Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", extendz = "BASE")
class RETTO_Flow_Gen2_2<C2 extends List<X2>, X2 extends String> extends RETTO_Flow_Gen2<C2, X2> {
  @State C2 hello;
  @State X2 world;

  public RETTO_Flow_Gen2_2(Supplier<C2> supplierC, Supplier<X2> supplierX) {
    super(supplierC, supplierX);
  }
}

@GlobalFunctionContainer
class RETTO_GlobalFunctionContainer2_1 {
  @GlobalFunction
  static <A extends String> A STEP1(@In Supplier<A> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <B extends G, G extends String> B STEP2(@In Supplier<B> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <B extends G, G extends String> B STEP3_1(@In Supplier<B> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <A extends String> A STEP3(@In Supplier<A> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <B extends G, G extends String> B STEP4(@In Supplier<B> supplierB) {
    return supplierB.get();
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class RETTO_Flow_Gen_GlobalFunctionCall1<C extends X, X extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  @State String str;
  @State C hello;
  @State X world;

  public RETTO_Flow_Gen_GlobalFunctionCall1(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @StepCall(globalFunctionName = "STEP1", transit = "TRANSIT1", returnTo = "str")
  static String STEP1(@In(name = "supplier", from = "supplierC") Supplier<String> supplierC) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT1(@StepRef Transition STEP2) {
    return STEP2;
  }

  @StepCall(globalFunctionName = "STEP2", transit = "TRANSIT2", returnTo = "str")
  static String STEP2(@In(name = "supplier", from = "supplierX") Supplier<String> supplierX) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT2(@StepRef Transition STEP3) {
    return STEP3;
  }

  @StepCall(globalFunctionName = "STEP3_1", transit = "TRANSIT3", returnTo = "world")
  static <C extends X, X extends String> C STEP3(
      @In(name = "supplier", from = "supplierC") Supplier<C> supplierC) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT3(@StepRef Transition STEP4) {
    return STEP4;
  }

  @StepCall(globalFunctionName = "STEP4", transit = "TRANSIT4", returnTo = "hello")
  static <C extends X, X extends String> C STEP4(
      @In(name = "supplierB", from = "supplierC") Supplier<C> supplierC) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT4(@Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", extendz = "BASE")
class RETTO_Flow_Gen_GlobalTransitionerCall2<C2 extends X2, X2 extends String>
    extends RETTO_Flow_Gen_GlobalFunctionCall1<C2, X2> {
  @State C2 hello;
  @State X2 world;

  public RETTO_Flow_Gen_GlobalTransitionerCall2(Supplier<C2> supplierC, Supplier<X2> supplierX) {
    super(supplierC, supplierX);
  }
}

@GlobalFunctionContainer
class RETTO_GlobalFunctionContainer2_2 {
  @GlobalFunction
  static <J extends String> J STEP1(@In Supplier<J> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <K extends List<L>, L extends String> K STEP2(@In Supplier<K> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <J extends String> List<J> STEP3(@In Supplier<List<J>> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <K extends List<L>, L extends String> K STEP4(@In Supplier<K> supplierK) {
    return supplierK.get();
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class RETTO_Flow_Gen_GlobalFunctionCall2<C extends List<X>, X extends String> {
  @State String str;
  @State List<String> list;
  @State List<X> listX;
  @State C hello;
  @State X world;

  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  public RETTO_Flow_Gen_GlobalFunctionCall2(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @StepCall(globalFunctionName = "STEP1", transit = "TRANSIT1", returnTo = "str")
  static String STEP1(@In(name = "supplier", from = "supplierX") Supplier<String> supplierX) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT1(@StepRef Transition STEP2) {
    return STEP2;
  }

  @StepCall(globalFunctionName = "STEP2", transit = "TRANSIT2", returnTo = "list")
  static List<String> STEP2(
      @In(name = "supplier", from = "supplierC") Supplier<List<String>> supplierC) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT2(@StepRef Transition STEP3) {
    return STEP3;
  }

  @StepCall(globalFunctionName = "STEP3", transit = "TRANSIT3", returnTo = "listX")
  static <X extends String> List<X> STEP3(
      @In(name = "supplier", from = "supplierC") Supplier<List<X>> supplierC) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT3(@StepRef Transition STEP4) {
    return STEP4;
  }

  @StepCall(globalFunctionName = "STEP4", transit = "TRANSIT4", returnTo = "hello")
  static <C extends List<X>, X extends String> C STEP4(
      @In(name = "supplierK", from = "supplierC") Supplier<C> supplierC) {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT4(@Terminal Transition END) {
    return END;
  }
}
