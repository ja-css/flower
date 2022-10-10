package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.lang.annotation.AnnotationFormatError;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DuplicateMethodAnnotationsTest {
  @Test
  public void testDuplicateAnnotation() {
    Flower flower = new Flower();
    flower.registerFlow(D1_TestFlow1.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("is annotated as both"));
  }

  @Test
  public void testStaticMethod() {
    Flower flower = new Flower();
    flower.registerFlow(D1_TestFlow2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("must be static"));
  }
}

@FlowType(name = "Test", firstStep = "step1")
class D1_TestFlow1 {
  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  @SimpleStepFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class D1_TestFlow2 {
  @StepFunction(transit = "transit")
  void step1() {}

  @TransitFunction()
  Transition transit() {
    return null;
  }
}
