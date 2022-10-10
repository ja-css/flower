package com.flower.generics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

public class GenericInheritedTest {
  @Test
  void test_G2_Flow_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(G2_Flow_O_to_O_OK.class);
    flower.initialize();

    FlowExec<G2_Flow_O_to_O_OK> helloWorldExec = flower.getFlowExec(G2_Flow_O_to_O_OK.class);
    FlowFuture<G2_Flow_O_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new G2_Flow_O_to_O_OK("Hello", " world!"));

    G2_Flow_O_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_G2_ChildFlow_O_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(G2_Flow_O_to_O_OK.class);
    flower.registerFlow(G2_ChildFlow_O_to_O_OK.class);
    flower.initialize();

    FlowExec<G2_ChildFlow_O_to_O_OK> helloWorldExec =
        flower.getFlowExec(G2_ChildFlow_O_to_O_OK.class);
    FlowFuture<G2_ChildFlow_O_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new G2_ChildFlow_O_to_O_OK("Hello", " world!"));

    G2_ChildFlow_O_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_G2_1_ChildFlow_O_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(G2_Flow_O_to_O_OK.class);
    flower.registerFlow(G2_1_ChildFlow_O_to_O_OK.class);
    flower.initialize();

    FlowExec<G2_1_ChildFlow_O_to_O_OK> helloWorldExec =
        flower.getFlowExec(G2_1_ChildFlow_O_to_O_OK.class);
    FlowFuture<G2_1_ChildFlow_O_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new G2_1_ChildFlow_O_to_O_OK("Hello", " world!"));

    G2_1_ChildFlow_O_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }

  @Test
  void test_G2_ChildFlow2_O_to_O_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(G2_Flow_O_to_O_OK.class);
    flower.registerFlow(G2_ChildFlow_O_to_O_OK.class);
    flower.registerFlow(G2_ChildFlow2_O_to_O_OK.class);
    flower.initialize();

    FlowExec<G2_ChildFlow2_O_to_O_OK> helloWorldExec =
        flower.getFlowExec(G2_ChildFlow2_O_to_O_OK.class);
    FlowFuture<G2_ChildFlow2_O_to_O_OK> flowFuture =
        helloWorldExec.runFlow(new G2_ChildFlow2_O_to_O_OK(new Object(), "Hello", " world!"));

    G2_ChildFlow2_O_to_O_OK state = flowFuture.getFuture().get();
    assertEquals("Hello", state.hello);
    assertEquals(" world!", state.world);
  }
}

@FlowType(firstStep = "HELLO_STEP", name = "G2_FLOW")
class G2_Flow_O_to_O_OK<C> {
  @State
  final C hello;
  @State final C world;

  public G2_Flow_O_to_O_OK(C hello, C world) {
    this.hello = hello;
    this.world = world;
  }

  @SimpleStepFunction
  static <C> Transition HELLO_STEP(@In C hello, @In C world, @Terminal Transition END) {
    System.out.print(hello);
    System.out.print(world);
    return END;
  }
}

@FlowType(extendz = "G2_FLOW", name = "G2_CHILD_FLOW")
class G2_ChildFlow_O_to_O_OK<Z extends CharSequence> extends G2_Flow_O_to_O_OK<Z> {
  public G2_ChildFlow_O_to_O_OK(Z hello, Z world) {
    super(hello, world);
  }
}

@FlowType(extendz = "G2_FLOW", name = "G2_1_CHILD_FLOW")
class G2_1_ChildFlow_O_to_O_OK<Z extends CharSequence> extends G2_Flow_O_to_O_OK<Z> {
  public G2_1_ChildFlow_O_to_O_OK(Z hello, Z world) {
    super(hello, world);
  }

  @SimpleStepFunction
  static <Z extends CharSequence> Transition HELLO_STEP(
      @In Z hello, @In Z world, @Terminal Transition END) {
    System.out.print(hello);
    System.out.print(world);
    return END;
  }
}

@FlowType(extendz = "G2_CHILD_FLOW")
class G2_ChildFlow2_O_to_O_OK<X, Y extends String & List> extends G2_ChildFlow_O_to_O_OK<Y> {
  final X field1;

  public G2_ChildFlow2_O_to_O_OK(X field1, Y hello, Y world) {
    super(hello, world);
    this.field1 = field1;
  }
}
