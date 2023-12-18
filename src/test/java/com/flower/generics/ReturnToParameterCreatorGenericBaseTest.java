package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class ReturnToParameterCreatorGenericBaseTest {
  static class Factory<T> implements Supplier<T> {
    final List<T> elements;
    int index = 0;

    public Factory(T... elements) {
      this.elements = ImmutableList.copyOf(elements);
    }

    @Override
    public T get() {
      return elements.get(index++);
    }
  }

  static Factory<String> STRINGS() {
    return new Factory<>("Hello", " world!");
  }

  static Factory<String> STRINGSD() {
    return new Factory<>("HelloD", " worldD!");
  }

  static Factory<List<String>> LISTS() {
    return new Factory<>(ImmutableList.of("Hello"), ImmutableList.of(" world!"));
  }

  @Test
  void test_RETTO_Flow_Base() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Base> helloWorldExec = flower.getFlowExec(RETTO_Flow_Base.class);
    FlowFuture<RETTO_Flow_Base> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Base<>(STRINGS()));

    RETTO_Flow_Base state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
  }

  @Test
  void test_RETTO_Flow_Base_Child() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base.class);
    flower.registerFlow(RETTO_Flow_Base_Child.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Base_Child> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Base_Child.class);
    FlowFuture<RETTO_Flow_Base_Child> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Base_Child<>(LISTS()));

    RETTO_Flow_Base_Child state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
  }

  @Test
  void test_RETTO_Flow_Base_Child2() {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base.class);
    flower.registerFlow(RETTO_Flow_Base_Child2_Fails.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@ReturnTo parameter type mismatch"));
  }

  // -----------------------------------------

  @Test
  void test_RETTO_Flow_Call() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer.class);
    flower.registerFlow(RETTO_Flow_Call.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@ReturnTo or return value parameter type mismatch"));
  }

  @Test
  void test_RETTO_Flow_Call2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer.class);
    flower.registerFlow(RETTO_Flow_Call2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Call2> helloWorldExec = flower.getFlowExec(RETTO_Flow_Call2.class);
    FlowFuture<RETTO_Flow_Call2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Call2<>(LISTS(), STRINGSD()));

    RETTO_Flow_Call2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello);
  }

  @Test
  void test_RETTO_Flow_Reference() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer.class);
    flower.registerFlow(RETTO_Flow_Reference.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Reference> helloWorldExec = flower.getFlowExec(RETTO_Flow_Reference.class);
    FlowFuture<RETTO_Flow_Reference> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Reference<>(STRINGS(), STRINGSD()));

    RETTO_Flow_Reference state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello1);
  }

  @Test
  void test_RETTO_Flow_Reference2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer.class);
    flower.registerFlow(RETTO_Flow_Reference2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Reference2> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Reference2.class);
    FlowFuture<RETTO_Flow_Reference2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Reference2<>(STRINGS(), STRINGSD()));

    RETTO_Flow_Reference2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello1);
  }

  @Test
  void test_RETTO_Flow_Reference3() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(RETTO_GlobalFunctionContainer.class);
    flower.registerFlow(RETTO_Flow_Reference3.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Reference3> helloWorldExec =
        flower.getFlowExec(RETTO_Flow_Reference3.class);
    FlowFuture<RETTO_Flow_Reference3> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Reference3<>(STRINGS(), STRINGSD()));

    RETTO_Flow_Reference3 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello);
  }

  @Test
  void test_RETTO_Flow_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_2> helloWorldExec = flower.getFlowExec(RETTO_Flow_2.class);
    FlowFuture<RETTO_Flow_2> flowFuture = helloWorldExec.runFlow(new RETTO_Flow_2<>(LISTS()));

    RETTO_Flow_2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
  }

  @Test
  void test_RETTO_Flow_Base2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(RETTO_Flow_Base2.class);
    flower.initialize();

    FlowExec<RETTO_Flow_Base2> helloWorldExec = flower.getFlowExec(RETTO_Flow_Base2.class);
    FlowFuture<RETTO_Flow_Base2> flowFuture =
        helloWorldExec.runFlow(new RETTO_Flow_Base2(LISTS(), STRINGS()));

    RETTO_Flow_Base2 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.world);
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class RETTO_Flow_Base<C> {
  @State final Supplier<C> supplier;
  @State C hello;

  public RETTO_Flow_Base(Supplier<C> supplier) {
    this.supplier = supplier;
  }

  @StepFunction(transit = "HELLO_TRANSIT", returnTo = "hello")
  static <C> C HELLO_STEP(@In Supplier<C> supplier) {
    return supplier.get();
  }

  @TransitFunction
  static Transition HELLO_TRANSIT(@Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = RETTO_Flow_Base.class)
class RETTO_Flow_Base_Child<C extends List<String>> extends RETTO_Flow_Base<C> {
  public RETTO_Flow_Base_Child(Supplier<C> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = RETTO_Flow_Base.class)
class RETTO_Flow_Base_Child2_Fails<G extends List<String>, Z extends String>
    extends RETTO_Flow_Base<G> {
  @State Supplier<Z> supplier;
  @State Z hello;
  @State Z world;

  public RETTO_Flow_Base_Child2_Fails(Supplier<G> supplierG, Supplier<Z> supplier) {
    super(supplierG);
    this.supplier = supplier;
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class RETTO_Flow_2<C> {
  @State final Supplier<List<C>> supplier;
  @State List<C> hello;
  @State List<C> world;

  public RETTO_Flow_2(Supplier<List<C>> supplier) {
    this.supplier = supplier;
  }

  @StepFunction(transit = "TRANSIT", returnTo = "hello")
  static <C> List<C> HELLO_STEP(@In Supplier<List<C>> supplier) {
    return supplier.get();
  }

  @TransitFunction
  static <C> Transition TRANSIT(@Terminal Transition END) {
    return END;
  }
}

// -----------------------------------------

@GlobalFunctionContainer
class RETTO_GlobalFunctionContainer {
  @GlobalFunction
  static <X extends String> X HELLO_GLOBAL(@In Supplier<X> supplier) {
    return supplier.get();
  }

  @GlobalFunction
  static <X extends String, Y extends List> Transition HELLO_GLOBAL2(
      @Out OutPrm<X> hello,
      @Out OutPrm<X> world,
      @In Supplier<X> supplier,
      @Terminal Transition END) {
    hello.setOutValue(supplier.get());
    world.setOutValue(supplier.get());

    return END;
  }

  @GlobalFunction
  static Transition HELLO_TRANSIT(@Terminal Transition END) {
    return END;
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP")
class RETTO_Flow_Call<C> {
  @State
  final Supplier<C> supplier;
  @State C hello;
  @State C world;

  public RETTO_Flow_Call(Supplier<C> supplier) {
    this.supplier = supplier;
  }

  @StepCall(transit = "TRANSIT", globalFunctionContainer = RETTO_GlobalFunctionContainer.class, globalFunctionName = "HELLO_GLOBAL")
  static <C> C HELLO_STEP(@In Supplier<C> supplier) {
    return null;
  }

  @TransitCall(globalFunctionContainer = RETTO_GlobalFunctionContainer.class, globalFunctionName = "HELLO_TRANSIT")
  static Transition TRANSIT(@Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class RETTO_Flow_Call2<C, D extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<D> supplier;
  @State C helloC;
  @State C worldC;
  @State D hello;
  @State D world;

  public RETTO_Flow_Call2(Supplier<C> supplierC, Supplier<D> supplier) {
    this.supplierC = supplierC;
    this.supplier = supplier;
  }

  @StepCall(transit = "HELLO_TRANSIT", globalFunctionContainer = RETTO_GlobalFunctionContainer.class, globalFunctionName = "HELLO_GLOBAL", returnTo = "hello")
  static <D extends String> D HELLO_STEP(@In Supplier<D> supplier) {
    return null;
  }

  @TransitCall(globalFunctionContainer = RETTO_GlobalFunctionContainer.class, globalFunctionName = "HELLO_TRANSIT")
  static Transition HELLO_TRANSIT(@Terminal Transition END) {
    return END;
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP")
class RETTO_Flow_Reference<C extends String, Z extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<Z> supplier;

  @State C hello;
  @State C world;
  @State Z hello1;
  @State Z world1;

  public RETTO_Flow_Reference(Supplier<C> supplierC, Supplier<Z> supplier) {
    this.supplierC = supplierC;
    this.supplier = supplier;
  }

  @StepFunction(globalTransitContainer = RETTO_GlobalFunctionContainer.class, globalTransit = "HELLO_TRANSIT", returnTo = "hello1")
  static <Z extends String, C extends String> Z HELLO_STEP(@In Supplier<Z> supplier) {
    return supplier.get();
  }
}

@FlowType(firstStep = "HELLO_STEP")
class RETTO_Flow_Reference2<C extends String, Z extends String> {
  @State final Supplier<Z> supplierZ;
  @State final Supplier<C> supplier;

  @State Z hello;
  @State Z world;
  @State C hello1;
  @State C world1;

  public RETTO_Flow_Reference2(Supplier<Z> supplierZ, Supplier<C> supplier) {
    this.supplierZ = supplierZ;
    this.supplier = supplier;
  }

  @StepFunction(transit = "TRANSIT", returnTo = "hello1")
  static <C extends String> C HELLO_STEP(@In Supplier<C> supplier) {
    return supplier.get();
  }

  @TransitCall(globalFunctionContainer = RETTO_GlobalFunctionContainer.class, globalFunctionName = "HELLO_TRANSIT")
  static <C extends String> Transition TRANSIT(@Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class RETTO_Flow_Reference3<C extends String, Z extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<Z> supplierZ;

  @State Z hello;
  @State Z world;
  @State C hello1;
  @State C world1;

  public RETTO_Flow_Reference3(Supplier<C> supplierC, Supplier<Z> supplierZ) {
    this.supplierC = supplierC;
    this.supplierZ = supplierZ;
  }

  @StepFunction(transit = "TRANSIT", returnTo = "hello")
  static <Z extends String> Z HELLO_STEP(@In Supplier<Z> supplierZ) {
    return supplierZ.get();
  }

  @TransitFunction
  static Transition TRANSIT(@Terminal Transition END) {
    return END;
  }
}
// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class RETTO_Flow_Base2<C> {
  @State final Supplier<C> supplier;
  @State final Supplier<List<C>> supplierList;
  @State List<C> hello;
  @State C world;

  public RETTO_Flow_Base2(Supplier<List<C>> supplierList, Supplier<C> supplier) {
    this.supplierList = supplierList;
    this.supplier = supplier;
  }

  @StepFunction(transit = "TRANSIT", returnTo = "world")
  static <C> C HELLO_STEP(@In Supplier<C> supplier) {
    return supplier.get();
  }

  @TransitFunction
  static Transition TRANSIT(@Terminal Transition END) {
    return END;
  }
}
