package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.flower.anno.params.transit.StepRef;
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

public class InRetOrExceptionParameterCreatorGenericBase2Test {
  @Test
  void test_INRETOREXC_Flow_Gen1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Gen1.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen1> helloWorldExec = flower.getFlowExec(INRETOREXC_Flow_Gen1.class);
    FlowFuture<INRETOREXC_Flow_Gen1> flowFuture =
        helloWorldExec.runFlow(new INRETOREXC_Flow_Gen1<>("Hello", " world!"));

    INRETOREXC_Flow_Gen1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Gen1_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Gen1.class);
    flower.registerFlow(INRETOREXC_Flow_Gen1_2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen1_2> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Gen1_2.class);
    FlowFuture<INRETOREXC_Flow_Gen1_2> flowFuture =
        helloWorldExec.runFlow(new INRETOREXC_Flow_Gen1_2<>("Hello", " world!"));

    INRETOREXC_Flow_Gen1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Gen2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Gen2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen2> helloWorldExec = flower.getFlowExec(INRETOREXC_Flow_Gen2.class);
    FlowFuture<INRETOREXC_Flow_Gen2> flowFuture =
        helloWorldExec.runFlow(new INRETOREXC_Flow_Gen2<>(ImmutableList.of("Hello"), " world!"));

    INRETOREXC_Flow_Gen2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Gen2_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(INRETOREXC_Flow_Gen2.class);
    flower.registerFlow(INRETOREXC_Flow_Gen2_2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen2_2> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Gen2_2.class);
    FlowFuture<INRETOREXC_Flow_Gen2_2> flowFuture =
        helloWorldExec.runFlow(new INRETOREXC_Flow_Gen2_2<>(ImmutableList.of("Hello"), " world!"));

    INRETOREXC_Flow_Gen2_2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }

  // --------------------------------------------------------------------------

  @Test
  void test_INRETOREXC_Flow_Gen_GlobalFunctionCall1()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRETOREXC_GlobalFunctionContainer2_1.class);
    flower.registerFlow(INRETOREXC_Flow_Gen_GlobalFunctionCall1.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen_GlobalFunctionCall1> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Gen_GlobalFunctionCall1.class);
    FlowFuture<INRETOREXC_Flow_Gen_GlobalFunctionCall1> flowFuture =
        helloWorldExec.runFlow(new INRETOREXC_Flow_Gen_GlobalFunctionCall1<>("Hello", " world!"));

    INRETOREXC_Flow_Gen_GlobalFunctionCall1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Gen_GlobalFunctionCall2()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRETOREXC_GlobalFunctionContainer2_2.class);
    flower.registerFlow(INRETOREXC_Flow_Gen_GlobalFunctionCall2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen_GlobalFunctionCall2> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Gen_GlobalFunctionCall2.class);
    FlowFuture<INRETOREXC_Flow_Gen_GlobalFunctionCall2> flowFuture =
        helloWorldExec.runFlow(
            new INRETOREXC_Flow_Gen_GlobalFunctionCall2<>(ImmutableList.of("Hello"), " world!"));

    INRETOREXC_Flow_Gen_GlobalFunctionCall2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Gen_GlobalTransitionerCall1()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRETOREXC_GlobalFunctionContainer2_1.class);
    flower.registerFlow(INRETOREXC_Flow_Gen_GlobalTransitionerCall1.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen_GlobalTransitionerCall1> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Gen_GlobalTransitionerCall1.class);
    FlowFuture<INRETOREXC_Flow_Gen_GlobalTransitionerCall1> flowFuture =
        helloWorldExec.runFlow(
            new INRETOREXC_Flow_Gen_GlobalTransitionerCall1<>("Hello", " world!"));

    INRETOREXC_Flow_Gen_GlobalTransitionerCall1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_INRETOREXC_Flow_Gen_GlobalTransitionerCall2()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(INRETOREXC_GlobalFunctionContainer2_1.class);
    flower.registerFlow(INRETOREXC_Flow_Gen_GlobalTransitionerCall1.class);
    flower.registerFlow(INRETOREXC_Flow_Gen_GlobalTransitionerCall2.class);
    flower.initialize();

    FlowExec<INRETOREXC_Flow_Gen_GlobalTransitionerCall2> helloWorldExec =
        flower.getFlowExec(INRETOREXC_Flow_Gen_GlobalTransitionerCall2.class);
    FlowFuture<INRETOREXC_Flow_Gen_GlobalTransitionerCall2> flowFuture =
        helloWorldExec.runFlow(
            new INRETOREXC_Flow_Gen_GlobalTransitionerCall2<>("Hello", " world!"));

    INRETOREXC_Flow_Gen_GlobalTransitionerCall2 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class INRETOREXC_Flow_Gen1<C extends X, X extends String> {
  @State final C hello;
  @State final X world;

  public INRETOREXC_Flow_Gen1(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT1")
  static String STEP1(@In String world) {
    return world;
  }

  @TransitFunction
  static Transition TRANSIT1(
      @InRetOrException ReturnValueOrException<String> worldRet, @StepRef Transition STEP2) {
    return STEP2;
  }

  @StepFunction(transit = "TRANSIT2")
  static String STEP2(@In String hello) {
    return hello;
  }

  @TransitFunction
  static Transition TRANSIT2(
      @InRetOrException ReturnValueOrException<String> helloRet, @StepRef Transition STEP3) {
    return STEP3;
  }

  @StepFunction(transit = "TRANSIT3")
  static <X extends String> X STEP3(@In X hello) {
    return hello;
  }

  @TransitFunction
  static <X extends String> Transition TRANSIT3(
      @InRetOrException ReturnValueOrException<X> helloRet, @StepRef Transition STEP4) {
    return STEP4;
  }

  @StepFunction(transit = "TRANSIT4")
  static <C extends X, X extends String> C STEP4(@In C hello, @In X world) {
    return hello;
  }

  @TransitFunction
  static <C extends X, X extends String> Transition TRANSIT4(
      @InRetOrException ReturnValueOrException<C> helloRet, @In X world, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", extendz = INRETOREXC_Flow_Gen1.class)
class INRETOREXC_Flow_Gen1_2<C2 extends X2, X2 extends String>
    extends INRETOREXC_Flow_Gen1<C2, X2> {
  @State final C2 hello;
  @State final X2 world;

  public INRETOREXC_Flow_Gen1_2(C2 hello, X2 world) {
    super(hello, world);
    this.hello = hello;
    this.world = world;
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class INRETOREXC_Flow_Gen2<C extends List<X>, X extends String> {
  @State final C hello;
  @State final X world;

  public INRETOREXC_Flow_Gen2(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT1")
  static String STEP1(@In String world) {
    return world;
  }

  @TransitFunction
  static Transition TRANSIT1(@In String world, @StepRef Transition STEP2) {
    return STEP2;
  }

  @StepFunction(transit = "TRANSIT2")
  static List<String> STEP2(@In List<String> hello) {
    return hello;
  }

  @TransitFunction
  static Transition TRANSIT2(@In List<String> hello, @StepRef Transition STEP3) {
    return STEP3;
  }

  @StepFunction(transit = "TRANSIT3")
  static <X extends String> List<X> STEP3(@In List<X> hello) {
    return hello;
  }

  @TransitFunction
  static <X extends String> Transition TRANSIT3(@In List<X> hello, @StepRef Transition STEP4) {
    return STEP4;
  }

  @StepFunction(transit = "TRANSIT4")
  static <C extends List<X>, X extends String> C STEP4(@In C hello, @In X world) {
    return hello;
  }

  @TransitFunction
  static <C extends List<X>, X extends String> Transition TRANSIT4(
      @In C hello, @In X world, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", extendz = INRETOREXC_Flow_Gen2.class)
class INRETOREXC_Flow_Gen2_2<C2 extends List<X2>, X2 extends String>
    extends INRETOREXC_Flow_Gen2<C2, X2> {
  @State final C2 hello;
  @State final X2 world;

  public INRETOREXC_Flow_Gen2_2(C2 hello, X2 world) {
    super(hello, world);
    this.hello = hello;
    this.world = world;
  }
}

/* @FlowType(firstStep = "STEP1", name = "BASE")
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

// --------------------------------------------------------------------------
*/
@GlobalFunctionContainer
class INRETOREXC_GlobalFunctionContainer2_1 {
  @GlobalFunction
  static String STEP1_SF(@In String world) {
    return world;
  }

  @GlobalFunction
  static Transition STEP1(
      @InRetOrException ReturnValueOrException<String> worldRet, @StepRef Transition STEP2_SF) {
    return STEP2_SF;
  }

  @GlobalFunction
  static String STEP2_SF(@In String hello) {
    return hello;
  }

  @GlobalFunction
  static Transition STEP2(
      @InRetOrException ReturnValueOrException<String> helloRet, @StepRef Transition STEP3_SF) {
    return STEP3_SF;
  }

  @GlobalFunction
  static <A extends String> A STEP3_SF(@In A hello) {
    return hello;
  }

  @GlobalFunction
  static <A extends String> Transition STEP3(
      @InRetOrException ReturnValueOrException<A> helloRet, @Terminal Transition STEP4_SF) {
    return STEP4_SF;
  }

  @GlobalFunction
  static <B extends G, G extends String> B STEP4_SF(@In B hello, @In G world) {
    return hello;
  }

  @GlobalFunction
  static <B extends G, G extends String> Transition STEP4(
      @InRetOrException ReturnValueOrException<B> helloRet, @In G world, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1_SF", name = "BASE")
class INRETOREXC_Flow_Gen_GlobalTransitionerCall1<C extends X, X extends String> {
  @State final C hello;
  @State final X world;

  public INRETOREXC_Flow_Gen_GlobalTransitionerCall1(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransitContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalTransit = "STEP1")
  static String STEP1_SF(@In String world) {
    return world;
  }

  @StepFunction(globalTransitContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalTransit = "STEP2")
  static String STEP2_SF(@In String hello) {
    return hello;
  }

  @StepFunction(globalTransitContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalTransit = "STEP3")
  static <X extends String> X STEP3_SF(@In X hello) {
    return hello;
  }

  @StepFunction(globalTransitContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalTransit = "STEP3")
  static <C extends X, X extends String> C STEP31_SF(@In C hello) {
    return hello;
  }

  @StepFunction(globalTransitContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalTransit = "STEP4")
  static <C extends X, X extends String> C STEP4_SF(@In C hello, @In X world) {
    return hello;
  }
}

@FlowType(firstStep = "STEP4_SF", extendz = INRETOREXC_Flow_Gen_GlobalTransitionerCall1.class)
class INRETOREXC_Flow_Gen_GlobalTransitionerCall2<C2 extends X2, X2 extends String>
    extends INRETOREXC_Flow_Gen_GlobalTransitionerCall1<C2, X2> {
  @State final C2 hello;
  @State final X2 world;

  public INRETOREXC_Flow_Gen_GlobalTransitionerCall2(C2 hello, X2 world) {
    super(hello, world);
    this.hello = hello;
    this.world = world;
  }
}

@FlowType(firstStep = "STEP1_SF", name = "BASE")
class INRETOREXC_Flow_Gen_GlobalFunctionCall1<C extends X, X extends String> {
  @State
  final C hello;
  @State final X world;

  public INRETOREXC_Flow_Gen_GlobalFunctionCall1(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP1_SF", transit = "STEP1")
  static String STEP1_SF(@In String world) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP1")
  static Transition STEP1(
          @InRetOrException ReturnValueOrException<String> worldRet, @StepRef Transition STEP2_SF) {
    return null;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP2_SF", transit = "STEP2")
  static String STEP2_SF(@In String hello) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP2")
  static Transition STEP2(
      @InRetOrException ReturnValueOrException<String> helloRet, @StepRef Transition STEP3_SF) {
    return null;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP3_SF", transit = "STEP3")
  static <X extends String> X STEP3_SF(@In X hello) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP3")
  static <X extends String> Transition STEP3(
      @InRetOrException ReturnValueOrException<X> helloRet, @Terminal Transition STEP4_SF) {
    return null;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP4_SF", transit = "STEP4")
  static <C extends X, X extends String> C STEP4_SF(@In C hello, @In X world) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_1.class, globalFunctionName = "STEP4")
  static <C extends X, X extends String> Transition STEP4(
      @InRetOrException ReturnValueOrException<C> helloRet, @In X world, @Terminal Transition END) {
    return null;
  }
}

@GlobalFunctionContainer
class INRETOREXC_GlobalFunctionContainer2_2 {
  @GlobalFunction
  static String STEP1_SF(@In String world) {
    System.out.println("Global STEP 1");
    return world;
  }

  @GlobalFunction
  static Transition STEP1(
      @InRetOrException ReturnValueOrException<String> worldRet, @StepRef Transition STEP2_SF) {
    System.out.println("Global STEP 1");
    return STEP2_SF;
  }

  @GlobalFunction
  static List<String> STEP2_SF(@In List<String> hello) {
    System.out.println("Global STEP 2");
    return hello;
  }

  @GlobalFunction
  static Transition STEP2(
      @InRetOrException ReturnValueOrException<List<String>> helloRet,
      @StepRef Transition STEP3_SF) {
    System.out.println("Global STEP 2");
    return STEP3_SF;
  }

  @GlobalFunction
  static <J extends String> List<J> STEP3_SF(@In List<J> hello) {
    System.out.println("Global STEP 3");
    return hello;
  }

  @GlobalFunction
  static <J extends String> Transition STEP3(
      @InRetOrException ReturnValueOrException<List<J>> helloRet, @StepRef Transition STEP4_SF) {
    System.out.println("Global STEP 3");
    return STEP4_SF;
  }

  @GlobalFunction
  static <K extends List<L>, L extends String> K STEP4_SF(@In K hello, @In L world) {
    System.out.println("Global STEP 4");
    return hello;
  }

  @GlobalFunction
  static <K extends List<L>, L extends String> Transition STEP4(
      @InRetOrException ReturnValueOrException<K> helloRet, @In L world, @Terminal Transition END) {
    System.out.println("Global STEP 4");
    return END;
  }
}

@FlowType(firstStep = "STEP1_SF", name = "BASE")
class INRETOREXC_Flow_Gen_GlobalFunctionCall2<C extends List<X>, X extends String> {
  @State final C hello;
  @State final X world;

  public INRETOREXC_Flow_Gen_GlobalFunctionCall2(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP1_SF", transit = "STEP1")
  static String STEP1_SF(@In String world) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP1")
  static Transition STEP1(
      @InRetOrException ReturnValueOrException<String> worldRet, @StepRef Transition STEP2_SF) {
    return null;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP2_SF", transit = "STEP2")
  static List<String> STEP2_SF(@In List<String> hello) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP2")
  static Transition STEP2(
      @InRetOrException ReturnValueOrException<List<String>> helloRet,
      @StepRef Transition STEP3_SF) {
    return null;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP3_SF", transit = "STEP3")
  static <X extends String> List<X> STEP3_SF(@In List<X> hello) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP3")
  static <X extends String> Transition STEP3(
      @InRetOrException ReturnValueOrException<List<X>> helloRet, @StepRef Transition STEP4_SF) {
    return null;
  }

  @StepCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP4_SF", transit = "STEP4")
  static <C extends List<X>, X extends String> C STEP4_SF(@In C hello, @In X world) {
    return null;
  }

  @TransitCall(globalFunctionContainer = INRETOREXC_GlobalFunctionContainer2_2.class, globalFunctionName = "STEP4")
  static <C extends List<X>, X extends String> Transition STEP4(
      @InRetOrException ReturnValueOrException<C> helloRet, @In X world, @Terminal Transition END) {
    return null;
  }
}
