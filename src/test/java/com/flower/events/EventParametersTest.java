package com.flower.events;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.flower.anno.event.Concurrency;
import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.events.EventInfo;
import com.flower.anno.params.events.FlowInfo;
import com.flower.anno.params.events.InFromFlow;
import com.flower.anno.params.events.StepInfo;
import com.flower.anno.params.events.TransitionInfo;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowInfoPrm;
import com.flower.conf.StepInfoPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EventParametersTest {
  @Test
  void goodBindingsTest() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(E2_TestFlow1.class);
    flower.registerEventProfile(E2_TestEventProfile1.class);

    flower.initialize();

    FlowFuture<E2_TestFlow1> result =
        flower.getFlowExec(E2_TestFlow1.class).runFlow(new E2_TestFlow1());
    result.getFuture().get();
  }

  @Test
  void throwsInEventTest() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(E2_TestFlow2.class);
    flower.registerEventProfile(E2_TestEventProfile2.class);

    flower.initialize();

    FlowFuture<E2_TestFlow2> result =
        flower.getFlowExec(E2_TestFlow2.class).runFlow(new E2_TestFlow2());
    result.getFuture().get();
  }

  @Test
  void throwsInBreakingEventTest() {
    Flower flower = new Flower();
    flower.registerFlow(E2_TestFlow3.class);
    flower.registerEventProfile(E2_TestEventProfile3.class);

    flower.initialize();

    FlowFuture<E2_TestFlow3> result =
        flower.getFlowExec(E2_TestFlow3.class).runFlow(new E2_TestFlow3());

    ExecutionException e = assertThrows(ExecutionException.class, () -> result.getFuture().get());
    Assertions.assertTrue(e.getCause().getMessage().contains("TestException"));
  }

  @Test
  void inFromFlowNoFieldTest() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(E2_TestFlow4.class);
    flower.registerEventProfile(E2_TestEventProfile4.class);

    flower.initialize();

    FlowFuture<E2_TestFlow4> result =
        flower.getFlowExec(E2_TestFlow4.class).runFlow(new E2_TestFlow4());
    result.getFuture().get();
  }

  @Test
  void inFromFlowNoFieldPrimitiveTypeTest() throws ExecutionException, InterruptedException {
    Flower flower = new Flower();
    flower.registerFlow(E2_TestFlow5.class);
    flower.registerEventProfile(E2_TestEventProfile5.class);

    flower.initialize();

    FlowFuture<E2_TestFlow5> result =
        flower.getFlowExec(E2_TestFlow5.class).runFlow(new E2_TestFlow5());
    result.getFuture().get();
  }
}

@FlowType(name = "Test", firstStep = "firstStep")
@EventProfiles({E2_TestEventProfile1.class})
class E2_TestFlow1 {
  @SimpleStepFunction
  static Transition firstStep(@Terminal Transition end) {
    System.out.println("FIRST STEP CALL");
    return end;
  }
}

// TODO: proper exception message on init when public constructor in not available
@EventProfileContainer(name = "TestEventProfile1")
class E2_TestEventProfile1 {
  public E2_TestEventProfile1() {}

  @EventFunction(types = {EventType.BEFORE_FLOW})
  static void event(@FlowInfo FlowInfoPrm flowInfo, @EventInfo EventType event) {
    System.out.printf("%s: FlowInfo %s %n", event, flowInfo);
  }

  @EventFunction(types = {EventType.BEFORE_STEP})
  static void event2(@StepInfo StepInfoPrm stepInfo, @EventInfo EventType event) {
    System.out.printf("%s: StepInfo %s %n", event, stepInfo);
  }

  @EventFunction(types = {EventType.AFTER_TRANSIT})
  static void event3(@TransitionInfo Transition transition, @EventInfo EventType event) {
    System.out.printf("%s: Transition %s %n", event, transition);
  }
}

@FlowType(name = "Test", firstStep = "firstStep")
@EventProfiles({E2_TestEventProfile2.class})
class E2_TestFlow2 {
  @SimpleStepFunction
  static Transition firstStep(@Terminal Transition end) {
    System.out.println("FIRST STEP CALL");
    return end;
  }
}

@EventProfileContainer(name = "TestEventProfile2")
class E2_TestEventProfile2 {
  public E2_TestEventProfile2() {}

  @EventFunction(types = {EventType.BEFORE_FLOW})
  static void event(@FlowInfo FlowInfoPrm flowInfo, @EventInfo EventType event) {
    throw new RuntimeException("TestException");
  }
}

@FlowType(name = "Test", firstStep = "firstStep")
@EventProfiles({E2_TestEventProfile3.class})
class E2_TestFlow3 {
  @SimpleStepFunction
  static Transition firstStep(@Terminal Transition end) {
    System.out.println("FIRST STEP CALL");
    return end;
  }
}

@EventProfileContainer(name = "TestEventProfile3")
class E2_TestEventProfile3 {
  public E2_TestEventProfile3() {}

  @EventFunction(
      types = {EventType.BEFORE_FLOW},
      concurrency = Concurrency.FINALIZER)
  static void event(@FlowInfo FlowInfoPrm flowInfo, @EventInfo EventType event) {
    throw new RuntimeException("TestException");
  }
}

@FlowType(name = "Test", firstStep = "firstStep")
@EventProfiles({E2_TestEventProfile4.class})
class E2_TestFlow4 {
  @SimpleStepFunction
  static Transition firstStep(@Terminal Transition end) {
    System.out.println("FIRST STEP CALL");
    return end;
  }
}

@EventProfileContainer(name = "TestEventProfile4")
class E2_TestEventProfile4 {
  public E2_TestEventProfile4() {}

  @EventFunction(types = EventType.BEFORE_FLOW)
  static void event(@Nullable @InFromFlow Integer parameterWithoutField) {
    System.out.println("Hello from Event " + parameterWithoutField);
  }
}

@FlowType(name = "Test", firstStep = "firstStep")
@EventProfiles({E2_TestEventProfile5.class})
class E2_TestFlow5 {
  @SimpleStepFunction
  static Transition firstStep(@Terminal Transition end) {
    System.out.println("FIRST STEP CALL");
    return end;
  }
}

@EventProfileContainer(name = "TestEventProfile5")
class E2_TestEventProfile5 {
  public E2_TestEventProfile5() {}

  @EventFunction(types = EventType.BEFORE_FLOW)
  static void event(@Nullable @InFromFlow int parameterWithoutField) {
    System.out.println("Hello from Event " + parameterWithoutField);
  }
}
