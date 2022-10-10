package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.SimpleStepCall;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.step.transitOverride.TransitInPrm;
import com.flower.anno.params.step.transitOverride.TransitParametersOverride;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.InOutPrm;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public class InOutParameterCreatorGenericBase2Test {
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
  void test_INOUT_Flow_Gen1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Gen1.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Gen1> helloWorldExec = flower.getFlowExec(INOUT_Flow_Gen1.class);
    FlowFuture<INOUT_Flow_Gen1> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Gen1<>(STRINGS(), STRINGSD()));

    INOUT_Flow_Gen1 state = flowFuture.getFuture().get();
    assertEquals(" world!", state.hello);
    assertEquals("HelloD", state.world);
  }

  @Test
  void test_INOUT_Flow_Gen1_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Gen1.class);
    flower.registerFlow(INOUT_Flow_Gen1_2.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Gen1_2> helloWorldExec = flower.getFlowExec(INOUT_Flow_Gen1_2.class);
    FlowFuture<INOUT_Flow_Gen1_2> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Gen1_2<>(STRINGS(), STRINGSD()));

    INOUT_Flow_Gen1_2 state = flowFuture.getFuture().get();
    assertEquals(" world!", state.hello);
    assertEquals("HelloD", state.world);
  }

  @Test
  void test_INOUT_Flow_Gen2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Gen2.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Gen2> helloWorldExec = flower.getFlowExec(INOUT_Flow_Gen2.class);
    FlowFuture<INOUT_Flow_Gen2> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Gen2(LISTS(), STRINGSD()));

    INOUT_Flow_Gen2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of(" world!"), state.hello);
    assertEquals("HelloD", state.world);
  }

  @Test
  void test_IN_Flow_Gen2_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INOUT_Flow_Gen2.class);
    flower.registerFlow(INOUT_Flow_Gen2_2.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Gen2_2> helloWorldExec = flower.getFlowExec(INOUT_Flow_Gen2_2.class);
    FlowFuture<INOUT_Flow_Gen2_2> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Gen2_2(LISTS(), STRINGSD()));

    INOUT_Flow_Gen2_2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of(" world!"), state.hello);
    assertEquals("HelloD", state.world);
  }

  // --------------------------------------------------------------------------

  @Test
  void test_IN_Flow_Gen_GlobalFunctionCall1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INOUT_GlobalFunctionContainer2_1.class);
    flower.registerFlow(INOUT_Flow_Gen_GlobalFunctionCall1.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Gen_GlobalFunctionCall1> helloWorldExec =
        flower.getFlowExec(INOUT_Flow_Gen_GlobalFunctionCall1.class);
    FlowFuture<INOUT_Flow_Gen_GlobalFunctionCall1> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Gen_GlobalFunctionCall1<>(STRINGS(), STRINGSD()));

    INOUT_Flow_Gen_GlobalFunctionCall1 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.str);
    assertEquals("here", state.hello);
    assertEquals(" worldD!", state.world);
  }

  @Test
  void test_IN_Flow_Gen_GlobalFunctionCall2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INOUT_GlobalFunctionContainer2_2.class);
    flower.registerFlow(INOUT_Flow_Gen_GlobalFunctionCall2.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Gen_GlobalFunctionCall2> helloWorldExec =
        flower.getFlowExec(INOUT_Flow_Gen_GlobalFunctionCall2.class);
    FlowFuture<INOUT_Flow_Gen_GlobalFunctionCall2> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Gen_GlobalFunctionCall2<>(LISTS(), STRINGS()));

    INOUT_Flow_Gen_GlobalFunctionCall2 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.str);
    assertEquals(ImmutableList.of("Hello"), state.list);
    assertEquals(ImmutableList.of("hereD"), state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Gen_GlobalTransitionerCall1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INOUT_GlobalFunctionContainer2_1.class);
    flower.registerFlow(INOUT_Flow_Gen_GlobalTransitionerCall1.class);
    flower.initialize();

    FlowExec<INOUT_Flow_Gen_GlobalTransitionerCall1> helloWorldExec =
        flower.getFlowExec(INOUT_Flow_Gen_GlobalTransitionerCall1.class);
    FlowFuture<INOUT_Flow_Gen_GlobalTransitionerCall1> flowFuture =
        helloWorldExec.runFlow(new INOUT_Flow_Gen_GlobalTransitionerCall1<>(STRINGS(), STRINGSD()));

    INOUT_Flow_Gen_GlobalTransitionerCall1 state = flowFuture.getFuture().get();
    assertEquals(" world!", state.hello);
    assertEquals("HelloD", state.world);
  }
  /*
  @Test
  void test_IN_Flow_Gen_GlobalTransitionerCall2() throws ExecutionException, InterruptedException {
      Flower flower = new Flower();
      flower.registerGlobalFunctions(OUT_GlobalFunctionContainer2_1.class);
      flower.registerFlow(OUT_Flow_Gen_GlobalTransitionerCall1.class);
      flower.registerFlow(OUT_Flow_Gen_GlobalTransitionerCall2.class);
      flower.initialize();

      FlowExec<OUT_Flow_Gen_GlobalTransitionerCall2> helloWorldExec = flower.getFlowExec(OUT_Flow_Gen_GlobalTransitionerCall2.class);
      FlowFuture<OUT_Flow_Gen_GlobalTransitionerCall2> flowFuture = helloWorldExec.runFlow(new OUT_Flow_Gen_GlobalTransitionerCall2<>(STRINGS(), STRINGSD()));

      OUT_Flow_Gen_GlobalTransitionerCall2 state = flowFuture.getFuture().get();
      assertEquals("Hello", state.str);
      assertEquals("here", state.hello);
      assertEquals(" worldD!", state.world);
  }*/
}

@FlowType(firstStep = "STEP3", name = "BASE")
class INOUT_Flow_Gen1<C extends X, X extends String> {
  @State C hello;
  @State X world;

  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  public INOUT_Flow_Gen1(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @SimpleStepFunction
  static <C extends X, X extends String> Transition STEP3(
      @InOut NullableInOutPrm<X> world, @In Supplier<C> supplierC, @StepRef Transition STEP4) {
    world.setOutValue(supplierC.get());
    return STEP4;
  }

  @SimpleStepFunction
  static <C extends X, X extends String> Transition STEP4(
      @InOut NullableInOutPrm<C> hello,
      @InOut InOutPrm<X> world,
      @In Supplier<C> supplierC,
      @In Supplier<X> supplierX,
      @Terminal Transition END) {
    hello.setOutValue(supplierC.get());
    world.setOutValue(supplierX.get());
    return END;
  }
}

@FlowType(firstStep = "STEP3", extendz = "BASE")
class INOUT_Flow_Gen1_2<C2 extends X2, X2 extends String> extends INOUT_Flow_Gen1<C2, X2> {
  @State C2 hello;
  @State X2 world;

  public INOUT_Flow_Gen1_2(Supplier<C2> supplierC, Supplier<X2> supplierX) {
    super(supplierC, supplierX);
  }
}

@FlowType(firstStep = "STEP3", name = "BASE")
class INOUT_Flow_Gen2<C extends List<X>, X extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  @State C hello;
  @State X world;

  public INOUT_Flow_Gen2(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @SimpleStepFunction
  static <C extends List<X>, X extends String> Transition STEP3(
      @InOut NullableInOutPrm<C> hello, @In Supplier<C> supplierC, @StepRef Transition STEP4) {
    hello.setOutValue(supplierC.get());
    return STEP4;
  }

  @SimpleStepFunction
  static <C extends List<X>, X extends String> Transition STEP4(
      @InOut InOutPrm<C> hello,
      @InOut NullableInOutPrm<X> world,
      @In Supplier<C> supplierC,
      @In Supplier<X> supplierX,
      @Terminal Transition END) {
    hello.setOutValue(supplierC.get());
    world.setOutValue(supplierX.get());
    return END;
  }
}

@FlowType(firstStep = "STEP3", extendz = "BASE")
class INOUT_Flow_Gen2_2<C2 extends List<X2>, X2 extends String> extends INOUT_Flow_Gen2<C2, X2> {
  @State C2 hello;
  @State X2 world;

  public INOUT_Flow_Gen2_2(Supplier<C2> supplierC, Supplier<X2> supplierX) {
    super(supplierC, supplierX);
  }
}

// --------------------------------------------------------------------------

@GlobalFunctionContainer
class INOUT_GlobalFunctionContainer2_1 {
  @GlobalFunction
  static <A extends String> Transition STEP1(
          @InOut NullableInOutPrm<A> str, @In Supplier<A> supplier, @StepRef Transition STEP2) {
    str.setOutValue(supplier.get());
    return STEP2;
  }

  @GlobalFunction
  static <B extends G, G extends String> Transition STEP2(
          @InOut InOutPrm<B> str, @In Supplier<B> supplier, @StepRef Transition STEP3) {
    str.setOutValue(supplier.get());
    return STEP3;
  }

  @GlobalFunction
  static <B extends G, G extends String> Transition STEP3_1(
      @InOut NullableInOutPrm<G> world, @In Supplier<B> supplier, @StepRef Transition STEP4) {
    world.setOutValue(supplier.get());
    return STEP4;
  }

  @GlobalFunction
  static <A extends String> Transition STEP3(
      @InOut NullableInOutPrm<A> hello, @In Supplier<A> supplier, @StepRef Transition STEP4) {
    hello.setOutValue(supplier.get());
    return STEP4;
  }

  @GlobalFunction
  static <B extends G, G extends String> Transition STEP4(
      @InOut NullableInOutPrm<B> hello,
      @InOut NullableInOutPrm<G> world,
      @In Supplier<B> supplierB,
      @In Supplier<G> supplierG,
      @Terminal Transition END) {
    hello.setOutValue(supplierB.get());
    world.setOutValue(supplierG.get());
    return END;
  }
}

@FlowType(firstStep = "STEP3", name = "BASE")
class INOUT_Flow_Gen_GlobalTransitionerCall1<C extends X, X extends String> {
  @State
  final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  @State C hello;
  @State X world;

  public INOUT_Flow_Gen_GlobalTransitionerCall1(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @TransitInPrm(paramName = "supplier", from = "supplierC")
  @StepFunction(globalTransit = "STEP3")
  static <C extends X, X extends String> void STEP3() {}

  @TransitInPrm(paramName = "supplier", from = "supplierC")
  @StepFunction(globalTransit = "STEP3")
  static <C extends X, X extends String> void STEP31() {}

  @TransitParametersOverride(
      in = {
        @TransitInPrm(paramName = "supplierB", from = "supplierC"),
        @TransitInPrm(paramName = "supplierG", from = "supplierX")
      })
  @StepFunction(globalTransit = "STEP4")
  static <C extends X, X extends String> void STEP4() {}
}
/*
@FlowType(firstStep = "STEP1", extendz = "BASE")
class OUT_Flow_Gen_GlobalTransitionerCall2<C2 extends X2, X2 extends String> extends OUT_Flow_Gen_GlobalTransitionerCall1<C2, X2> {
    @State C2 hello;
    @State X2 world;

    public OUT_Flow_Gen_GlobalTransitionerCall2(Supplier<C2> supplierC, Supplier<X2> supplierX) {
        super(supplierC, supplierX);
    }
}
*/
@FlowType(firstStep = "STEP1", name = "BASE")
class INOUT_Flow_Gen_GlobalFunctionCall1<C extends X, X extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  @State String str;
  @State C hello;
  @State X world;

  public INOUT_Flow_Gen_GlobalFunctionCall1(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @SimpleStepCall(globalFunctionName = "STEP1")
  static Transition STEP1(
      @InOut NullableInOutPrm<String> str,
      @In(name = "supplier", from = "supplierC") Supplier<String> supplierC,
      @StepRef Transition STEP2) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP2")
  static Transition STEP2(
      @InOut InOutPrm<String> str,
      @In(name = "supplier", from = "supplierX") Supplier<String> supplierX,
      @StepRef Transition STEP3) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP3_1")
  static <C extends X, X extends String> Transition STEP3(
      @InOut NullableInOutPrm<X> world,
      @In(name = "supplier", from = "supplierC") Supplier<C> supplierC,
      @StepRef Transition STEP4) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP4")
  static <C extends X, X extends String> Transition STEP4(
      @InOut NullableInOutPrm<C> hello,
      @InOut NullableInOutPrm<X> world,
      @In(name = "supplierB", from = "supplierC") Supplier<C> supplierC,
      @In(name = "supplierG", from = "supplierX") Supplier<X> supplierX,
      @Terminal Transition END) {
    return null;
  }
}

@GlobalFunctionContainer
class INOUT_GlobalFunctionContainer2_2 {
  @GlobalFunction
  static <J extends String> Transition STEP1(
      @InOut NullableInOutPrm<J> str, @In Supplier<J> supplier, @StepRef Transition STEP2) {
    str.setOutValue(supplier.get());
    return STEP2;
  }

  @GlobalFunction
  static <K extends List<L>, L extends String> Transition STEP2(
      @InOut NullableInOutPrm<K> list, @In Supplier<K> supplier, @StepRef Transition STEP3) {
    list.setOutValue(supplier.get());
    return STEP3;
  }

  @GlobalFunction
  static <J extends String> Transition STEP3(
      @InOut NullableInOutPrm<List<J>> hello,
      @In Supplier<List<J>> supplier,
      @StepRef Transition STEP4) {
    hello.setOutValue(supplier.get());
    return STEP4;
  }

  @GlobalFunction
  static <K extends List<L>, L extends String> Transition STEP4(
      @InOut NullableInOutPrm<K> hello,
      @InOut NullableInOutPrm<L> world,
      @In Supplier<K> supplierK,
      @In Supplier<L> supplierL,
      @Terminal Transition END) {
    hello.setOutValue(supplierK.get());
    world.setOutValue(supplierL.get());
    return END;
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class INOUT_Flow_Gen_GlobalFunctionCall2<C extends List<X>, X extends String> {
  @State String str;
  @State List<String> list;
  @State List<X> listX;
  @State C hello;
  @State X world;

  @State final Supplier<C> supplierC;
  @State final Supplier<X> supplierX;

  public INOUT_Flow_Gen_GlobalFunctionCall2(Supplier<C> supplierC, Supplier<X> supplierX) {
    this.supplierC = supplierC;
    this.supplierX = supplierX;
  }

  @SimpleStepCall(globalFunctionName = "STEP1")
  static Transition STEP1(
      @InOut NullableInOutPrm<String> str,
      @In(name = "supplier", from = "supplierX") Supplier<String> supplierX,
      @StepRef Transition STEP2) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP2")
  static Transition STEP2(
      @InOut NullableInOutPrm<List<String>> list,
      @In(name = "supplier", from = "supplierC") Supplier<List<String>> supplierC,
      @StepRef Transition STEP3) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP3")
  static <X extends String> Transition STEP3(
      @InOut(name = "hello", fromAndTo = "listX") NullableInOutPrm<List<X>> listX,
      @In(name = "supplier", from = "supplierC") Supplier<List<X>> supplierC,
      @StepRef Transition STEP4) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP4")
  static <C extends List<X>, X extends String> Transition STEP4(
      @InOut NullableInOutPrm<C> hello,
      @InOut NullableInOutPrm<X> world,
      @In(name = "supplierK", from = "supplierC") Supplier<C> supplierC,
      @In(name = "supplierL", from = "supplierX") Supplier<X> supplierX,
      @Terminal Transition END) {
    return null;
  }
}

/*
@FlowType(firstStep = "STEP1", name = "BASE")
class IN_Flow_Gen1_1<C extends X, X extends String> {
    @State final C hello;
    @State final X world;

    public IN_Flow_Gen1_1(C hello, X world) {
        this.hello = hello;
        this.world = world;
    }

    @SimpleStepFunction
    static Transition STEP1(@In String world, @StepRef Transition STEP2) { return STEP2; }

    @SimpleStepFunction
    static Transition STEP2(@In String hello, @StepRef Transition STEP3) { return STEP3; }

    @SimpleStepFunction
    static <X extends String> Transition STEP3(@In X hello, @StepRef Transition STEP4) { return STEP4; }

    @SimpleStepFunction
    static <C extends X, X extends String> Transition STEP4(@In C hello, @In X world, @Terminal Transition END) { return END; }
}
*/
