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
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class InRetOrExceptionParameterCreatorGenericBaseTest {
  @Test
  void test_INRETOREXC_Flow_Base() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Base.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Base> helloWorldExec = flower.getFlowExec(INRETOREXC_Flow_Base.class);
    FlowFuture<INRETOREXC_Flow_Base> flowFuture =
        helloWorldExec.runFlow(new INRETOREXC_Flow_Base<>("Hello", " world!"));

    INRETOREXC_Flow_Base state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Base_Child() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Base.class);
    flower.registerFlow(INRETOREXC_Flow_Base_Child.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Base_Child> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Base_Child.class);
    FlowFuture<INRETOREXC_Flow_Base_Child> flowFuture =
        helloWorldExec.runFlow(
            new INRETOREXC_Flow_Base_Child<>(
                ImmutableList.of("Hello"), ImmutableList.of(" world!")));

    INRETOREXC_Flow_Base_Child state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Base_Child2() {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Base.class);
    flower.registerFlow(INRETOREXC_Flow_Base_Child2_Fails.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@InRetOrException parameter type mismatch"));
  }

  // -----------------------------------------

  @Test
  void test_INRETOREXC_Flow_Call() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRETOREXC_GlobalFunctionContainer.class);
    flower.registerFlow(INRETOREXC_Flow_Call.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@InRetOrException parameter type mismatch"));
  }

  @Test
  void test_INRETOREXC_Flow_Call2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRETOREXC_GlobalFunctionContainer.class);
    flower.registerFlow(INRETOREXC_Flow_Call2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Call2> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Call2.class);
    FlowFuture<INRETOREXC_Flow_Call2> flowFuture =
        helloWorldExec.runFlow(
            new INRETOREXC_Flow_Call2<>("Hello", " world!", "HelloD", " worldD!"));

    INRETOREXC_Flow_Call2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello);
    assertEquals(" worldD!", state.world);
  }

  @Test
  void test_IN_Flow_Reference2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(IN_GlobalFunctionContainer.class);
    flower.registerFlow(IN_Flow_Reference2.class);
    flower.initialize();

    FlowExec<IN_Flow_Reference2> helloWorldExec = flower.getFlowExec(IN_Flow_Reference2.class);
    FlowFuture<IN_Flow_Reference2> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Reference2<>("Hello", " world!", "Hello1", " world1!"));

    IN_Flow_Reference2 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_2> helloWorldExec = flower.getFlowExec(INRETOREXC_Flow_2.class);
    FlowFuture<INRETOREXC_Flow_2> flowFuture =
        helloWorldExec.runFlow(
            new INRETOREXC_Flow_2<>(ImmutableList.of("Hello"), ImmutableList.of(" world!")));

    INRETOREXC_Flow_2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Base2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Base2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Base2> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Base2.class);
    FlowFuture<INRETOREXC_Flow_Base2> flowFuture =
        helloWorldExec.runFlow(new INRETOREXC_Flow_Base2<>(ImmutableList.of("Hello"), " world!"));

    INRETOREXC_Flow_Base2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class INRETOREXC_Flow_Base<C> {
  @State final C hello;
  @State final C world;

  public INRETOREXC_Flow_Base(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "HELLO_TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return hello;
  }

  @TransitFunction
  static <C> Transition HELLO_TRANSIT(
      @InRetOrException ReturnValueOrException<C> helloRet, @In C world, @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = "BASE")
class INRETOREXC_Flow_Base_Child<C extends List<String>> extends INRETOREXC_Flow_Base<C> {
  public INRETOREXC_Flow_Base_Child(C hello, C world) {
    super(hello, world);
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class INRETOREXC_Flow_Base2<C> {
  @State final C hello;
  @State final C world;

  public INRETOREXC_Flow_Base2(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "HELLO_TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return hello;
  }

  @TransitFunction
  static <C> Transition HELLO_TRANSIT(
      @InRetOrException ReturnValueOrException<C> helloRet, @In C world, @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = "BASE")
class INRETOREXC_Flow_Base_Child2_Fails<G extends List<String>, Z extends String>
    extends INRETOREXC_Flow_Base<G> {
  @State Z hello;
  @State Z world;

  @StepFunction(transit = "HELLO_TRANSIT")
  static <Z extends String> Z HELLO_STEP(@In Z hello, @In Z world) {
    return hello;
  }

  public INRETOREXC_Flow_Base_Child2_Fails(G hello0, G world0, Z hello, Z world) {
    super(hello0, world0);
    this.hello = hello;
    this.world = world;
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class INRETOREXC_Flow_2<C> {
  @State
  final List<C> hello;
  @State final List<C> world;

  public INRETOREXC_Flow_2(List<C> hello, List<C> world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "HELLO_TRANSIT")
  static <C> List<C> HELLO_STEP(@In List<C> hello, @In List<C> world) {
    return hello;
  }

  @TransitFunction
  static <C> Transition HELLO_TRANSIT(
      @InRetOrException ReturnValueOrException<List<C>> helloRet,
      @In List<C> world,
      @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }
}

// -----------------------------------------

@GlobalFunctionContainer
class INRETOREXC_GlobalFunctionContainer {
  @GlobalFunction
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return hello;
  }

  @GlobalFunction
  static <X extends String> Transition HELLO_GLOBAL(
          @InRetOrException ReturnValueOrException<X> helloRet, @In X world, @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }

  @GlobalFunction
  static <X extends String, Y extends List> Transition HELLO_GLOBAL2(
      @In X hello, @In X world, @Terminal Transition END) {
    System.out.println(hello + " " + world);
    return END;
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP")
class INRETOREXC_Flow_Call<C> {
  @State final C hello;
  @State final C world;

  public INRETOREXC_Flow_Call(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepCall(globalFunctionName = "HELLO_STEP", transit = "HELLO_TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return null;
  }

  @TransitCall(globalFunctionName = "HELLO_GLOBAL")
  static <C> Transition HELLO_TRANSIT(
      @InRetOrException ReturnValueOrException<C> helloRet, @In C world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class INRETOREXC_Flow_Call2<C, D extends String> {
  @State final C helloC;
  @State final C worldC;
  @State final D hello;
  @State final D world;

  public INRETOREXC_Flow_Call2(C helloC, C worldC, D hello, D world) {
    this.helloC = helloC;
    this.worldC = worldC;
    this.hello = hello;
    this.world = world;
  }

  @StepCall(globalFunctionName = "HELLO_STEP", transit = "HELLO_TRANSIT")
  static <D extends String> D HELLO_STEP(@In D hello, @In D world) {
    return null;
  }

  @TransitCall(globalFunctionName = "HELLO_GLOBAL")
  static <D extends String> Transition HELLO_TRANSIT(
      @InRetOrException ReturnValueOrException<D> helloRet, @In D world, @Terminal Transition END) {
    return END;
  }
}
