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
import com.flower.anno.params.step.transitOverride.TransitInPrm;
import com.flower.anno.params.step.transitOverride.TransitInRetPrm;
import com.flower.anno.params.step.transitOverride.TransitParametersOverride;
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

class InRetParameterCreatorGenericBaseTest {
  @Test
  void test_INRET_Flow_Base() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base.class);
    flower.initialize();

    FlowExec<INRET_Flow_Base> helloWorldExec = flower.getFlowExec(INRET_Flow_Base.class);
    FlowFuture<INRET_Flow_Base> flowFuture =
        helloWorldExec.runFlow(new INRET_Flow_Base<>("Hello", " world!"));

    INRET_Flow_Base state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRET_Flow_Base_Child() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base.class);
    flower.registerFlow(INRET_Flow_Base_Child.class);
    flower.initialize();

    FlowExec<INRET_Flow_Base_Child> helloWorldExec =
        flower.getFlowExec(INRET_Flow_Base_Child.class);
    FlowFuture<INRET_Flow_Base_Child> flowFuture =
        helloWorldExec.runFlow(
            new INRET_Flow_Base_Child<>(ImmutableList.of("Hello"), ImmutableList.of(" world!")));

    INRET_Flow_Base_Child state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_INRET_Flow_Base_Child2() {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base.class);
    flower.registerFlow(INRET_Flow_Base_Child2_Fails.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@InRet parameter type mismatch"));
  }

  // -----------------------------------------

  @Test
  void test_INRET_Flow_Call() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRET_GlobalFunctionContainer.class);
    flower.registerFlow(INRET_Flow_Call.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, () -> flower.initialize());
    assertTrue(e.getMessage().contains("@InRet parameter type mismatch"));
  }

  @Test
  void test_INRET_Flow_Call2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRET_GlobalFunctionContainer.class);
    flower.registerFlow(INRET_Flow_Call2.class);
    flower.initialize();

    FlowExec<INRET_Flow_Call2> helloWorldExec = flower.getFlowExec(INRET_Flow_Call2.class);
    FlowFuture<INRET_Flow_Call2> flowFuture =
        helloWorldExec.runFlow(new INRET_Flow_Call2<>("Hello", " world!", "HelloD", " worldD!"));

    INRET_Flow_Call2 state = flowFuture.getFuture().get();
    assertEquals("HelloD", state.hello);
    assertEquals(" worldD!", state.world);
  }

  @Test
  void test_INRET_Flow_Reference() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRET_GlobalFunctionContainer.class);
    flower.registerFlow(INRET_Flow_Reference.class);
    flower.initialize();

    FlowExec<INRET_Flow_Reference> helloWorldExec = flower.getFlowExec(INRET_Flow_Reference.class);
    FlowFuture<INRET_Flow_Reference> flowFuture =
        helloWorldExec.runFlow(
            new INRET_Flow_Reference<>("Hello", " world!", "Hello1", " world1!"));

    INRET_Flow_Reference state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
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
  void test_INRET_Flow_Reference3() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRET_GlobalFunctionContainer.class);
    flower.registerFlow(INRET_Flow_Reference3.class);
    flower.initialize();

    FlowExec<INRET_Flow_Reference3> helloWorldExec =
        flower.getFlowExec(INRET_Flow_Reference3.class);
    FlowFuture<INRET_Flow_Reference3> flowFuture =
        helloWorldExec.runFlow(
            new INRET_Flow_Reference3<>("Hello", " world!", "Hello1", " world1!"));

    INRET_Flow_Reference3 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRET_Flow_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_2.class);
    flower.initialize();

    FlowExec<INRET_Flow_2> helloWorldExec = flower.getFlowExec(INRET_Flow_2.class);
    FlowFuture<INRET_Flow_2> flowFuture =
        helloWorldExec.runFlow(
            new INRET_Flow_2<>(ImmutableList.of("Hello"), ImmutableList.of(" world!")));

    INRET_Flow_2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(ImmutableList.of(" world!"), state.world);
  }

  @Test
  void test_INRET_Flow_Base2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRET_Flow_Base2.class);
    flower.initialize();

    FlowExec<INRET_Flow_Base2> helloWorldExec = flower.getFlowExec(INRET_Flow_Base2.class);
    FlowFuture<INRET_Flow_Base2> flowFuture =
        helloWorldExec.runFlow(new INRET_Flow_Base2<>(ImmutableList.of("Hello"), " world!"));

    INRET_Flow_Base2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class INRET_Flow_Base<C> {
  @State final C hello;
  @State final C world;

  public INRET_Flow_Base(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "HELLO_TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return hello;
  }

  @TransitFunction
  static <C> Transition HELLO_TRANSIT(@InRet C helloRet, @In C world, @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = "BASE")
class INRET_Flow_Base_Child<C extends List<String>> extends INRET_Flow_Base<C> {
  public INRET_Flow_Base_Child(C hello, C world) {
    super(hello, world);
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class INRET_Flow_Base2<C> {
  @State
  final C hello;
  @State final C world;

  public INRET_Flow_Base2(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "HELLO_TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return hello;
  }

  @TransitFunction
  static <C> Transition HELLO_TRANSIT(@InRet C helloRet, @In C world, @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP", extendz = "BASE")
class INRET_Flow_Base_Child2_Fails<G extends List<String>, Z extends String>
    extends INRET_Flow_Base<G> {
  @State Z hello;
  @State Z world;

  @StepFunction(transit = "HELLO_TRANSIT")
  static <Z extends String> Z HELLO_STEP(@In Z hello, @In Z world) {
    return hello;
  }

  public INRET_Flow_Base_Child2_Fails(G hello0, G world0, Z hello, Z world) {
    super(hello0, world0);
    this.hello = hello;
    this.world = world;
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class INRET_Flow_2<C> {
  @State final List<C> hello;
  @State final List<C> world;

  public INRET_Flow_2(List<C> hello, List<C> world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "HELLO_TRANSIT")
  static <C> List<C> HELLO_STEP(@In List<C> hello, @In List<C> world) {
    return hello;
  }

  @TransitFunction
  static <C> Transition HELLO_TRANSIT(
      @InRet List<C> helloRet, @In List<C> world, @Terminal Transition END) {
    System.out.println(helloRet + " " + world);
    return END;
  }
}

/*@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class IN_Flow_3<Y extends X, X> {
    @State final Y hello;

    public IN_Flow_3(Y hello) {
        this.hello = hello;
    }

    @SimpleStepFunction
    static <X> Transition HELLO_STEP(@In X hello, @Terminal Transition END) {
        System.out.println(hello);
        return END;
    }

    Y in(Y y) {
        in2(y);
        return y;
        //This won't work (x -> y): return in2(y);
    }

    X in2(X x) {
        return x;
    }
}
*/
// -----------------------------------------

@GlobalFunctionContainer
class INRET_GlobalFunctionContainer {
  @GlobalFunction
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return hello;
  }

  @GlobalFunction
  static <X extends String> Transition HELLO_GLOBAL(
      @In X hello, @InRet X world, @Terminal Transition END) {
    System.out.println(hello + " " + world);
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
class INRET_Flow_Call<C> {
  @State final C hello;
  @State final C world;

  public INRET_Flow_Call(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepCall(globalFunctionName = "HELLO_STEP", transit = "HELLO_TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    return null;
  }

  @TransitCall(globalFunctionName = "HELLO_GLOBAL")
  static <C> Transition HELLO_TRANSIT(@InRet C hello, @In C world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class INRET_Flow_Call2<C, D extends String> {
  @State final C helloC;
  @State final C worldC;
  @State final D hello;
  @State final D world;

  public INRET_Flow_Call2(C helloC, C worldC, D hello, D world) {
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
      @InRet D hello, @In D world, @Terminal Transition END) {
    return END;
  }
}

// -----------------------------------------

@FlowType(firstStep = "HELLO_STEP")
class INRET_Flow_Reference<C extends String, Z extends String> {
  @State final C hello;
  @State final C world;
  @State final Z hello1;
  @State final Z world1;

  public INRET_Flow_Reference(C hello, C world, Z hello1, Z world1) {
    this.hello = hello;
    this.world = world;
    this.hello1 = hello1;
    this.world1 = world1;
  }

  @TransitParametersOverride(
      inRet = {@TransitInRetPrm(paramName = "hello")},
      in = {@TransitInPrm(paramName = "world", from = "world1")})
  @StepFunction(globalTransit = "HELLO_GLOBAL")
  static <Z extends String, C extends String> Z HELLO_STEP() {
    return null;
  }
}

/*
@FlowType(firstStep = "HELLO_STEP")
class IN_Flow_Reference2<C extends String, Z extends String> {
    @State final Z hello;
    @State final Z world;
    @State final C hello1;
    @State final C world1;

    public IN_Flow_Reference2(Z hello, Z world, C hello1, C world1) {
        this.hello = hello;
        this.world = world;
        this.hello1 = hello1;
        this.world1 = world1;
    }

    @TransitParametersOverride(in={
            @TransitInPrm(paramName = "hello", from = "hello1"),
            @TransitInPrm(paramName = "world", from = "world1")
    })
    @StepFunction(transit = "TRANSIT")
    static void HELLO_STEP() { return; }

    @TransitCall(globalFunctionName = "HELLO_GLOBAL")
    static <C extends String> void TRANSIT(@In C hello, @In C world, @Terminal Transition END) { }
}
*/
@FlowType(firstStep = "HELLO_STEP")
class INRET_Flow_Reference3<C extends String, Z extends String> {
  @State final Z hello;
  @State final Z world;
  @State final C hello1;
  @State final C world1;

  public INRET_Flow_Reference3(Z hello, Z world, C hello1, C world1) {
    this.hello = hello;
    this.world = world;
    this.hello1 = hello1;
    this.world1 = world1;
  }

  @TransitParametersOverride(
      inRet = {@TransitInRetPrm(paramName = "hello")},
      in = {@TransitInPrm(paramName = "world", from = "world1")})
  @StepFunction(transit = "TRANSIT")
  static <C extends String, Z extends String> C HELLO_STEP() {
    return null;
  }

  @TransitFunction
  static <C extends String> Transition TRANSIT(@In C hello, @In C world, @Terminal Transition END) {
    return END;
  }
}

// -----------------------------------------
/*
@FlowType(firstStep = "HELLO_STEP")
class IN_Flow_Reference4<C extends String> {
    @State final List<C> hello;
    @State final C world;

    public IN_Flow_Reference4(List<C> hello, C world) {
        this.hello = hello;
        this.world = world;
    }

    @TransitParametersOverride(in={
            @TransitInPrm(paramName = "hello", from = "hello1"),
            @TransitInPrm(paramName = "world", from = "world1")
    })
    @StepFunction(globalTransit = "HELLO_GLOBAL")
    static <Z extends String, C extends String> void HELLO_STEP() { return; }
}

@FlowType(firstStep = "HELLO_STEP", name = "BASE")
class IN_Flow_Base2<C> {
    @State final List<C> hello;
    @State final C world;

    public IN_Flow_Base2(List<C> hello, C world) {
        this.hello = hello;
        this.world = world;
    }

    @SimpleStepFunction
    static <C> Transition HELLO_STEP(@In List<C> hello, @In C world, @Terminal Transition END) {
        System.out.println(hello + " " + world);
        return END;
    }
}
*/
