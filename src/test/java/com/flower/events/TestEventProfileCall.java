package com.flower.events;

import com.flower.anno.event.EventCall;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.GlobalFunctionContainer;
import com.flower.anno.functions.GlobalFunction;

@GlobalFunctionContainer
class TestEventGlobalFunctionContainer {
  @GlobalFunction
  static void beforeFlowEvent() {
    System.out.println("TestEventProfile: BEFORE_FLOW");
  }

  @GlobalFunction
  static void afterFlowEvent() {
    System.out.println("TestEventProfile: AFTER_FLOW");
  }

  @GlobalFunction
  static void beforeStepEvent() {
    System.out.println("TestEventProfile: BEFORE_STEP");
  }

  @GlobalFunction
  static void afterStepEvent() {
    System.out.println("TestEventProfile: AFTER_STEP");
  }

  @GlobalFunction
  static void beforeStepIterationEvent() {
    System.out.println("TestEventProfile: BEFORE_STEP_ITERATION");
  }

  @GlobalFunction
  static void afterStepIterationEvent() {
    System.out.println("TestEventProfile: AFTER_STEP_ITERATION");
  }

  @GlobalFunction
  static void beforeExecEvent() {
    System.out.println("TestEventProfile: BEFORE_EXEC");
  }

  @GlobalFunction
  static void afterExecEvent() {
    System.out.println("TestEventProfile: AFTER_EXEC");
  }

  @GlobalFunction
  static void beforeTransitEvent() {
    System.out.println("TestEventProfile: BEFORE_TRANSIT");
  }

  @GlobalFunction
  static void afterTransitEvent() {
    System.out.println("TestEventProfile: AFTER_TRANSIT");
  }

  @GlobalFunction
  static void flowExceptionEvent() {
    System.out.println("TestEventProfile: FLOW_EXCEPTION");
  }
}

@EventProfileContainer(name = "TestEventProfileCall")
public class TestEventProfileCall {
  public TestEventProfileCall() {
    System.out.println("TestEventProfileCall: constructor");
  }

  @EventCall(globalFunctionName = "beforeFlowEvent", types = EventType.BEFORE_FLOW)
  static void beforeFlowEvent() {}

  @EventCall(globalFunctionName = "afterFlowEvent", types = EventType.AFTER_FLOW)
  static void afterFlowEvent() {}

  @EventCall(globalFunctionName = "beforeStepEvent", types = EventType.BEFORE_STEP)
  static void beforeStepEvent() {}

  @EventCall(globalFunctionName = "afterStepEvent", types = EventType.AFTER_STEP)
  static void afterStepEvent() {}

  @EventCall(
      globalFunctionName = "beforeStepIterationEvent",
      types = EventType.BEFORE_STEP_ITERATION)
  static void beforeStepIterationEvent() {}

  @EventCall(globalFunctionName = "afterStepIterationEvent", types = EventType.AFTER_STEP_ITERATION)
  static void afterStepIterationEvent() {}

  @EventCall(globalFunctionName = "beforeExecEvent", types = EventType.BEFORE_EXEC)
  static void beforeExecEvent() {}

  @EventCall(globalFunctionName = "afterExecEvent", types = EventType.AFTER_EXEC)
  static void afterExecEvent() {}

  @EventCall(globalFunctionName = "beforeTransitEvent", types = EventType.BEFORE_TRANSIT)
  static void beforeTransitEvent() {}

  @EventCall(globalFunctionName = "afterTransitEvent", types = EventType.AFTER_TRANSIT)
  static void afterTransitEvent() {}

  @EventCall(globalFunctionName = "flowExceptionEvent", types = EventType.FLOW_EXCEPTION)
  static void flowExceptionEvent() {}
}
