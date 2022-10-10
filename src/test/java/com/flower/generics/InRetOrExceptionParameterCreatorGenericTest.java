package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.params.common.In;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

public class InRetOrExceptionParameterCreatorGenericTest {
  @Test
  void test_I1_Flow_Call_O_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_O_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_O_to_O_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_O_to_O_OK.class);
    FlowFuture<I1_Flow_Call_O_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_O_to_O_OK<>("Hello", " world!"));

    I1_Flow_Call_O_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_CS_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_CS_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_CS_to_O_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_CS_to_O_OK.class);
    FlowFuture<I1_Flow_Call_CS_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_CS_to_O_OK<>("Hello", " world!"));

    I1_Flow_Call_CS_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_Str_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_Str_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_Str_to_O_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_Str_to_O_OK.class);
    FlowFuture<I1_Flow_Call_Str_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_Str_to_O_OK<>("Hello", " world!"));

    I1_Flow_Call_Str_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_O_to_CS_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_O_to_CS_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_O_to_CS_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_O_to_CS_Fail.class);
    FlowFuture<I1_Flow_Call_O_to_CS_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_O_to_CS_Fail<>("Hello", " world!"));

    I1_Flow_Call_O_to_CS_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_CS_to_CS_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_CS_to_CS_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_CS_to_CS_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_CS_to_CS_OK.class);
    FlowFuture<I1_Flow_Call_CS_to_CS_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_CS_to_CS_OK<>("Hello", " world!"));

    I1_Flow_Call_CS_to_CS_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_Str_to_CS_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_Str_to_CS_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_Str_to_CS_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_Str_to_CS_OK.class);
    FlowFuture<I1_Flow_Call_Str_to_CS_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_Str_to_CS_OK<>("Hello", " world!"));

    I1_Flow_Call_Str_to_CS_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_O_to_Str_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_O_to_Str_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_O_to_Str_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_O_to_Str_Fail.class);
    FlowFuture<I1_Flow_Call_O_to_Str_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_O_to_Str_Fail<>("Hello", " world!"));

    I1_Flow_Call_O_to_Str_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_CS_to_Str_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_CS_to_Str_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_CS_to_Str_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_CS_to_Str_Fail.class);
    FlowFuture<I1_Flow_Call_CS_to_Str_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_CS_to_Str_Fail<>("Hello", " world!"));

    I1_Flow_Call_CS_to_Str_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Call_Str_to_Str_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Call_Str_to_Str_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Call_Str_to_Str_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Call_Str_to_Str_OK.class);
    FlowFuture<I1_Flow_Call_Str_to_Str_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Call_Str_to_Str_OK<>("Hello", " world!"));

    I1_Flow_Call_Str_to_Str_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  // ------------ GLOBAL CALL TEST------------

  @Test
  void test_I1_Flow_Global_O_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_O_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_O_to_O_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_O_to_O_OK.class);
    FlowFuture<I1_Flow_Global_O_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_O_to_O_OK<>("Hello", " world!"));

    I1_Flow_Global_O_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_CS_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_CS_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_CS_to_O_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_CS_to_O_OK.class);
    FlowFuture<I1_Flow_Global_CS_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_CS_to_O_OK<>("Hello", " world!"));

    I1_Flow_Global_CS_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_Str_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_Str_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_Str_to_O_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_Str_to_O_OK.class);
    FlowFuture<I1_Flow_Global_Str_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_Str_to_O_OK<>("Hello", " world!"));

    I1_Flow_Global_Str_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_O_to_CS_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_O_to_CS_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_O_to_CS_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_O_to_CS_Fail.class);
    FlowFuture<I1_Flow_Global_O_to_CS_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_O_to_CS_Fail<>("Hello", " world!"));

    I1_Flow_Global_O_to_CS_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_CS_to_CS_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_CS_to_CS_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_CS_to_CS_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_CS_to_CS_OK.class);
    FlowFuture<I1_Flow_Global_CS_to_CS_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_CS_to_CS_OK<>("Hello", " world!"));

    I1_Flow_Global_CS_to_CS_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_Str_to_CS_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_Str_to_CS_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_Str_to_CS_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_Str_to_CS_OK.class);
    FlowFuture<I1_Flow_Global_Str_to_CS_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_Str_to_CS_OK<>("Hello", " world!"));

    I1_Flow_Global_Str_to_CS_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_O_to_Str_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_O_to_Str_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_O_to_Str_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_O_to_Str_Fail.class);
    FlowFuture<I1_Flow_Global_O_to_Str_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_O_to_Str_Fail<>("Hello", " world!"));

    I1_Flow_Global_O_to_Str_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_CS_to_Str_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_CS_to_Str_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_CS_to_Str_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_CS_to_Str_Fail.class);
    FlowFuture<I1_Flow_Global_CS_to_Str_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_CS_to_Str_Fail<>("Hello", " world!"));

    I1_Flow_Global_CS_to_Str_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Global_Str_to_Str_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(I1_GlobalFunctionContainer.class);
    flower.registerFlow(I1_Flow_Global_Str_to_Str_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Global_Str_to_Str_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Global_Str_to_Str_OK.class);
    FlowFuture<I1_Flow_Global_Str_to_Str_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Global_Str_to_Str_OK<>("Hello", " world!"));

    I1_Flow_Global_Str_to_Str_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }
}

@GlobalFunctionContainer
class I1_GlobalFunctionContainer {
  @GlobalFunction
  static <X> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<X> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }

  @GlobalFunction
  static <Y extends CharSequence> Transition TRANSIT2(
      @InRetOrException ReturnValueOrException<Y> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }

  @GlobalFunction
  static <Z extends String> Transition TRANSIT3(
      @InRetOrException ReturnValueOrException<Z> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

// ------------ CALL TEST------------

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_O_to_O_OK<C> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_O_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT")
  static <C> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_CS_to_O_OK<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_CS_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT")
  static <C extends CharSequence> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_Str_to_O_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_Str_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT")
  static <C extends String> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_O_to_CS_Fail<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_O_to_CS_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT2")
  static <C extends CharSequence> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_CS_to_CS_OK<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_CS_to_CS_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT2")
  static <C extends CharSequence> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_Str_to_CS_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_Str_to_CS_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT2")
  static <C extends String> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_O_to_Str_Fail<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_O_to_Str_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT3")
  static <C extends String> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_CS_to_Str_Fail<C extends String> {
  @State
  final C hello;
  @State final C world;

  public I1_Flow_Call_CS_to_Str_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT3")
  static <C extends String> Transition TRANSIT(
          @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Call_Str_to_Str_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Call_Str_to_Str_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitCall(globalFunctionName = "TRANSIT3")
  static <C extends String> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    return null;
  }
}

// ------------ GLOBAL CALL TEST------------

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_O_to_O_OK<C> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_O_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_CS_to_O_OK<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_CS_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_Str_to_O_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_Str_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_O_to_CS_Fail<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_O_to_CS_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT2")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_CS_to_CS_OK<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_CS_to_CS_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT2")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_Str_to_CS_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_Str_to_CS_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT2")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_O_to_Str_Fail<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_O_to_Str_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT3")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_CS_to_Str_Fail<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_CS_to_Str_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT3")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Global_Str_to_Str_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Global_Str_to_Str_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(globalTransit = "TRANSIT3")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }
}
