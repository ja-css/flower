package com.flower.validations;

import static org.junit.Assert.assertTrue;

import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.functions.GlobalFunction;
import com.flower.engine.Flower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UniqueGlobalFunctionNameTest {
  /*
      + Global Function name globally unique;
  */
  @Test
  public void testUniqueFlowName() {
    Flower flower = new Flower();
    flower.registerGlobalFunctions(U4_TestFlow1.class);
    flower.registerGlobalFunctions(U4_TestFlow2.class);

    IllegalStateException e =
        Assertions.assertThrows(IllegalStateException.class, flower::initialize);

    assertTrue(e.getMessage().contains("Duplicate GlobalFunction name"));
  }
}

@GlobalFunctionContainer
class U4_TestFlow1 {
  @GlobalFunction(name = "func")
  static void func1() {}
}

@GlobalFunctionContainer
class U4_TestFlow2 {
  @GlobalFunction(name = "func")
  static void func2() {}
}
