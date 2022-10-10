package com.flower.nullaway;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.common.Output;
import com.flower.anno.params.transit.InRet;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.InOutPrm;
import com.flower.conf.NullableInOutPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowNullAwayTest {
  @Test
  void test_UninitializedField() {
    Flower flower = new Flower();
    flower.registerFlow(FNA_UninitializedField.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("expects to find the state field [i] initialized"));
  }

  @Test
  void test_UninitializedFieldInOut() {
    Flower flower = new Flower();
    flower.registerFlow(FNA_UninitializedFieldInOut.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("expects to find the state field [i] initialized"));
  }

  @Test
  void test_FinalFieldInitializing() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_FinalFieldInitializing.class);
    flower.initialize();

    FlowExec<FNA_FinalFieldInitializing> helloWorldExec =
        flower.getFlowExec(FNA_FinalFieldInitializing.class);
    FlowFuture<FNA_FinalFieldInitializing> flowFuture =
        helloWorldExec.runFlow(new FNA_FinalFieldInitializing());

    flowFuture.getFuture().get();
  }

  @Test
  void test_OutParameterInitializing() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_OutParameterInitializing.class);
    flower.initialize();

    FlowExec<FNA_OutParameterInitializing> helloWorldExec =
        flower.getFlowExec(FNA_OutParameterInitializing.class);
    FlowFuture<FNA_OutParameterInitializing> flowFuture =
        helloWorldExec.runFlow(new FNA_OutParameterInitializing());

    flowFuture.getFuture().get();
  }

  @Test
  void test_InOutParameterInitializing() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_InOutParameterInitializing.class);
    flower.initialize();

    FlowExec<FNA_InOutParameterInitializing> helloWorldExec =
        flower.getFlowExec(FNA_InOutParameterInitializing.class);
    FlowFuture<FNA_InOutParameterInitializing> flowFuture =
        helloWorldExec.runFlow(new FNA_InOutParameterInitializing());

    flowFuture.getFuture().get();
  }

  @Test
  void test_FNA_OptionalOut_OutParameterNotInitializing() {
    Flower flower = new Flower();
    flower.registerFlow(FNA_OptionalOut_OutParameterNotInitializing.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("expects to find the state field [i] initialized"));
  }

  @Test
  void test_FNA_OptionalOut_InOutParameterNotInitializing() {
    Flower flower = new Flower();
    flower.registerFlow(FNA_OptionalOut_InOutParameterNotInitializing.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("expects to find the state field [i] initialized"));
  }

  @Test
  void test_FNA_ReturnToInitializing() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_ReturnToInitializing.class);
    flower.initialize();

    FlowExec<FNA_ReturnToInitializing> helloWorldExec =
        flower.getFlowExec(FNA_ReturnToInitializing.class);
    FlowFuture<FNA_ReturnToInitializing> flowFuture =
        helloWorldExec.runFlow(new FNA_ReturnToInitializing());

    flowFuture.getFuture().get();
  }

  @Test
  void test_FNA_NullableReturnToNotInitializing() {
    Flower flower = new Flower();
    flower.registerFlow(FNA_NullableReturnToNotInitializing.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("expects to find the state field [i] initialized"));
  }

  @Test
  void test_FNA_InRetInitializing() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_InRetInitializing.class);
    flower.initialize();

    FlowExec<FNA_InRetInitializing> helloWorldExec =
        flower.getFlowExec(FNA_InRetInitializing.class);
    FlowFuture<FNA_InRetInitializing> flowFuture =
        helloWorldExec.runFlow(new FNA_InRetInitializing());

    flowFuture.getFuture().get();
  }

  @Test
  void test_FNA_NullableInRetNotInitializing() {
    Flower flower = new Flower();
    flower.registerFlow(FNA_NullableReturnValueNotInitializingInRet.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("@InRet parameter Nullable mismatch"));
  }

  @Test
  void test_FNA_NullableReturnValueWorksWithNullableInRet()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_NullableReturnValueWorksWithNullableInRet.class);
    flower.initialize();

    FlowExec<FNA_NullableReturnValueWorksWithNullableInRet> helloWorldExec =
        flower.getFlowExec(FNA_NullableReturnValueWorksWithNullableInRet.class);
    FlowFuture<FNA_NullableReturnValueWorksWithNullableInRet> flowFuture =
        helloWorldExec.runFlow(new FNA_NullableReturnValueWorksWithNullableInRet());

    flowFuture.getFuture().get();
  }

  @Test
  void test_FNA_InRetOrExceptionWorksWithNullableReturnValue()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_InRetOrExceptionWorksWithNullableReturnValue.class);
    flower.initialize();

    FlowExec<FNA_InRetOrExceptionWorksWithNullableReturnValue> helloWorldExec =
        flower.getFlowExec(FNA_InRetOrExceptionWorksWithNullableReturnValue.class);
    FlowFuture<FNA_InRetOrExceptionWorksWithNullableReturnValue> flowFuture =
        helloWorldExec.runFlow(new FNA_InRetOrExceptionWorksWithNullableReturnValue());

    flowFuture.getFuture().get();
  }

  @Test
  void test_FNA_InRetOrExceptionStepFunctionIsNotInitializing() {
    Flower flower = new Flower();
    flower.registerFlow(FNA_InRetOrExceptionStepFunctionIsNotInitializing.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("expects to find the state field [i2] initialized"));
  }

  @Test
  void test_FNA_FNA_InRetStepFunctionIsInitializing()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(FNA_InRetStepFunctionIsInitializing.class);
    flower.initialize();

    FlowExec<FNA_InRetStepFunctionIsInitializing> helloWorldExec =
        flower.getFlowExec(FNA_InRetStepFunctionIsInitializing.class);
    FlowFuture<FNA_InRetStepFunctionIsInitializing> flowFuture =
        helloWorldExec.runFlow(new FNA_InRetStepFunctionIsInitializing());

    flowFuture.getFuture().get();
  }
}

@FlowType(firstStep = "STEP")
class FNA_UninitializedField {
  @State int i;

  @SimpleStepFunction
  static Transition STEP(@In int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_UninitializedFieldInOut {
  @State
  int i;

  @SimpleStepFunction
  static Transition STEP(@InOut InOutPrm<Integer> i, @Terminal Transition END) {
    i.setOutValue(5);
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_FinalFieldInitializing {
  @State final int i = 5;

  @SimpleStepFunction
  static Transition STEP(@In int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_OutParameterInitializing {
  @State int i;

  @SimpleStepFunction
  static Transition STEP(@Out OutPrm<Integer> i, @StepRef Transition STEP2) {
    i.setOutValue(5);
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_InOutParameterInitializing {
  @State int i;

  @SimpleStepFunction
  static Transition STEP(@InOut NullableInOutPrm<Integer> i, @StepRef Transition STEP2) {
    i.setOutValue(5);
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_OptionalOut_OutParameterNotInitializing {
  @State int i;

  @SimpleStepFunction
  static Transition STEP(@Out(out = Output.OPTIONAL) OutPrm<Integer> i, @StepRef Transition STEP2) {
    i.setOutValue(5);
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_OptionalOut_InOutParameterNotInitializing {
  @State int i;

  @SimpleStepFunction
  static Transition STEP(
          @InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> i, @StepRef Transition STEP2) {
    i.setOutValue(5);
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_ReturnToInitializing {
  @State int i;

  @StepFunction(transit = "TRANSIT", returnTo = "i")
  static int STEP(@Out(out = Output.OPTIONAL) OutPrm<Integer> i) {
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(@StepRef Transition STEP2) {
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In int i, @Terminal Transition END) {
    System.out.println(i);
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_NullableReturnToNotInitializing {
  @State int i;

  @StepFunction(transit = "TRANSIT", returnTo = "i")
  @Nullable
  static Integer STEP(@Out(out = Output.OPTIONAL) OutPrm<Integer> i) {
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(@StepRef Transition STEP2) {
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_InRetInitializing {
  @StepFunction(transit = "TRANSIT")
  static int STEP() {
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(@InRet int i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_NullableReturnValueNotInitializingInRet {
  @StepFunction(transit = "TRANSIT")
  @Nullable
  static Integer STEP() {
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(@InRet Integer i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_NullableReturnValueWorksWithNullableInRet {
  @StepFunction(transit = "TRANSIT")
  @Nullable
  static Integer STEP() {
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(@Nullable @InRet Integer i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_InRetOrExceptionWorksWithNullableReturnValue {
  @StepFunction(transit = "TRANSIT")
  @Nullable
  static Integer STEP() {
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(
      @InRetOrException ReturnValueOrException<Integer> i, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_InRetOrExceptionStepFunctionIsNotInitializing {
  @State int i2;

  @StepFunction(transit = "TRANSIT")
  @Nullable
  static Integer STEP(@Out OutPrm<Integer> i2) {
    i2.setOutValue(5);
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(
          @InRetOrException ReturnValueOrException<Integer> i, @In int i2, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class FNA_InRetStepFunctionIsInitializing {
  @State int i2;

  @StepFunction(transit = "TRANSIT")
  static Integer STEP(@Out OutPrm<Integer> i2) {
    i2.setOutValue(5);
    return 5;
  }

  @TransitFunction
  static Transition TRANSIT(@InRet Integer i, @In int i2, @Terminal Transition END) {
    return END;
  }
}
