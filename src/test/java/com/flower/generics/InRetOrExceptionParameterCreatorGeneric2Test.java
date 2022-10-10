package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
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

public class InRetOrExceptionParameterCreatorGeneric2Test {
  @Test
  void test_I1_Flow_O_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_O_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_O_to_O_OK> helloWorldExec = flower.getFlowExec(I1_Flow_O_to_O_OK.class);
    FlowFuture<I1_Flow_O_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_O_to_O_OK<>("Hello", " world!"));

    I1_Flow_O_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_CS_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_CS_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_CS_to_O_OK> helloWorldExec = flower.getFlowExec(I1_Flow_CS_to_O_OK.class);
    FlowFuture<I1_Flow_CS_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_CS_to_O_OK<>("Hello", " world!"));

    I1_Flow_CS_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Str_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_Str_to_O_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Str_to_O_OK> helloWorldExec = flower.getFlowExec(I1_Flow_Str_to_O_OK.class);
    FlowFuture<I1_Flow_Str_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Str_to_O_OK<>("Hello", " world!"));

    I1_Flow_Str_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_O_to_CS_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_O_to_CS_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_O_to_CS_Fail> helloWorldExec = flower.getFlowExec(I1_Flow_O_to_CS_Fail.class);
    FlowFuture<I1_Flow_O_to_CS_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_O_to_CS_Fail<>("Hello", " world!"));

    I1_Flow_O_to_CS_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_CS_to_CS_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_CS_to_CS_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_CS_to_CS_OK> helloWorldExec = flower.getFlowExec(I1_Flow_CS_to_CS_OK.class);
    FlowFuture<I1_Flow_CS_to_CS_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_CS_to_CS_OK<>("Hello", " world!"));

    I1_Flow_CS_to_CS_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Str_to_CS_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_Str_to_CS_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Str_to_CS_OK> helloWorldExec = flower.getFlowExec(I1_Flow_Str_to_CS_OK.class);
    FlowFuture<I1_Flow_Str_to_CS_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Str_to_CS_OK<>("Hello", " world!"));

    I1_Flow_Str_to_CS_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_O_to_Str_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_O_to_Str_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_O_to_Str_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_O_to_Str_Fail.class);
    FlowFuture<I1_Flow_O_to_Str_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_O_to_Str_Fail<>("Hello", " world!"));

    I1_Flow_O_to_Str_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_CS_to_Str_Fail() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_CS_to_Str_Fail.class);
    flower.initialize();

    FlowExec<I1_Flow_CS_to_Str_Fail> helloWorldExec =
        flower.getFlowExec(I1_Flow_CS_to_Str_Fail.class);
    FlowFuture<I1_Flow_CS_to_Str_Fail> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_CS_to_Str_Fail<>("Hello", " world!"));

    I1_Flow_CS_to_Str_Fail state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_I1_Flow_Str_to_Str_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(I1_Flow_Str_to_Str_OK.class);
    flower.initialize();

    FlowExec<I1_Flow_Str_to_Str_OK> helloWorldExec =
        flower.getFlowExec(I1_Flow_Str_to_Str_OK.class);
    FlowFuture<I1_Flow_Str_to_Str_OK> flowFuture =
        helloWorldExec.runFlow(new I1_Flow_Str_to_Str_OK<>("Hello", " world!"));

    I1_Flow_Str_to_Str_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }
}

// ------------ BASE TEST------------

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_O_to_O_OK<C> {
  @State final C hello;
  @State final C world;

  public I1_Flow_O_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_CS_to_O_OK<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_CS_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C extends CharSequence> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Str_to_O_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Str_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C extends String> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_O_to_CS_Fail<C> {
  @State final C hello;
  @State final C world;

  public I1_Flow_O_to_CS_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_CS_to_CS_OK<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_CS_to_CS_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C extends CharSequence> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Str_to_CS_OK<C extends String> {
  @State
  final C hello;
  @State final C world;

  public I1_Flow_Str_to_CS_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C extends String> Transition TRANSIT(
          @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_O_to_Str_Fail<C> {
  @State final C hello;
  @State final C world;

  public I1_Flow_O_to_Str_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_CS_to_Str_Fail<C extends CharSequence> {
  @State final C hello;
  @State final C world;

  public I1_Flow_CS_to_Str_Fail(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends CharSequence> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C extends CharSequence> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}

@FlowType(firstStep = "HELLO_STEP")
class I1_Flow_Str_to_Str_OK<C extends String> {
  @State final C hello;
  @State final C world;

  public I1_Flow_Str_to_Str_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @StepFunction(transit = "TRANSIT")
  static <C extends String> C HELLO_STEP(@In C hello, @In C world) {
    System.out.print(hello);
    return world;
  }

  @TransitFunction
  static <C extends String> Transition TRANSIT(
      @InRetOrException ReturnValueOrException<C> world, @Terminal Transition END) {
    System.out.println(world.returnValue().get());
    return END;
  }
}
