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
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class InParameterCreatorGenericBase2Test {
  @Test
  void test_IN_Flow_Gen1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(IN_Flow_Gen1.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen1> helloWorldExec = flower.getFlowExec(IN_Flow_Gen1.class);
    FlowFuture<IN_Flow_Gen1> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Gen1<>("Hello", " world!"));

    IN_Flow_Gen1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Gen1_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(IN_Flow_Gen1.class);
    flower.registerFlow(IN_Flow_Gen1_2.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen1_2> helloWorldExec = flower.getFlowExec(IN_Flow_Gen1_2.class);
    FlowFuture<IN_Flow_Gen1_2> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Gen1_2<>("Hello", " world!"));

    IN_Flow_Gen1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Gen2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(IN_Flow_Gen2.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen2> helloWorldExec = flower.getFlowExec(IN_Flow_Gen2.class);
    FlowFuture<IN_Flow_Gen2> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Gen2<>(ImmutableList.of("Hello"), " world!"));

    IN_Flow_Gen2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Gen2_2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(IN_Flow_Gen2.class);
    flower.registerFlow(IN_Flow_Gen2_2.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen2_2> helloWorldExec = flower.getFlowExec(IN_Flow_Gen2_2.class);
    FlowFuture<IN_Flow_Gen2_2> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Gen2_2<>(ImmutableList.of("Hello"), " world!"));

    IN_Flow_Gen2_2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }

  // --------------------------------------------------------------------------

  @Test
  void test_IN_Flow_Gen_GlobalFunctionCall1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(IN_GlobalFunctionContainer2_1.class);
    flower.registerFlow(IN_Flow_Gen_GlobalFunctionCall1.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen_GlobalFunctionCall1> helloWorldExec =
        flower.getFlowExec(IN_Flow_Gen_GlobalFunctionCall1.class);
    FlowFuture<IN_Flow_Gen_GlobalFunctionCall1> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Gen_GlobalFunctionCall1<>("Hello", " world!"));

    IN_Flow_Gen_GlobalFunctionCall1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Gen_GlobalFunctionCall2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(IN_GlobalFunctionContainer2_2.class);
    flower.registerFlow(IN_Flow_Gen_GlobalFunctionCall2.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen_GlobalFunctionCall2> helloWorldExec =
        flower.getFlowExec(IN_Flow_Gen_GlobalFunctionCall2.class);
    FlowFuture<IN_Flow_Gen_GlobalFunctionCall2> flowFuture =
        helloWorldExec.runFlow(
            new IN_Flow_Gen_GlobalFunctionCall2<>(ImmutableList.of("Hello"), " world!"));

    IN_Flow_Gen_GlobalFunctionCall2 state = flowFuture.getFuture().get();
    assertEquals(ImmutableList.of("Hello"), state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Gen_GlobalTransitionerCall1() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(IN_GlobalFunctionContainer2_1.class);
    flower.registerFlow(IN_Flow_Gen_GlobalTransitionerCall1.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen_GlobalTransitionerCall1> helloWorldExec =
        flower.getFlowExec(IN_Flow_Gen_GlobalTransitionerCall1.class);
    FlowFuture<IN_Flow_Gen_GlobalTransitionerCall1> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Gen_GlobalTransitionerCall1<>("Hello", " world!"));

    IN_Flow_Gen_GlobalTransitionerCall1 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_IN_Flow_Gen_GlobalTransitionerCall2() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(IN_GlobalFunctionContainer2_1.class);
    flower.registerFlow(IN_Flow_Gen_GlobalTransitionerCall1.class);
    flower.registerFlow(IN_Flow_Gen_GlobalTransitionerCall2.class);
    flower.initialize();

    FlowExec<IN_Flow_Gen_GlobalTransitionerCall2> helloWorldExec =
        flower.getFlowExec(IN_Flow_Gen_GlobalTransitionerCall2.class);
    FlowFuture<IN_Flow_Gen_GlobalTransitionerCall2> flowFuture =
        helloWorldExec.runFlow(new IN_Flow_Gen_GlobalTransitionerCall2<>("Hello", " world!"));

    IN_Flow_Gen_GlobalTransitionerCall2 state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class IN_Flow_Gen1<C extends X, X extends String> {
  @State final C hello;
  @State final X world;

  public IN_Flow_Gen1(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @SimpleStepFunction
  static Transition STEP1(@In String world, @StepRef Transition STEP2) {
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In String hello, @StepRef Transition STEP3) {
    return STEP3;
  }

  @SimpleStepFunction
  static <X extends String> Transition STEP3(@In X hello, @StepRef Transition STEP4) {
    return STEP4;
  }

  @SimpleStepFunction
  static <C extends X, X extends String> Transition STEP4(
      @In C hello, @In X world, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", extendz = "BASE")
class IN_Flow_Gen1_2<C2 extends X2, X2 extends String> extends IN_Flow_Gen1<C2, X2> {
  @State final C2 hello;
  @State final X2 world;

  public IN_Flow_Gen1_2(C2 hello, X2 world) {
    super(hello, world);
    this.hello = hello;
    this.world = world;
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class IN_Flow_Gen2<C extends List<X>, X extends String> {
  @State final C hello;
  @State final X world;

  public IN_Flow_Gen2(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @SimpleStepFunction
  static Transition STEP1(@In String world, @StepRef Transition STEP2) {
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In List<String> hello, @StepRef Transition STEP3) {
    return STEP3;
  }

  @SimpleStepFunction
  static <X extends String> Transition STEP3(@In List<X> hello, @StepRef Transition STEP4) {
    return STEP4;
  }

  @SimpleStepFunction
  static <C extends List<X>, X extends String> Transition STEP4(
      @In C hello, @In X world, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", extendz = "BASE")
class IN_Flow_Gen2_2<C2 extends List<X2>, X2 extends String> extends IN_Flow_Gen2<C2, X2> {
  @State final C2 hello;
  @State final X2 world;

  public IN_Flow_Gen2_2(C2 hello, X2 world) {
    super(hello, world);
    this.hello = hello;
    this.world = world;
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class IN_Flow_Gen1_1<C extends X, X extends String> {
  @State final C hello;
  @State final X world;

  public IN_Flow_Gen1_1(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @SimpleStepFunction
  static Transition STEP1(@In String world, @StepRef Transition STEP2) {
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In String hello, @StepRef Transition STEP3) {
    return STEP3;
  }

  @SimpleStepFunction
  static <X extends String> Transition STEP3(@In X hello, @StepRef Transition STEP4) {
    return STEP4;
  }

  @SimpleStepFunction
  static <C extends X, X extends String> Transition STEP4(
      @In C hello, @In X world, @Terminal Transition END) {
    return END;
  }
}

// --------------------------------------------------------------------------

@GlobalFunctionContainer
class IN_GlobalFunctionContainer2_1 {
  @GlobalFunction
  static Transition STEP1(@In String world, @StepRef Transition STEP2) {
    return STEP2;
  }

  @GlobalFunction
  static Transition STEP2(@In String hello, @StepRef Transition STEP3) {
    return STEP3;
  }

  @GlobalFunction
  static <A extends String> Transition STEP3(@In A hello, @Terminal Transition STEP4) {
    return STEP4;
  }

  @GlobalFunction
  static <B extends G, G extends String> Transition STEP4(
      @In B hello, @In G world, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class IN_Flow_Gen_GlobalTransitionerCall1<C extends X, X extends String> {
  @State
  final C hello;
  @State final X world;

  public IN_Flow_Gen_GlobalTransitionerCall1(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "STEP1")
  static void STEP1(@In String world) {}

  @StepFunction(globalTransit = "STEP2")
  static void STEP2(@In String hello) {}

  @StepFunction(globalTransit = "STEP3")
  static <X extends String> void STEP3(@In X hello) {}

  @StepFunction(globalTransit = "STEP3")
  static <C extends X, X extends String> void STEP31(@In C hello) {}

  @StepFunction(globalTransit = "STEP4")
  static <C extends X, X extends String> void STEP4(@In C hello, @In X world) {}
}

@FlowType(firstStep = "STEP4", extendz = "BASE")
class IN_Flow_Gen_GlobalTransitionerCall2<C2 extends X2, X2 extends String>
    extends IN_Flow_Gen_GlobalTransitionerCall1<C2, X2> {
  @State final C2 hello;
  @State final X2 world;

  public IN_Flow_Gen_GlobalTransitionerCall2(C2 hello, X2 world) {
    super(hello, world);
    this.hello = hello;
    this.world = world;
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class IN_Flow_Gen_GlobalFunctionCall1<C extends X, X extends String> {
  @State final C hello;
  @State final X world;

  public IN_Flow_Gen_GlobalFunctionCall1(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @SimpleStepCall(globalFunctionName = "STEP1")
  static Transition STEP1(@In String world, @StepRef Transition STEP2) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP2")
  static Transition STEP2(@In String hello, @StepRef Transition STEP3) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP3")
  static <X extends String> Transition STEP3(@In X hello, @Terminal Transition STEP4) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP4")
  static <C extends X, X extends String> Transition STEP4(
      @In C hello, @In X world, @Terminal Transition END) {
    return null;
  }
}

@GlobalFunctionContainer
class IN_GlobalFunctionContainer2_2 {
  @GlobalFunction
  static Transition STEP1(@In String world, @StepRef Transition STEP2) {
    System.out.println("Global STEP 1");
    return STEP2;
  }

  @GlobalFunction
  static Transition STEP2(@In List<String> hello, @StepRef Transition STEP3) {
    System.out.println("Global STEP 2");
    return STEP3;
  }

  @GlobalFunction
  static <J extends String> Transition STEP3(@In List<J> hello, @StepRef Transition STEP4) {
    System.out.println("Global STEP 3");
    return STEP4;
  }

  @GlobalFunction
  static <K extends List<L>, L extends String> Transition STEP4(
      @In K hello, @In L world, @Terminal Transition END) {
    System.out.println("Global STEP 4");
    return END;
  }
}

@FlowType(firstStep = "STEP1", name = "BASE")
class IN_Flow_Gen_GlobalFunctionCall2<C extends List<X>, X extends String> {
  @State final C hello;
  @State final X world;

  public IN_Flow_Gen_GlobalFunctionCall2(C hello, X world) {
    this.hello = hello;
    this.world = world;
  }

  @SimpleStepCall(globalFunctionName = "STEP1")
  static Transition STEP1(@In String world, @StepRef Transition STEP2) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP2")
  static Transition STEP2(@In List<String> hello, @StepRef Transition STEP3) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP3")
  static <X extends String> Transition STEP3(@In List<X> hello, @StepRef Transition STEP4) {
    return null;
  }

  @SimpleStepCall(globalFunctionName = "STEP4")
  static <C extends List<X>, X extends String> Transition STEP4(
      @In C hello, @In X world, @Terminal Transition END) {
    return null;
  }
}
