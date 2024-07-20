package com.flower.flows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.common.Output;
import com.flower.anno.params.events.InFromFlow;
import com.flower.anno.params.transit.InRet;
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
import java.lang.annotation.AnnotationFormatError;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowerNullabilityTest {
  @Test
  public void testNullReturnValueNotUpdated() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ReturnNullToFlowFieldIsNotUpdated.class);
    flower.initialize();

    ReturnNullToFlowFieldIsNotUpdated testFlow = new ReturnNullToFlowFieldIsNotUpdated();

    FlowExec<ReturnNullToFlowFieldIsNotUpdated> flowExec =
        flower.getFlowExec(ReturnNullToFlowFieldIsNotUpdated.class);

    FlowFuture<ReturnNullToFlowFieldIsNotUpdated> flowFuture = flowExec.runFlow(testFlow);
    ReturnNullToFlowFieldIsNotUpdated flow = flowFuture.getFuture().get();

    assertEquals(flow.i, 565);
  }

  @Test
  public void testNonNullReturnValueUpdated() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(ReturnNonNullToFlowFieldIsUpdated.class);
    flower.initialize();

    ReturnNonNullToFlowFieldIsUpdated testFlow = new ReturnNonNullToFlowFieldIsUpdated();

    FlowExec<ReturnNonNullToFlowFieldIsUpdated> flowExec =
        flower.getFlowExec(ReturnNonNullToFlowFieldIsUpdated.class);

    FlowFuture<ReturnNonNullToFlowFieldIsUpdated> flowFuture = flowExec.runFlow(testFlow);
    ReturnNonNullToFlowFieldIsUpdated flow = flowFuture.getFuture().get();

    assertEquals(flow.i, 789);
  }

  @Test
  public void testNullOutValueNotUpdated() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(OutNullToFlowFieldIsNotUpdated.class);
    flower.initialize();

    OutNullToFlowFieldIsNotUpdated testFlow = new OutNullToFlowFieldIsNotUpdated();

    FlowExec<OutNullToFlowFieldIsNotUpdated> flowExec =
        flower.getFlowExec(OutNullToFlowFieldIsNotUpdated.class);

    FlowFuture<OutNullToFlowFieldIsNotUpdated> flowFuture = flowExec.runFlow(testFlow);
    OutNullToFlowFieldIsNotUpdated flow = flowFuture.getFuture().get();

    assertEquals(flow.i, 565);
  }

  @Test
  public void testNonNullOutValueUpdated() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(OutNonNullToFlowFieldIsUpdated.class);
    flower.initialize();

    OutNonNullToFlowFieldIsUpdated testFlow = new OutNonNullToFlowFieldIsUpdated();

    FlowExec<OutNonNullToFlowFieldIsUpdated> flowExec =
        flower.getFlowExec(OutNonNullToFlowFieldIsUpdated.class);

    FlowFuture<OutNonNullToFlowFieldIsUpdated> flowFuture = flowExec.runFlow(testFlow);
    OutNonNullToFlowFieldIsUpdated flow = flowFuture.getFuture().get();

    assertEquals(flow.i, 789);
  }

  @Test
  public void testTransitCantBeNullable1() {
    Flower flower = new Flower();
    flower.registerFlow(TransitCantBeNullable1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value can't be @Nullable"));
  }

  @Test
  public void testTransitCantBeNullable2() {
    Flower flower = new Flower();
    flower.registerFlow(TransitCantBeNullable1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Transit Return value can't be @Nullable"));
  }

  @Test
  public void testInFromFlowNonNullableContainer() {
    Flower flower = new Flower();
    flower.registerEventProfile(InFromFlowNonNullableContainer.class);
    flower.registerFlow(TransitCantBeNullable1.class);
    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @InFromFlow must be @Nullable"));
  }

  @Test
  public void testOptionalOutAndInOutIsOptionalDoesntThrow()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(OptionalOutAndInOutIsOptionalDoesntThrow.class);
    flower.initialize();

    OptionalOutAndInOutIsOptionalDoesntThrow testFlow =
        new OptionalOutAndInOutIsOptionalDoesntThrow();

    FlowExec<OptionalOutAndInOutIsOptionalDoesntThrow> flowExec =
        flower.getFlowExec(OptionalOutAndInOutIsOptionalDoesntThrow.class);

    FlowFuture<OptionalOutAndInOutIsOptionalDoesntThrow> flowFuture = flowExec.runFlow(testFlow);
    OptionalOutAndInOutIsOptionalDoesntThrow flow = flowFuture.getFuture().get();

    assertEquals(flow.out, 12);
    assertEquals(flow.inOut, 34);
  }

  /*
  TODO: we decided this shouldn't cause runtime exception, We should implement NullAway-like static analysis to fail on init
  @Test
  public void testMandatoryOutThrows() {
    Flower flower = new Flower();
    flower.registerFlow(MandatoryOutThrows.class);
    flower.initialize();

    MandatoryOutThrows testFlow = new MandatoryOutThrows();

    FlowExec<MandatoryOutThrows> flowExec = flower.getFlowExec(MandatoryOutThrows.class);

    FlowFuture<MandatoryOutThrows> flowFuture = flowExec.runFlow(testFlow);
    Exception e = assertThrows(Exception.class, () -> flowFuture.getFuture().get());
    Assertions.assertTrue(
        e.getMessage()
            .contains("Fatal: value of Out or InOut parameter with Output.MANDATORY wasn't set in the Function call"));
  }
*/

  /*
  TODO: we decided this shouldn't cause runtime exception, We should implement NullAway-like static analysis to fail on init
  @Test
  public void testMandatoryInOutThrows() {
    Flower flower = new Flower();
    flower.registerFlow(MandatoryInOutThrows.class);
    flower.initialize();

    MandatoryInOutThrows testFlow = new MandatoryInOutThrows();

    FlowExec<MandatoryInOutThrows> flowExec = flower.getFlowExec(MandatoryInOutThrows.class);
    FlowFuture<MandatoryInOutThrows> flowFuture = flowExec.runFlow(testFlow);
    Exception e = assertThrows(Exception.class, () -> flowFuture.getFuture().get());
    Assertions.assertTrue(
        e.getMessage()
            .contains("Fatal: value of Out or InOut parameter with Output.MANDATORY wasn't set in the Function call"));
  }
*/
  @Test
  public void testNullableInInRetAllowed() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(NullableInInRetAllowed.class);
    flower.initialize();

    NullableInInRetAllowed testFlow = new NullableInInRetAllowed();

    FlowExec<NullableInInRetAllowed> flowExec = flower.getFlowExec(NullableInInRetAllowed.class);
    FlowFuture<NullableInInRetAllowed> flowFuture = flowExec.runFlow(testFlow);
    flowFuture.getFuture().get();
  }

  @Test
  public void testNullableInInRetOrExceptionAllowed()
      throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(NullableInInRetOrExceptionAllowed.class);
    flower.initialize();

    NullableInInRetOrExceptionAllowed testFlow = new NullableInInRetOrExceptionAllowed();

    FlowExec<NullableInInRetOrExceptionAllowed> flowExec =
        flower.getFlowExec(NullableInInRetOrExceptionAllowed.class);
    FlowFuture<NullableInInRetOrExceptionAllowed> flowFuture = flowExec.runFlow(testFlow);
    flowFuture.getFuture().get();
  }

  @Test
  public void testInRetAndInRetOrExceptionFails() {
    Flower flower = new Flower();
    flower.registerFlow(InRetAndInRetOrExceptionFails.class);

    Exception e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("Function can't have both IN_RET and IN_RET_OR_EXCEPTION parameters"));
  }

  @Test
  public void testInOutIsInitializing() {
    Flower flower = new Flower();
    flower.registerFlow(InOutIsInitializing.class);

    flower.initialize();
  }
}

@FlowType(firstStep = "STEP")
class ReturnNullToFlowFieldIsNotUpdated {
  @State Integer i = 565;

  @StepFunction(transit = "TRANSIT", returnTo = "i")
  @Nullable
  static Integer STEP() {
    return null;
  }

  @TransitFunction
  static Transition TRANSIT(@Terminal Transition END) {
    System.out.println("tulula");
    return END;
  }
}

@FlowType(firstStep = "STEP")
class ReturnNonNullToFlowFieldIsUpdated {
  @State Integer i = 565;

  @StepFunction(transit = "TRANSIT", returnTo = "i")
  static @Nullable Integer STEP() {
    return 789;
  }

  @TransitFunction
  static Transition TRANSIT(@Terminal Transition END) {
    System.out.println("tulula");
    return END;
  }
}

@FlowType(firstStep = "STEP")
class OutNullToFlowFieldIsNotUpdated {
  @State Integer i = 565;

  @StepFunction(transit = "TRANSIT")
  @Nullable
  static void STEP(@Out(out = Output.OPTIONAL) OutPrm<Integer> i) {
    return;
  }

  @TransitFunction
  static Transition TRANSIT(@Terminal Transition END) {
    System.out.println("tulula");
    return END;
  }
}

@FlowType(firstStep = "STEP")
class OutNonNullToFlowFieldIsUpdated {
  @State Integer i = 565;

  @StepFunction(transit = "TRANSIT")
  @Nullable
  static void STEP(@Out(out = Output.OPTIONAL) OutPrm<Integer> i) {
    i.setOutValue(789);
    return;
  }

  @TransitFunction
  static Transition TRANSIT(@Terminal Transition END) {
    System.out.println("tulula");
    return END;
  }
}

@FlowType(firstStep = "STEP")
class TransitCantBeNullable1 {
  @StepFunction(transit = "TRANSIT")
  @Nullable
  static void STEP() {}

  @TransitFunction
  @Nullable
  static Transition TRANSIT(@Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class TransitCantBeNullable2 {
  @SimpleStepFunction
  @Nullable
  static Transition STEP(@Terminal Transition END) {
    return END;
  }
}

@EventProfileContainer
class InFromFlowNonNullableContainer {
  @EventFunction(types = EventType.AFTER_EXEC)
  @Nullable
  static void EVENT(@InFromFlow int i) {}
}

@FlowType(firstStep = "STEP")
class OptionalOutAndInOutIsOptionalDoesntThrow {
  @State
  Integer out = 12;
  @State Integer inOut = 34;

  @SimpleStepFunction
  static Transition STEP(
      @Out(out = Output.OPTIONAL) OutPrm<Integer> out,
      @InOut(out = Output.OPTIONAL) NullableInOutPrm<Integer> inOut,
      @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class MandatoryOutThrows {
  @State Integer out = 12;

  @SimpleStepFunction
  static Transition STEP(@Out OutPrm<Integer> out, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class MandatoryInOutThrows {
  @State Integer inOut = 34;

  @SimpleStepFunction
  static Transition STEP(@InOut NullableInOutPrm<Integer> inOut, @Terminal Transition END) {
    return END;
  }
}

@FlowType(firstStep = "STEP")
class NullableInInRetAllowed {
  @State Integer in;

  @StepFunction(transit = "TRANSIT")
  static @Nullable Integer STEP(@Nullable @In Integer in) {
    System.out.printf("STEP %s \n", in);
    return null;
  }

  @TransitFunction
  static Transition TRANSIT(@Nullable @InRet Integer inRet, @Terminal Transition END) {
    System.out.printf("TRANSIT %s \n", inRet);
    return END;
  }
}

@FlowType(firstStep = "STEP")
class NullableInInRetOrExceptionAllowed {
  @State Integer in;

  @StepFunction(transit = "TRANSIT")
  static @Nullable Integer STEP(@Nullable @In Integer in) {
    System.out.printf("STEP %s \n", in);
    return null;
  }

  @TransitFunction
  static Transition TRANSIT(
      @InRetOrException ReturnValueOrException<Integer> inRet, @Terminal Transition END) {
    System.out.printf("TRANSIT returnValue %s \n", inRet.returnValue());
    System.out.printf("TRANSIT exception %s \n", inRet.exception());
    return END;
  }
}

@FlowType(firstStep = "STEP")
class InRetAndInRetOrExceptionFails {
  @State Integer in;

  @StepFunction(transit = "TRANSIT")
  static @Nullable Integer STEP(@Nullable @In Integer in) {
    System.out.printf("STEP %s \n", in);
    return null;
  }

  @TransitFunction
  static Transition TRANSIT(
      @Nullable @InRet Integer inRet0,
      @InRetOrException ReturnValueOrException<Integer> inRet,
      @Terminal Transition END) {
    System.out.printf("TRANSIT %s \n", inRet0);
    System.out.printf("TRANSIT returnValue %s \n", inRet.returnValue());
    System.out.printf("TRANSIT exception %s \n", inRet.exception());
    return END;
  }
}

@FlowType(firstStep = "STEP")
class InOutIsInitializing {
  @State @Nullable Integer in;

  @SimpleStepFunction
  static Transition STEP(@InOut NullableInOutPrm<Integer> in,
                         @StepRef Transition STEP2) {
    System.out.printf("STEP %s \n", in.getInValue());
    in.setOutValue(123);
    return STEP2;
  }

  @SimpleStepFunction
  static Transition STEP2(@In Integer in,
                          @Terminal Transition end) {
    System.out.printf("STEP %s \n", in);
    return end;
  }
}
