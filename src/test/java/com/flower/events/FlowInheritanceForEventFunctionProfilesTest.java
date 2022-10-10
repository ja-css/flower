package com.flower.events;

import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.events.EventInfo;
import com.flower.anno.params.events.FlowInfo;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowInfoPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

public class FlowInheritanceForEventFunctionProfilesTest {
  @Test
  public void testInheritanceWithStateOverride() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(E3_TestFlow1.class);
    flower.registerFlow(E3_TestChildFlow1_1.class);
    flower.registerEventProfile(E3_TestEventProfile1.class);
    flower.registerEventProfile(E3_TestEventProfile2.class);

    flower.initialize();

    FlowExec<E3_TestChildFlow1_1> flowExec = flower.getFlowExec(E3_TestChildFlow1_1.class);

    FlowFuture<E3_TestChildFlow1_1> future = flowExec.runFlow(new E3_TestChildFlow1_1());
    E3_TestChildFlow1_1 child = future.getFuture().get();
  }
}

@FlowType(name = "ParentFlow", firstStep = "transit")
@EventProfiles({"TestEventProfile1"})
class E3_TestFlow1 {
  @SimpleStepFunction
  static Transition transit(@Terminal Transition end) {
    System.out.println("step");
    return end;
  }
}

@FlowType(extendz = "ParentFlow", firstStep = "transit")
@EventProfiles({"TestEventProfile2"})
class E3_TestChildFlow1_1 extends E3_TestFlow1 {}

@EventProfileContainer(name = "TestEventProfile1")
class E3_TestEventProfile1 {
  public E3_TestEventProfile1() {}

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
  static void event(@FlowInfo FlowInfoPrm flowInfo, @EventInfo EventType event) {
    System.out.printf("Profile1 %s : Flow %s%n", event, flowInfo);
  }
}

@EventProfileContainer(name = "TestEventProfile2")
class E3_TestEventProfile2 {
  public E3_TestEventProfile2() {}

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
  static void event(@FlowInfo FlowInfoPrm flowInfo, @EventInfo EventType event) {
    System.out.printf("Profile2 %s : Flow %s%n", event, flowInfo);
  }
}
