package com.flower.flows;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.step.FlowFactory;
import com.flower.anno.params.step.FlowRepo;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFactoryPrm;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowId;
import com.flower.conf.FlowRepoPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowFactoryPrmTest {
  @Test
  void test1() {
    Flower flower = new Flower();
    flower.registerFlow(F_ParentFlow_ErrorNonParameterizedType.class);
    flower.registerFlow(F_ChildFlow.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("should be a parameterized type FlowFactoryPrm<FLOW_TYPE>"));
  }

  @Test
  void test2() {
    Flower flower = new Flower();
    flower.registerFlow(F_ParentFlow_WrongTypeName.class);
    flower.registerFlow(F_ChildFlow.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("Flow factory parameter generic subtype mismatch"));
  }

  @Test
  void test3() {
    Flower flower = new Flower();
    flower.registerFlow(F_ParentFlow_WrongType.class);
    flower.registerFlow(F_ChildFlow.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("Flow factory parameter generic subtype mismatch"));
  }

  @Test
  void test_OK() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(F_ParentFlow.class);
    flower.registerFlow(F_ChildFlow.class);

    flower.initialize();

    FlowExec<F_ParentFlow> exec = flower.getFlowExec(F_ParentFlow.class);
    exec.runFlow(new F_ParentFlow()).getFuture().get();
  }

  @Test
  void test_OK2() throws InterruptedException, ExecutionException {
    Flower flower = new Flower();
    flower.registerFlow(F_ParentFlow2.class);
    flower.registerFlow(F_ChildFlow2.class);

    flower.initialize();

    FlowExec<F_ParentFlow2> exec = flower.getFlowExec(F_ParentFlow2.class);
    FlowFuture<F_ParentFlow2> future = exec.runFlow(new F_ParentFlow2());
    future.getFuture().get();
  }
}

@FlowType(firstStep = "step1")
class F_ParentFlow2 {
  @State
  String childFlowId;

  @SimpleStepFunction
  static Transition step1(
      @StepRef Transition waitStep,
      @FlowFactory(flowTypeName = "F_ChildFlow") FlowFactoryPrm<F_ChildFlow2> flowFactory,
      @Out OutPrm<String> childFlowId) {
    System.out.println("F_ParentFlow2 step1 start");

    FlowId id = flowFactory.runChildFlow(new F_ChildFlow2()).getFlowId();
    String flowIdToken = flowFactory.serializeFlowId(id);

    System.out.printf("F_ParentFlow2 childFlow created %s%n", flowIdToken);

    childFlowId.setOutValue(flowIdToken);

    System.out.println("F_ParentFlow2 step1 end");

    return waitStep;
  }

  @SimpleStepFunction
  static ListenableFuture<Transition> waitStep(
          @Terminal Transition end, @FlowRepo FlowRepoPrm flowRepo, @In String childFlowId) {
    System.out.println("F_ParentFlow2 waitStep start");
    System.out.println("F_ParentFlow2 waiting for " + childFlowId);

    ListenableFuture<F_ChildFlow2> future =
        (ListenableFuture<F_ChildFlow2>)
            flowRepo.getFlowFuture(flowRepo.deserializeFlowId(childFlowId));
    return Futures.transform(
        future,
        flow2 -> {
          System.out.println("F_ParentFlow2 waitStep end message [" + flow2.message + "]");
          return end;
        },
        MoreExecutors.directExecutor());
  }
}

@FlowType(firstStep = "step1", name = "F_ChildFlow")
class F_ChildFlow2 {
  @State String message = "started";

  @SimpleStepFunction
  static Transition step1(@StepRef Transition endStep) {
    System.out.println("F_ChildFlow2 step1");

    return endStep.setDelay(Duration.ofMillis(2000));
  }

  @SimpleStepFunction
  static Transition endStep(@Terminal Transition end, @Out OutPrm<String> message) {
    System.out.println("F_ChildFlow2 endStep");
    message.setOutValue("finished");

    return end;
  }
}

@FlowType(firstStep = "step")
class F_ParentFlow {
  @SimpleStepFunction
  static Transition step(
      @Terminal Transition end,
      @FlowFactory(flowTypeName = "F_ChildFlow") FlowFactoryPrm<F_ChildFlow> flowFactory)
      throws ExecutionException, InterruptedException {
    System.out.println("F_ParentFlow step start");

    flowFactory.runChildFlow(new F_ChildFlow()).getFuture().get();

    System.out.println("F_ParentFlow step end");

    return end;
  }
}

@FlowType(firstStep = "step", name = "F_ChildFlow")
class F_ChildFlow {
  @SimpleStepFunction
  static Transition step(@Terminal Transition end) {
    System.out.println("F_ChildFlow step");
    return end;
  }
}

@FlowType(firstStep = "step")
class F_ParentFlow_WrongTypeName {
  @SimpleStepFunction
  static Transition step(
      @Terminal Transition end,
      @FlowFactory(flowTypeName = "F_ChildFlow") FlowFactoryPrm<F_ParentFlow> flowFactory) {
    return end;
  }
}

@FlowType(firstStep = "step")
class F_ParentFlow_WrongType {
  @SimpleStepFunction
  static Transition step(
      @Terminal Transition end,
      @FlowFactory(flowType = F_ChildFlow.class) FlowFactoryPrm<F_ParentFlow> flowFactory) {
    return end;
  }
}

@FlowType(firstStep = "step")
class F_ParentFlow_ErrorNonParameterizedType {
  @SimpleStepFunction
  static Transition step(
      @Terminal Transition end,
      @FlowFactory(flowTypeName = "F_ChildFlow") FlowFactoryPrm flowFactory) {
    return end;
  }
}
