package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.events.EventInfo;
import com.flower.anno.params.events.FlowInfo;
import com.flower.anno.params.events.StepInfo;
import com.flower.anno.params.events.TransitionInfo;
import com.flower.anno.params.step.FlowFactory;
import com.flower.anno.params.transit.InRet;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowInfoPrm;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.StepInfoPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.lang.annotation.AnnotationFormatError;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WrongTypesForAnnotationsTest {
  @Test
  public void testOutAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_Out_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @Out should be of type com.css.flower.conf.OutPrm"));
  }

  @Test
  public void testFlowInfoAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowInfo_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @FlowInfo should be of type"));
  }

  @Test
  public void testFlowInfoAnnotation_step2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowInfo_Step_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @FlowInfo can only be used in EventFunction"));
  }

  @Test
  public void testStepInfoAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepInfo_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @StepInfo should be of type"));
  }

  @Test
  public void testStepInfoAnnotation_step2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepInfo_Step_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @StepInfo can only be used in EventFunction"));
  }

  @Test
  public void testTransitionInfoAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_TransitionInfo_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @TransitionInfo should be of type"));
  }

  @Test
  public void testTransitionInfoAnnotation_step2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_TransitionInfo_Step_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @TransitionInfo can only be used in EventFunction"));
  }

  @Test
  public void testEventInfoAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_EventInfo_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @EventInfo should be of type"));
  }

  @Test
  public void testEventInfoAnnotation_step2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_EventInfo_Step_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @EventInfo can only be used in EventFunction"));
  }

  @Test
  public void testInOutAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InOut_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @InOut should be of type com.css.flower.conf.InOutPrm"));
  }

  @Test
  public void testFlowTypeFactoryAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowTypeFactory_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @FlowTypeFactory should be of type com.css.flower.conf.FlowFactoryPrm"));
  }

  @Test
  public void testStepRefAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepRef_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @StepRef can only be used in TransitFunction"));
  }

  @Test
  public void testTerminalAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_Terminal_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @Terminal can only be used in TransitFunction"));
  }

  @Test
  public void testInRetAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InRet_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @InRet can only be used in TransitFunction"));
  }

  @Test
  public void testOutAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_Out_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @Out should be of type com.css.flower.conf.OutPrm"));
  }

  @Test
  public void testFlowInfoAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowInfo_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @FlowInfo should be of"));
  }

  @Test
  public void testFlowInfoAnnotation_transitioner2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowInfo_Transitioner_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @FlowInfo can only be used in EventFunction"));
  }

  @Test
  public void testStepInfoAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepInfo_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @StepInfo should be of"));
  }

  @Test
  public void testStepInfoAnnotation_transitioner2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepInfo_Transitioner_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @StepInfo can only be used in EventFunction"));
  }

  @Test
  public void testTransitionInfoAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_TransitionInfo_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @TransitionInfo should be of"));
  }

  @Test
  public void testTransitionInfoAnnotation_transitioner2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_TransitionInfo_Transitioner_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @TransitionInfo can only be used in EventFunction"));
  }

  @Test
  public void testEventInfoAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_EventInfo_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @EventInfo should be of"));
  }

  @Test
  public void testEventInfoAnnotation_transitioner2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_EventInfo_Transitioner_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @EventInfo can only be used in EventFunction"));
  }

  @Test
  public void testInOutAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InOut_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @InOut should be of type com.css.flower.conf.InOutPrm"));
  }

  @Test
  public void testFlowTypeFactoryAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowTypeFactory_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @FlowTypeFactory can only be used in StepFunction"));
  }

  @Test
  public void testStepRefAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepRef_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @StepRef should be of type com.css.flower.conf.Transition"));
  }

  @Test
  public void testTerminalAnnotation_transitioner() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_Terminal_Transitioner.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @Terminal should be of type com.css.flower.conf.Transition"));
  }

  @Test
  public void testOutAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_Out_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @Out should be of type com.css.flower.conf.OutPrm"));
  }

  @Test
  public void testFlowInfoAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_FlowInfo_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @FlowInfo should be of type"));
  }

  @Test
  public void testStepInfoAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_StepInfo_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @StepInfo should be of type"));
  }

  @Test
  public void testTransitionInfoAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_TransitionInfo_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @TransitionInfo should be of type"));
  }

  @Test
  public void testEventInfoAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_EventInfo_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @EventInfo should be of type"));
  }

  @Test
  public void testInOutAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_InOut_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @InOut should be of type com.css.flower.conf.InOutPrm"));
  }

  @Test
  public void testFlowTypeFactoryAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_FlowTypeFactory_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @FlowTypeFactory should be of type com.css.flower.conf.FlowFactoryPrm"));
  }

  @Test
  public void testStepRefAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_StepRef_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @StepRef should be of type com.css.flower.conf.Transition"));
  }

  @Test
  public void testTerminalAnnotation_global() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(TestFlow_Terminal_Global.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @Terminal should be of type com.css.flower.conf.Transition"));
  }

  @Test
  public void testOutAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_Out_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @Out should be of type com.css.flower.conf.OutPrm"));
  }

  @Test
  public void testFlowInfoAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowInfo_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @FlowInfo should be of type"));
  }

  @Test
  public void testFlowInfoAnnotation_stepAndTransit2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowInfo_StepAndTransit_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @FlowInfo can only be used in EventFunction"));
  }

  @Test
  public void testStepInfoAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepInfo_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @StepInfo should be of type"));
  }

  @Test
  public void testStepInfoAnnotation_stepAndTransit2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepInfo_StepAndTransit_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @StepInfo can only be used in EventFunction"));
  }

  @Test
  public void testTransitionInfoAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_TransitionInfo_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @TransitionInfo should be of type"));
  }

  @Test
  public void testTransitionInfoAnnotation_stepAndTransit2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_TransitionInfo_StepAndTransit_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @TransitionInfo can only be used in EventFunction"));
  }

  @Test
  public void testEventInfoAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_EventInfo_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("annotated as @EventInfo should be of type"));
  }

  @Test
  public void testEventInfoAnnotation_stepAndTransit2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_EventInfo_StepAndTransit_2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @EventInfo can only be used in EventFunction"));
  }

  @Test
  public void testInOutAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InOut_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @InOut should be of type com.css.flower.conf.InOutPrm"));
  }

  @Test
  public void testFlowTypeFactoryAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_FlowTypeFactory_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @FlowTypeFactory should be of type com.css.flower.conf.FlowFactoryPrm"));
  }

  @Test
  public void testStepRefAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_StepRef_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @StepRef should be of type com.css.flower.conf.Transition"));
  }

  @Test
  public void testTerminalAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_Terminal_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @Terminal should be of type com.css.flower.conf.Transition"));
  }

  @Test
  public void testInRetAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InRet_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @InRet can only be used in TransitFunction"));
  }

  @Test
  public void testInRetOrExceptionAnnotation_step() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InRetOrExceptionAnnotation_Step.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @InRetOrException can only be used in TransitFunction"));
  }

  @Test
  public void testInRetOrExceptionAnnotation_step2() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InRetOrExceptionAnnotation_Step2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage().contains("annotated as @InRetOrException should be of type"));
  }

  @Test
  public void testInRetOrExceptionAnnotation_step3() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InRetOrExceptionAnnotation_Step3.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("@InRetOrException parameter type mismatch"));
  }

  /*
  TODO: this is allowed for now
    pros: InRet can be overridden with In
    cons: I'm not sure how this will work if an exception occurs, or how it _should_ work. Added to TODO list.

  @Test
    public void testInRetOrExceptionAnnotation_step4() {
      Flower flower = new Flower();
      flower.registerFlow(TestFlow_InRetOrExceptionAnnotation_Step4.class);

      IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
      Assertions.assertTrue(
          e.getMessage()
              .contains("Function can't have both IN_RET and IN_RET_OR_EXCEPTION parameters"));
    }*/

  @Test
  public void testInRetOrExceptionAnnotation_stepAndTransit() {
    Flower flower = new Flower();
    flower.registerFlow(TestFlow_InRetOrException_StepAndTransit.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains("annotated as @InRetOrException can only be used in TransitFunction"));
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_Out_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@Out int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_InOut_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@InOut int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_FlowTypeFactory_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@FlowFactory int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_StepRef_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@StepRef int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_Terminal_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@Terminal int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_InRet_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@InRet int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_InRetOrException_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@InRetOrException ReturnValueOrException<Integer> ret) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_InRetOrExceptionAnnotation_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@InRetOrException int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_InRetOrExceptionAnnotation_Step2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static int step1(@In int i1) {
    return 123;
  }

  @TransitFunction()
  static Transition transit(@InRetOrException int ret) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_InRetOrExceptionAnnotation_Step3 {
  @State int i1;

  @StepFunction(transit = "transit")
  static int step1(@In int i1) {
    return 123;
  }

  @TransitFunction()
  static Transition transit(@InRetOrException ReturnValueOrException<String> ret) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_InRetOrExceptionAnnotation_Step4 {
  @State int i1;

  @StepFunction(transit = "transit")
  static int step1(@In int i1) {
    return 123;
  }

  @TransitFunction()
  static Transition transit(
      @InRetOrException ReturnValueOrException<Integer> ret, @InRet Integer ret2) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_Out_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@Out int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_InOut_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@InOut int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_FlowTypeFactory_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@FlowFactory int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_StepRef_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@StepRef int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_Terminal_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@Terminal int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_Out_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@Out int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_InOut_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@InOut int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_FlowTypeFactory_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@FlowFactory int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_StepRef_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@StepRef int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_Terminal_StepAndTransit {
  @State
  int i1;

  @SimpleStepFunction
  static Transition transit(@Terminal int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_InRet_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static void step1(@InRet int i1) {}
}

@GlobalFunctionContainer
class TestFlow_Out_Global {
  @GlobalFunction
  static void step1(@Out int i1) {}
}

@GlobalFunctionContainer
class TestFlow_InOut_Global {
  @GlobalFunction
  static void step1(@InOut int i1) {}
}

@GlobalFunctionContainer
class TestFlow_FlowTypeFactory_Global {
  @GlobalFunction
  static void step1(@FlowFactory int i1) {}
}

@GlobalFunctionContainer
class TestFlow_StepRef_Global {
  @GlobalFunction
  static void step1(@StepRef int i1) {}
}

@GlobalFunctionContainer
class TestFlow_Terminal_Global {
  @GlobalFunction
  static void step1(@Terminal int i1) {}
}

@GlobalFunctionContainer
class TestFlow_FlowInfo_Global {
  @GlobalFunction
  static void step1(@FlowInfo int i1) {}
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_FlowInfo_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@FlowInfo int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_FlowInfo_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@FlowInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_FlowInfo_Transitioner_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@FlowInfo FlowInfoPrm i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_FlowInfo_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@FlowInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_FlowInfo_Step_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@FlowInfo FlowInfoPrm i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_FlowInfo_StepAndTransit_2 {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@FlowInfo FlowInfoPrm i1) {
    return null;
  }
}

@GlobalFunctionContainer
class TestFlow_StepInfo_Global {
  @GlobalFunction
  static void step1(@StepInfo int i1) {}
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_StepInfo_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@StepInfo int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_StepInfo_Step_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@StepInfo StepInfoPrm i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_StepInfo_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@StepInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_StepInfo_Transitioner_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@StepInfo StepInfoPrm i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_StepInfo_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@StepInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_StepInfo_StepAndTransit_2 {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@StepInfo StepInfoPrm i1) {
    return null;
  }
}

@GlobalFunctionContainer
class TestFlow_TransitionInfo_Global {
  @GlobalFunction
  static void step1(@TransitionInfo int i1) {}
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_TransitionInfo_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@TransitionInfo int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_TransitionInfo_Step_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@TransitionInfo Transition i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_TransitionInfo_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@TransitionInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_TransitionInfo_Transitioner_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@TransitionInfo Transition i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_TransitionInfo_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@TransitionInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_TransitionInfo_StepAndTransit_2 {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@TransitionInfo Transition i1) {
    return null;
  }
}

@GlobalFunctionContainer
class TestFlow_EventInfo_Global {
  @GlobalFunction
  static void step1(@EventInfo int i1) {}
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_EventInfo_Step {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@EventInfo int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_EventInfo_Step_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@EventInfo EventType i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_EventInfo_Transitioner {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@EventInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class TestFlow_EventInfo_Transitioner_2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@EventInfo EventType i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_EventInfo_StepAndTransit {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@EventInfo int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "transit")
class TestFlow_EventInfo_StepAndTransit_2 {
  @State int i1;

  @SimpleStepFunction
  static Transition transit(@EventInfo EventType i1) {
    return null;
  }
}
