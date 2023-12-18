package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.SimpleStepCall;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.step.transitOverride.TransitOutPrm;
import com.flower.anno.params.step.transitOverride.TransitParametersOverride;
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

class OutParameterCreatorGenericBaseTest {
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
  void test_OUT_Flow_Base() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(OUT_Flow_Base.class);
    flower.initialize();

    FlowExec<OUT_Flow_Base> helloWorldExec = flower.getFlowExec(OUT_Flow_Base.class);
    FlowFuture<OUT_Flow_Base> flowFuture = helloWorldExec.runFlow(new OUT_Flow_Base<>(STRINGS()));

    OUT_Flow_Base state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_OUT_Flow_Base_Child() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(OUT_Flow_Base.class);
    flower.registerFlow(OUT_Flow_Base_Child.class);
    flower.initialize();

    FlowExec<OUT_Flow_Base_Child> helloWorldExec = flower.getFlowExec(OUT_Flow_Base_Child.class);
    FlowFuture<OUT_Flow_Base_Child> flowFuture =
        helloWorldExec.runFlow(new OUT_Flow_Base_Child<>(LISTS()));

    OUT_Flow_Base_Child state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_OUT_Flow_Base_Child2() {
    Flower flower = new Flower();
    flower.registerFlow(OUT_Flow_Base.class);
    flower.registerFlow(OUT_Flow_Base_Child2_Fails.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@Out parameter type mismatch"));
  }

  // -----------------------------------------

  @Test
  void test_OUT_Flow_Call() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(OUT_GlobalFunctionContainer.class);
    flower.registerFlow(OUT_Flow_Call.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@Out parameter type mismatch"));
  }

  @Test
  void test_OUT_Flow_Call2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(OUT_GlobalFunctionContainer.class);
    flower.registerFlow(OUT_Flow_Call2.class);
    flower.initialize();

    FlowExec<OUT_Flow_Call2> helloWorldExec = flower.getFlowExec(OUT_Flow_Call2.class);
    FlowFuture<OUT_Flow_Call2> flowFuture =
        helloWorldExec.runFlow(new OUT_Flow_Call2<>(LISTS(), STRINGSD()));

    OUT_Flow_Call2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello);
    assertEquals(" worldD!", state.world);
  }

  @Test
  void test_OUT_Flow_Reference() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(OUT_GlobalFunctionContainer.class);
    flower.registerFlow(OUT_Flow_Reference.class);
    flower.initialize();

    FlowExec<OUT_Flow_Reference> helloWorldExec = flower.getFlowExec(OUT_Flow_Reference.class);
    FlowFuture<OUT_Flow_Reference> flowFuture =
        helloWorldExec.runFlow(new OUT_Flow_Reference<>(STRINGS(), STRINGSD()));

    OUT_Flow_Reference state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello1);
    assertEquals(" worldD!", state.world1);
  }

  @Test
  void test_OUT_Flow_Reference2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(OUT_GlobalFunctionContainer.class);
    flower.registerFlow(OUT_Flow_Reference2.class);
    flower.initialize();

    FlowExec<OUT_Flow_Reference2> helloWorldExec = flower.getFlowExec(OUT_Flow_Reference2.class);
    FlowFuture<OUT_Flow_Reference2> flowFuture =
        helloWorldExec.runFlow(new OUT_Flow_Reference2<>(STRINGS(), STRINGSD()));

    OUT_Flow_Reference2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello1);
    assertEquals(" worldD!", state.world1);
  }

  @Test
  void test_OUT_Flow_Reference3() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(OUT_GlobalFunctionContainer.class);
    flower.registerFlow(OUT_Flow_Reference3.class);
    flower.initialize();

    FlowExec<OUT_Flow_Reference3> helloWorldExec = flower.getFlowExec(OUT_Flow_Reference3.class);
    FlowFuture<OUT_Flow_Reference3> flowFuture =
        helloWorldExec.runFlow(new OUT_Flow_Reference3<>(STRINGS(), STRINGSD()));

    OUT_Flow_Reference3 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello1);
    assertEquals(" world!", state.world1);
  }

  @Test
  void test_OUT_Flow_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(OUT_Flow_2.class);
    flower.initialize();

    FlowExec<OUT_Flow_2> helloWorldExec = flower.getFlowExec(OUT_Flow_2.class);
    FlowFuture<OUT_Flow_2> flowFuture = helloWorldExec.runFlow(new OUT_Flow_2<>(LISTS()));

    OUT_Flow_2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_OUT_Flow_Base2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(OUT_Flow_Base2.class);
    flower.initialize();

    FlowExec<OUT_Flow_Base2> helloWorldExec = flower.getFlowExec(OUT_Flow_Base2.class);
    FlowFuture<OUT_Flow_Base2> flowFuture =
        helloWorldExec.runFlow(new OUT_Flow_Base2(LISTS(), STRINGS()));

    OUT_Flow_Base2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals("Hello", state.world);
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class OUT_Flow_Base<C> {
  @State final Supplier<C> supplier;
  @State C hello;
  @State C world;

  public OUT_Flow_Base(Supplier<C> supplier) {
    this.supplier = supplier;
  }

  @SimpleStepFunction
  static <C> Transition HELLO_STEP(
      @Out OutPrm<C> hello,
      @Out OutPrm<C> world,
      @In Supplier<C> supplier,
      @Terminal Transition END) {
    hello.setOutValue(supplier.get());
    world.setOutValue(supplier.get());

    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = OUT_Flow_Base.class)
class OUT_Flow_Base_Child<C extends List<String>> extends OUT_Flow_Base<C> {
  public OUT_Flow_Base_Child(Supplier<C> supplier) {
    super(supplier);
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = OUT_Flow_Base.class)
class OUT_Flow_Base_Child2_Fails<G extends List<String>, Z extends String>
    extends OUT_Flow_Base<G> {
  @State Supplier<Z> supplier;
  @State Z hello;
  @State Z world;

  public OUT_Flow_Base_Child2_Fails(Supplier<G> supplierG, Supplier<Z> supplier) {
    super(supplierG);
    this.supplier = supplier;
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class OUT_Flow_2<C> {
  @State final Supplier<List<C>> supplier;
  @State List<C> hello;
  @State List<C> world;

  public OUT_Flow_2(Supplier<List<C>> supplier) {
    this.supplier = supplier;
  }

  @SimpleStepFunction
  static <C> Transition HELLO_STEP(
      @Out OutPrm<List<C>> hello,
      @Out OutPrm<List<C>> world,
      @In Supplier<List<C>> supplier,
      @Terminal Transition END) {
    hello.setOutValue(supplier.get());
    world.setOutValue(supplier.get());
    return END;
  }
}

// -----------------------------------------

@GlobalFunctionContainer
class OUT_GlobalFunctionContainer {
  @GlobalFunction
  static <X extends String> Transition HELLO_GLOBAL(
      @Out OutPrm<X> hello,
      @Out OutPrm<X> world,
      @In Supplier<X> supplier,
      @Terminal Transition END) {
    hello.setOutValue(supplier.get());
    world.setOutValue(supplier.get());

    return END;
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
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP")
class OUT_Flow_Call<C> {
  @State final Supplier<C> supplier;
  @State C hello;
  @State C world;

  public OUT_Flow_Call(Supplier<C> supplier) {
    this.supplier = supplier;
  }

  @SimpleStepCall(globalFunctionContainer = OUT_GlobalFunctionContainer.class, globalFunctionName = "HELLO_GLOBAL")
  static <C> Transition HELLO_STEP(
      @Out OutPrm<C> hello,
      @Out OutPrm<C> world,
      @In Supplier<C> supplier,
      @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class OUT_Flow_Call2<C, D extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<D> supplier;
  @State C helloC;
  @State C worldC;
  @State D hello;
  @State D world;

  public OUT_Flow_Call2(Supplier<C> supplierC, Supplier<D> supplier) {
    this.supplierC = supplierC;
    this.supplier = supplier;
  }

  @SimpleStepCall(globalFunctionContainer = OUT_GlobalFunctionContainer.class, globalFunctionName = "HELLO_GLOBAL")
  static <D extends String> Transition HELLO_STEP(
      @Out OutPrm<D> hello,
      @Out OutPrm<D> world,
      @In Supplier<D> supplier,
      @Terminal Transition END) {
    return END;
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP")
class OUT_Flow_Reference<C extends String, Z extends String> {
  @State
  final Supplier<C> supplierC;
  @State final Supplier<Z> supplier;

  @State C hello;
  @State C world;
  @State Z hello1;
  @State Z world1;

  public OUT_Flow_Reference(Supplier<C> supplierC, Supplier<Z> supplier) {
    this.supplierC = supplierC;
    this.supplier = supplier;
  }

  @TransitParametersOverride(
      out = {
        @TransitOutPrm(paramName = "hello", to = "hello1"),
        @TransitOutPrm(paramName = "world", to = "world1")
      })
  @StepFunction(globalTransitContainer = OUT_GlobalFunctionContainer.class, globalTransit = "HELLO_GLOBAL")
  static <Z extends String, C extends String> void HELLO_STEP() {
    return;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class OUT_Flow_Reference2<C extends String, Z extends String> {
  @State final Supplier<Z> supplierZ;
  @State final Supplier<C> supplier;

  @State Z hello;
  @State Z world;
  @State C hello1;
  @State C world1;

  public OUT_Flow_Reference2(Supplier<Z> supplierZ, Supplier<C> supplier) {
    this.supplierZ = supplierZ;
    this.supplier = supplier;
  }

  @TransitParametersOverride(
      out = {
        @TransitOutPrm(paramName = "hello", to = "hello1"),
        @TransitOutPrm(paramName = "world", to = "world1")
      })
  @StepFunction(transit = "TRANSIT")
  static void HELLO_STEP() {}

  @TransitCall(globalFunctionContainer = OUT_GlobalFunctionContainer.class, globalFunctionName = "HELLO_GLOBAL")
  static <C extends String> Transition TRANSIT(
      @Out OutPrm<C> hello,
      @Out OutPrm<C> world,
      @In Supplier<C> supplier,
      @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class OUT_Flow_Reference3<C extends String, Z extends String> {
  @State final Supplier<C> supplierC;
  @State final Supplier<Z> supplierZ;

  @State Z hello;
  @State Z world;
  @State C hello1;
  @State C world1;

  public OUT_Flow_Reference3(Supplier<C> supplierC, Supplier<Z> supplierZ) {
    this.supplierC = supplierC;
    this.supplierZ = supplierZ;
  }

  @TransitParametersOverride(
      out = {
        @TransitOutPrm(paramName = "hello", to = "hello1"),
        @TransitOutPrm(paramName = "world", to = "world1")
      })
  @StepFunction(transit = "TRANSIT")
  static void HELLO_STEP() {
    return;
  }

  @TransitFunction
  static <C extends String> Transition TRANSIT(
      @Out OutPrm<C> hello,
      @Out OutPrm<C> world,
      @In Supplier<C> supplierC,
      @Terminal Transition END) {
    hello.setOutValue(supplierC.get());
    world.setOutValue(supplierC.get());
    return END;
  }
}
// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class OUT_Flow_Base2<C> {
  @State final Supplier<C> supplier;
  @State final Supplier<List<C>> supplierList;
  @State List<C> hello;
  @State C world;

  public OUT_Flow_Base2(Supplier<List<C>> supplierList, Supplier<C> supplier) {
    this.supplierList = supplierList;
    this.supplier = supplier;
  }

  @SimpleStepFunction
  static <C> Transition HELLO_STEP(
      @Out OutPrm<List<C>> hello,
      @Out OutPrm<C> world,
      @In Supplier<List<C>> supplierList,
      @In Supplier<C> supplier,
      @Terminal Transition END) {
    hello.setOutValue(supplierList.get());
    world.setOutValue(supplier.get());

    return END;
  }
}
