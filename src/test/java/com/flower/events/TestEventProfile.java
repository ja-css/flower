package com.flower.events;

import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventType;

@EventProfileContainer(name = "TestEventProfile")
public class TestEventProfile {
  public TestEventProfile() {
    System.out.println("TestEventProfile: constructor");
  }

  @EventFunction(types = EventType.BEFORE_FLOW)
  static void beforeFlowEvent() {
    System.out.println("TestEventProfile: BEFORE_FLOW");
  }

  @EventFunction(types = EventType.AFTER_FLOW)
  static void afterFlowEvent() {
    System.out.println("TestEventProfile: AFTER_FLOW");
  }

  @EventFunction(types = EventType.BEFORE_STEP)
  static void beforeStepEvent() {
    System.out.println("TestEventProfile: BEFORE_STEP");
  }

  @EventFunction(types = EventType.AFTER_STEP)
  static void afterStepEvent() {
    System.out.println("TestEventProfile: AFTER_STEP");
  }

  @EventFunction(types = EventType.BEFORE_STEP_ITERATION)
  static void beforeStepIterationEvent() {
    System.out.println("TestEventProfile: BEFORE_STEP_ITERATION");
  }

  @EventFunction(types = EventType.AFTER_STEP_ITERATION)
  static void afterStepIterationEvent() {
    System.out.println("TestEventProfile: AFTER_STEP_ITERATION");
  }

  @EventFunction(types = EventType.BEFORE_EXEC)
  static void beforeExecEvent() {
    System.out.println("TestEventProfile: BEFORE_EXEC");
  }

  @EventFunction(types = EventType.AFTER_EXEC)
  static void afterExecEvent() {
    System.out.println("TestEventProfile: AFTER_EXEC");
  }

  @EventFunction(types = EventType.BEFORE_TRANSIT)
  static void beforeTransitEvent() {
    System.out.println("TestEventProfile: BEFORE_TRANSIT");
  }

  @EventFunction(types = EventType.AFTER_TRANSIT)
  static void afterTransitEvent() {
    System.out.println("TestEventProfile: AFTER_TRANSIT");
  }

  @EventFunction(types = EventType.FLOW_EXCEPTION)
  static void flowExceptionEvent() {
    System.out.println("TestEventProfile: FLOW_EXCEPTION");
  }
}
