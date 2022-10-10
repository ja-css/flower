package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.lang.annotation.AnnotationFormatError;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UniqueFlowNameTest {
  /*
      +Flow name globally unique;
      +Flow Type must be annotated with @FlowType;
      +FirstStep must be assigned, and there must be only one FirstStep;
      +Flow State Fields unique names.
  */
  @Test
  public void testUniqueFlowName() {
    Flower flower = new Flower();
    flower.registerFlow(U1_TestFlow1.class);
    IllegalStateException e =
        assertThrows(IllegalStateException.class, () -> flower.registerFlow(U1_TestFlow2.class));
    Assertions.assertTrue(e.getMessage().contains("Duplicate Flow name"));
  }

  @Test
  public void testFlowNameAnnotation() {
    Flower flower = new Flower();
    AnnotationFormatError e =
        assertThrows(AnnotationFormatError.class, () -> flower.registerFlow(U1_TestFlow3.class));
    Assertions.assertTrue(e.getMessage().contains("must be annotated with @FlowType"));
  }

  @Test
  public void testNoFirstStep() {
    Flower flower = new Flower();
    flower.registerFlow(U1_TestFlow4.class);
    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("FirstStep declaration not found"));
  }

  @Test
  public void testUniqueStateFields() {
    Flower flower = new Flower();
    flower.registerFlow(U1_TestFlow8.class);
    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("Duplicate state field name for a Flow"));
  }
}

@FlowType(name = "Test", firstStep = "step1")
class U1_TestFlow1 {}

@FlowType(name = "Test", firstStep = "step1")
class U1_TestFlow2 {}

class U1_TestFlow3 {}

@FlowType(name = "Test", firstStep = "step1")
class U1_TestFlow4 {}

@FlowType(name = "Test", firstStep = "step2")
class U1_TestFlow8 {
  @State(name = "field2")
  int field1;

  @State StringBuilder field2;

  @StepFunction(transit = "transit")
  static void step2() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}
