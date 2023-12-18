package com.flower.validations;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.event.EventCall;
import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.functions.GlobalFunction;
import com.flower.anno.functions.StepCall;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitCall;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.events.InFromFlow;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CorrespondingStateParameterDoesntExistTest {
  @Test
  public void testStepParameter() {
    Flower flower = new Flower();
    flower.registerFlow(C1_TestFlow_TransitParameter.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testStepCallParameter() {
    Flower flower = new Flower();
    flower.registerFlow(C1_TestFlow_StepCallParameter.class);
    flower.registerGlobalFunctions(C1_GlobalFunctionContainer1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testStepGlobalTransitParameter() {
    Flower flower = new Flower();
    flower.registerFlow(C1_TestFlow_Step_GlobalTransitParameter.class);
    flower.registerGlobalFunctions(C1_GlobalFunctionContainer1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testStepCallGlobalTransitParameter() {
    Flower flower = new Flower();
    flower.registerFlow(C1_TestFlow_StepCall_GlobalTransitParameter.class);
    flower.registerGlobalFunctions(C1_GlobalFunctionContainer1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testTransitParameter() {
    Flower flower = new Flower();
    flower.registerFlow(C1_TestFlow_TransitParameter.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testTransitCallParameter() {
    Flower flower = new Flower();
    flower.registerFlow(C1_TestFlow_TransitCallParameter.class);
    flower.registerGlobalFunctions(C1_GlobalFunctionContainer1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testEventProfileParameter() {
    Flower flower = new Flower();
    flower.registerEventProfile(C1_TestEventProfile_EventProfileParameter.class);
    flower.registerFlow(C1_TestEventProfile_EventProfileParameter_Flow.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testEventProfileCallParameter() {
    Flower flower = new Flower();
    flower.registerEventProfile(C1_TestEventProfile_EventCallProfileParameter.class);
    flower.registerFlow(C1_TestEventProfile_EventCallProfileParameter_Flow.class);
    flower.registerGlobalFunctions(C1_GlobalFunctionContainer1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }

  @Test
  public void testEventProfileCallParameterFromFlow() {
    Flower flower = new Flower();
    flower.registerEventProfile(C1_TestEventProfile_EventCallProfileParameterFromFlow.class);
    flower.registerFlow(C1_TestEventProfile_EventCallProfileParameterFromFlow_Flow.class);
    flower.registerGlobalFunctions(C1_GlobalFunctionContainer1.class);

    IllegalStateException e = assertThrows(IllegalStateException.class, flower::initialize);
    Assertions.assertTrue(e.getMessage().contains("State field doesn't exist"));
  }
}

@GlobalFunctionContainer(name = "Container1")
class C1_GlobalFunctionContainer1 {
  @GlobalFunction
  static Transition glob1(@Nullable @In int i5) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class C1_TestFlow_StepParameter {
  @StepFunction(transit = "transit")
  static void step1(@In int i5) {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class C1_TestFlow_StepCallParameter {
  @StepCall(globalFunctionContainer = C1_GlobalFunctionContainer1.class, globalFunctionName = "glob1", transit = "transit")
  static Transition step1(@Nullable @In int i5) {
    return null;
  }

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class C1_TestFlow_Step_GlobalTransitParameter {
  @StepFunction(globalTransitContainer = C1_GlobalFunctionContainer1.class, globalTransit = "glob1")
  static void step1() {}
}

@FlowType(name = "Test", firstStep = "step1")
class C1_TestFlow_StepCall_GlobalTransitParameter {
  @StepCall(globalFunctionContainer = C1_GlobalFunctionContainer1.class, globalFunctionName = "glob1", globalTransitContainer = C1_GlobalFunctionContainer1.class, globalTransit = "glob1")
  static Transition step1(@Nullable @In int i5) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class C1_TestFlow_TransitParameter {
  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit(@In int i5) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
class C1_TestFlow_TransitCallParameter {
  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitCall(globalFunctionContainer = C1_GlobalFunctionContainer1.class, globalFunctionName = "glob1")
  static Transition transit(@Nullable @In int i5) {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile")
class C1_TestEventProfile_EventProfileParameter {
  @EventFunction(types = EventType.AFTER_EXEC)
  static void event(@Nullable @In int i5) {}
}

@FlowType(name = "Test", firstStep = "step1")
@EventProfiles(C1_TestEventProfile_EventProfileParameter.class)
class C1_TestEventProfile_EventProfileParameter_Flow {
  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile")
class C1_TestEventProfile_EventProfileParameterFromFlow {
  @EventFunction(types = EventType.AFTER_EXEC)
  static void event(@Nullable @InFromFlow int i5) {}
}

@FlowType(name = "Test", firstStep = "step1")
@EventProfiles(C1_TestEventProfile_EventCallProfileParameter.class)
class C1_TestEventProfile_EventProfileParameterFromFlow_Flow {
  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile")
class C1_TestEventProfile_EventCallProfileParameter {
  @EventCall(globalFunctionContainer = C1_GlobalFunctionContainer1.class, types = EventType.AFTER_EXEC, globalFunctionName = "glob1")
  static Transition event(@Nullable @In int i5) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
@EventProfiles(C1_TestEventProfile_EventCallProfileParameter.class)
class C1_TestEventProfile_EventCallProfileParameter_Flow {
  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile")
class C1_TestEventProfile_EventCallProfileParameterFromFlow {
  @EventCall(globalFunctionContainer = C1_GlobalFunctionContainer1.class, types = EventType.AFTER_EXEC, globalFunctionName = "glob1")
  static Transition event(@Nullable @InFromFlow int i5) {
    return null;
  }
}

@FlowType(name = "Test", firstStep = "step1")
@EventProfiles(C1_TestEventProfile_EventCallProfileParameterFromFlow.class)
class C1_TestEventProfile_EventCallProfileParameterFromFlow_Flow {
  @StepFunction(transit = "transit")
  static void step1() {}

  @TransitFunction()
  static Transition transit() {
    return null;
  }
}
