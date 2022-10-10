package com.flower.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

public class ExceptionsTest {
  static final String EXCEPTION_MESSAGE = "Test Exception Message";
  static final Exception EXCEPTION = new Exception(ExceptionsTest.EXCEPTION_MESSAGE);

  @Test
  void test1() {
    Flower flower = new Flower();
    flower.registerFlow(SimpleStepException.class);
    flower.initialize();

    FlowExec<SimpleStepException> helloWorldRunner = flower.getFlowExec(SimpleStepException.class);

    FlowFuture<SimpleStepException> future = helloWorldRunner.runFlow(new SimpleStepException());

    ExecutionException e = assertThrows(ExecutionException.class, () -> future.getFuture().get());
    assertEquals(e.getCause(), EXCEPTION);
  }

  @Test
  void test2() {
    Flower flower = new Flower();
    flower.registerFlow(StepFunctionException.class);
    flower.initialize();

    FlowExec<StepFunctionException> helloWorldRunner =
        flower.getFlowExec(StepFunctionException.class);

    FlowFuture<StepFunctionException> future =
        helloWorldRunner.runFlow(new StepFunctionException());

    ExecutionException e = assertThrows(ExecutionException.class, () -> future.getFuture().get());
    assertEquals(e.getCause(), EXCEPTION);
  }

  @Test
  void test3() {
    Flower flower = new Flower();
    flower.registerFlow(TransitFunctionException.class);
    flower.initialize();

    FlowExec<TransitFunctionException> helloWorldRunner =
        flower.getFlowExec(TransitFunctionException.class);

    FlowFuture<TransitFunctionException> future =
        helloWorldRunner.runFlow(new TransitFunctionException());

    ExecutionException e = assertThrows(ExecutionException.class, () -> future.getFuture().get());
    assertEquals(e.getCause(), EXCEPTION);
  }

  @Test
  void test4() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(InRetOrExceptionCatchesStepException.class);
    flower.initialize();

    FlowExec<InRetOrExceptionCatchesStepException> helloWorldRunner =
        flower.getFlowExec(InRetOrExceptionCatchesStepException.class);

    FlowFuture<InRetOrExceptionCatchesStepException> future =
        helloWorldRunner.runFlow(new InRetOrExceptionCatchesStepException());

    InRetOrExceptionCatchesStepException flow = future.getFuture().get();
    assertEquals(flow.stepException, EXCEPTION);
  }

  @Test
  void test5() {
    Flower flower = new Flower();
    flower.registerFlow(InRetOrExceptionCatchesStepExceptionTransitThrows.class);
    flower.initialize();

    FlowExec<InRetOrExceptionCatchesStepExceptionTransitThrows> helloWorldRunner =
        flower.getFlowExec(InRetOrExceptionCatchesStepExceptionTransitThrows.class);

    FlowFuture<InRetOrExceptionCatchesStepExceptionTransitThrows> future =
        helloWorldRunner.runFlow(new InRetOrExceptionCatchesStepExceptionTransitThrows());

    ExecutionException e = assertThrows(ExecutionException.class, () -> future.getFuture().get());
    assertEquals(e.getCause(), EXCEPTION);
  }

  @Test
  void test6() {
    Flower flower = new Flower();
    flower.registerFlow(OutPrmException.class);
    flower.initialize();

    FlowExec<OutPrmException> helloWorldRunner = flower.getFlowExec(OutPrmException.class);

    FlowFuture<OutPrmException> future = helloWorldRunner.runFlow(new OutPrmException());

    ExecutionException e = assertThrows(ExecutionException.class, () -> future.getFuture().get());
    assertEquals(e.getCause(), EXCEPTION);
  }

  @Test
  void test7() {
    Flower flower = new Flower();
    flower.registerFlow(InOutPrmException.class);
    flower.initialize();

    FlowExec<InOutPrmException> helloWorldRunner = flower.getFlowExec(InOutPrmException.class);

    FlowFuture<InOutPrmException> future = helloWorldRunner.runFlow(new InOutPrmException());

    ExecutionException e = assertThrows(ExecutionException.class, () -> future.getFuture().get());
    assertEquals(e.getCause(), EXCEPTION);
  }
}

@FlowType(name = "SimpleStepException", firstStep = "step")
class SimpleStepException {
  @SimpleStepFunction
  public static Transition step() throws Exception {
    throw ExceptionsTest.EXCEPTION;
  }
}

@FlowType(name = "StepFunctionException", firstStep = "step")
class StepFunctionException {
  @StepFunction(transit = "transit")
  public static void step() throws Exception {
    throw ExceptionsTest.EXCEPTION;
  }

  @TransitFunction
  public static Transition transit(@Terminal Transition end) {
    return end;
  }
}

@FlowType(name = "TransitFunctionException", firstStep = "step")
class TransitFunctionException {
  @StepFunction(transit = "transit")
  public static void step() {}

  @TransitFunction
  public static Transition transit() throws Exception {
    throw ExceptionsTest.EXCEPTION;
  }
}

@FlowType(name = "InRetOrExceptionCatchesStepException", firstStep = "step")
class InRetOrExceptionCatchesStepException {
  @State
  Throwable stepException;

  @StepFunction(transit = "transit")
  public static void step() throws Exception {
    throw ExceptionsTest.EXCEPTION;
  }

  @TransitFunction
  public static Transition transit(
      @InRetOrException ReturnValueOrException<Void> ret,
      @Out OutPrm<Throwable> stepException,
      @Terminal Transition end) {
    stepException.setOutValue(ret.exception().get());
    return end;
  }
}

@FlowType(name = "InRetOrExceptionCatchesStepExceptionTransitThrows", firstStep = "step2")
class InRetOrExceptionCatchesStepExceptionTransitThrows {
  @StepFunction(transit = "transit2")
  public static ListenableFuture<String> step2() {
    return Futures.immediateFailedFuture(new Exception("step2 Exception String"));
  }

  @TransitFunction
  public static Transition transit2(
      @InRetOrException ReturnValueOrException<String> ret, @StepRef Transition step3)
      throws Exception {
    if (ret.exception().isPresent()) {
      Throwable exception = ret.exception().get();
      System.out.printf("Transit2 exception %s%n", exception.getMessage());
      return step3;
    } else {
      throw ExceptionsTest.EXCEPTION;
    }
  }

  @StepFunction(transit = "transit2")
  public static ListenableFuture<String> step3() {
    return Futures.immediateFuture("step3 String");
  }
}

@FlowType(name = "OutPrmException", firstStep = "step")
class OutPrmException {
  @State int i;

  @SimpleStepFunction
  public static Transition step(@Out OutPrm<Integer> i, @Terminal Transition END) {
    i.setOutFuture(Futures.immediateFailedFuture(ExceptionsTest.EXCEPTION));

    return END;
  }
}

@FlowType(name = "OutPrmException", firstStep = "step")
class InOutPrmException {
  @State int i;

  @SimpleStepFunction
  public static Transition step(@InOut NullableInOutPrm<Integer> i, @Terminal Transition END) {
    i.setOutFuture(Futures.immediateFailedFuture(ExceptionsTest.EXCEPTION));

    return END;
  }
}
