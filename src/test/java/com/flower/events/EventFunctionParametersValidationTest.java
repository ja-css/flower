package com.flower.events;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.events.EventInfo;
import com.flower.anno.params.events.FlowInfo;
import com.flower.anno.params.events.StepInfo;
import com.flower.anno.params.events.TransitionInfo;
import com.flower.conf.FlowInfoPrm;
import com.flower.conf.StepInfoPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.lang.annotation.AnnotationFormatError;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventFunctionParametersValidationTest {
  @Test
  void goodBindingsTest() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow1.class);
    flower.registerEventProfile(E1_TestEventProfile1.class);

    flower.initialize();
  }

  @Test
  void failedStepBindings2Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow2.class);
    flower.registerEventProfile(E1_TestEventProfile2.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @StepInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedStepBindings3Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow3.class);
    flower.registerEventProfile(E1_TestEventProfile3.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @StepInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedStepBindings4Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow4.class);
    flower.registerEventProfile(E1_TestEventProfile4.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @StepInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings5Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow5.class);
    flower.registerEventProfile(E1_TestEventProfile5.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings6Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow6.class);
    flower.registerEventProfile(E1_TestEventProfile6.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings7Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow7.class);
    flower.registerEventProfile(E1_TestEventProfile7.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings8Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow8.class);
    flower.registerEventProfile(E1_TestEventProfile8.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings9Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow9.class);
    flower.registerEventProfile(E1_TestEventProfile9.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings10Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow10.class);
    flower.registerEventProfile(E1_TestEventProfile10.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings11Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow11.class);
    flower.registerEventProfile(E1_TestEventProfile11.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings12Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow12.class);
    flower.registerEventProfile(E1_TestEventProfile12.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings13Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow13.class);
    flower.registerEventProfile(E1_TestEventProfile13.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }

  @Test
  void failedTransitBindings14Test() {
    Flower flower = new Flower();
    flower.registerFlow(E1_TestFlow14.class);
    flower.registerEventProfile(E1_TestEventProfile14.class);

    AnnotationFormatError e = assertThrows(AnnotationFormatError.class, flower::initialize);
    Assertions.assertTrue(
        e.getMessage()
            .contains(
                "annotated as @TransitionInfo can only be used in EventFunction for the following EventTypes"));
  }
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile1.class})
class E1_TestFlow1 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile1")
class E1_TestEventProfile1 {
  @EventFunction(
      types = {
        EventType.BEFORE_FLOW,
        EventType.AFTER_FLOW,
        EventType.BEFORE_STEP,
        EventType.AFTER_STEP,
        EventType.BEFORE_STEP_ITERATION,
        EventType.AFTER_STEP_ITERATION,
        EventType.BEFORE_EXEC,
        EventType.AFTER_EXEC,
        EventType.BEFORE_TRANSIT,
        EventType.AFTER_TRANSIT,
        EventType.FLOW_EXCEPTION
      })
  static void event(@FlowInfo FlowInfoPrm flowInfo, @EventInfo EventType event) {}

  @EventFunction(
      types = {
        EventType.BEFORE_STEP,
        EventType.AFTER_STEP,
        EventType.BEFORE_STEP_ITERATION,
        EventType.AFTER_STEP_ITERATION,
        EventType.BEFORE_EXEC,
        EventType.AFTER_EXEC,
        EventType.BEFORE_TRANSIT,
        EventType.AFTER_TRANSIT
      })
  static void event2(@StepInfo StepInfoPrm stepInfo) {}

  @EventFunction(types = {EventType.AFTER_TRANSIT})
  static void event3(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile2.class})
class E1_TestFlow2 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile2")
class E1_TestEventProfile2 {
  @EventFunction(types = {EventType.BEFORE_FLOW})
  static void event(@StepInfo StepInfoPrm stepInfo) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile3.class})
class E1_TestFlow3 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile3")
class E1_TestEventProfile3 {
  @EventFunction(types = {EventType.AFTER_FLOW})
  static void event(@StepInfo StepInfoPrm stepInfo) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile4.class})
class E1_TestFlow4 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile4")
class E1_TestEventProfile4 {
  @EventFunction(types = {EventType.FLOW_EXCEPTION})
  static void event(@StepInfo StepInfoPrm stepInfo) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile5.class})
class E1_TestFlow5 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile5")
class E1_TestEventProfile5 {
  @EventFunction(types = {EventType.BEFORE_FLOW})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile6.class})
class E1_TestFlow6 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile6")
class E1_TestEventProfile6 {
  @EventFunction(types = {EventType.AFTER_FLOW})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile7.class})
class E1_TestFlow7 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile7")
class E1_TestEventProfile7 {
  @EventFunction(types = {EventType.BEFORE_STEP})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile8.class})
class E1_TestFlow8 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile8")
class E1_TestEventProfile8 {
  @EventFunction(types = {EventType.AFTER_STEP})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile9.class})
class E1_TestFlow9 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile9")
class E1_TestEventProfile9 {
  @EventFunction(types = {EventType.BEFORE_STEP_ITERATION})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile10.class})
class E1_TestFlow10 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile10")
class E1_TestEventProfile10 {
  @EventFunction(types = {EventType.AFTER_STEP_ITERATION})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile11.class})
class E1_TestFlow11 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile11")
class E1_TestEventProfile11 {
  @EventFunction(types = {EventType.BEFORE_EXEC})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile12.class})
class E1_TestFlow12 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile12")
class E1_TestEventProfile12 {
  @EventFunction(types = {EventType.AFTER_EXEC})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile13.class})
class E1_TestFlow13 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile13")
class E1_TestEventProfile13 {
  @EventFunction(types = {EventType.BEFORE_TRANSIT})
  static void event(@TransitionInfo Transition transition) {}
}

@FlowType(name = "Test", firstStep = "transit")
@EventProfiles({E1_TestEventProfile14.class})
class E1_TestFlow14 {
  @SimpleStepFunction
  static Transition transit() {
    return null;
  }
}

@EventProfileContainer(name = "TestEventProfile14")
class E1_TestEventProfile14 {
  @EventFunction(types = {EventType.FLOW_EXCEPTION})
  static void event(@TransitionInfo Transition transition) {}
}
