package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.flow.State;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.Out;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.lang.annotation.AnnotationFormatError;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DuplicateParameterAnnotationsTest {
  @Test
  public void testStepFunction() {
    Flower flower = new Flower();
    flower.registerFlow(D2_TestFlow1.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i1 is annotated as both"));
  }

  @Test
  public void testTransitionFunction() {
    Flower flower = new Flower();
    flower.registerFlow(D2_TestFlow2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i1 is annotated as both"));
  }

  @Test
  public void testGlobalFunction() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(D2_Container1.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i1 is annotated as both"));
  }

  @Test
  public void testStepCall() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(D2_Container2.class);
    flower.registerFlow(D2_TestFlow3.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i1 is annotated as both"));
  }

  @Test
  public void testTransitCall() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(D2_Container2.class);
    flower.registerFlow(D2_TestFlow4.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i1 is annotated as both"));
  }
}

@FlowType(name = "Test", firstStep = "step1")
class D2_TestFlow1 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1(@In @Out int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class D2_TestFlow2 {
  @State int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@In @Out int i1) {
    return null;
  }
}

@GlobalFunctionContainer(name = "Container1")
class D2_Container1 {
  @GlobalFunction
  static void step1(@In @Out int i1) {}
}

@GlobalFunctionContainer(name = "Container2")
class D2_Container2 {
  @GlobalFunction
  static Transition func1(@In int i1) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class D2_TestFlow3 {
  @State int i1;
  @State int i2;

  @StepCall(globalFunctionContainer = D2_Container2.class, globalFunctionName = "func1", transit = "transit")
  static void step1(@In @Out int i1) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class D2_TestFlow4 {
  @State
  int i1;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitCall(globalFunctionContainer = D2_Container2.class, globalFunctionName = "func1")
  static void transit(@In @Out int i1) {}
}
