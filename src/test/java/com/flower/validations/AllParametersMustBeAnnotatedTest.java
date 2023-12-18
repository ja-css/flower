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
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.lang.annotation.AnnotationFormatError;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AllParametersMustBeAnnotatedTest {
  @Test
  public void testStepFunction() {
    Flower flower = new Flower();
    flower.registerFlow(A1_TestFlow1.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i3 should be annotated"));
  }

  @Test
  public void testTransitionFunction() {
    Flower flower = new Flower();
    flower.registerFlow(A1_TestFlow2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i3 should be annotated"));
  }

  @Test
  public void testGlobalFunction() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(A1_Container1.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i3 should be annotated"));
  }

  @Test
  public void testStepCall() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(A1_Container2.class);
    flower.registerFlow(A1_TestFlow3.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i3 should be annotated"));
  }

  @Test
  public void testTransitCall() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(A1_Container2.class);
    flower.registerFlow(A1_TestFlow4.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Parameter i3 should be annotated"));
  }

  @Test
  public void testReturnValue() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(A1_Container3.class);
    flower.registerFlow(A1_TestFlow5.class);

    flower.initialize();
  }
}

@FlowType(name = "Test", firstStep = "step1")
class A1_TestFlow1 {
  @State int i1;
  @State int i2;

  @StepFunction(transit = "transit")
  static void step1(
      @In(name = "i1", from = "i1") int i1, @In(name = "i2", from = "i2") int i2, int i3) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class A1_TestFlow2 {
  @State int i1;
  @State int i2;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(
      @In(name = "i1", from = "i1") int i1, @In(name = "i2", from = "i2") int i2, int i3) {
    return null;
  }
}

@GlobalFunctionContainer(name = "Container1")
class A1_Container1 {
  @GlobalFunction
  static void step1(
      @In(name = "i1", from = "i1") int i1, @In(name = "i2", from = "i2") int i2, int i3) {}
}

@GlobalFunctionContainer(name = "Container2")
class A1_Container2 {
  @GlobalFunction
  static Transition func1(
      @In(name = "i1", from = "i1") int i1, @In(name = "i2", from = "i2") int i2) {
    throw new UnsupportedOperationException();
  }
}

@FlowType(name = "Test", firstStep = "step1")
class A1_TestFlow3 {
  @State
  int i1;
  @State int i2;

  @StepCall(globalFunctionContainer = A1_Container2.class, globalFunctionName = "func1", transit = "transit")
  static void step1(
      @In(name = "i1", from = "i1") int i1, @In(name = "i2", from = "i2") int i2, int i3) {}

  @TransitFunction()
  static Transition transit() {
    throw new UnsupportedOperationException();
  }
}

@FlowType(name = "Test", firstStep = "step1")
class A1_TestFlow4 {
  @State int i1;
  @State int i2;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitCall(globalFunctionContainer = A1_Container2.class, globalFunctionName = "func1")
  static void transit(
      @In(name = "i1", from = "i1") int i1, @In(name = "i2", from = "i2") int i2, int i3) {}
}

@GlobalFunctionContainer(name = "Container2")
class A1_Container3 {
  @GlobalFunction
  static Transition func1(@Nullable @In int i1, @Nullable @In int i2, @Nullable @In int i3) {
    throw new UnsupportedOperationException();
  }
}

@FlowType(name = "Test", firstStep = "step1")
class A1_TestFlow5 {
  @State int i1;
  @State int i2;
  @State int i3;

  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitCall(globalFunctionContainer = A1_Container3.class, globalFunctionName = "func1")
  static Transition transit(@Nullable @In int i1, @Nullable @In int i2, @Nullable @In int i3) {
    return null;
  }
}
