package com.flower.parameters;

import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CheckNotNullTest {
    @Test
    public void test_NullableFinalFlow() {
        Flower flower = new Flower();
        flower.registerFlow(NullableFinalFlow.class);
        assertThrows(IllegalStateException.class, () -> flower.initialize());
    }

    @Test
    public void test_NullableFinalEvent() {
        Flower flower = new Flower();
        flower.registerEventProfile(NullableFinalEventProfile.class);
        assertThrows(IllegalStateException.class, () -> flower.initialize());
    }

    @Test
    public void test_CheckNull() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(CheckNullFlow.class);
        flower.initialize();
        assertThrows(ExecutionException.class,
            () -> flower.getFlowExec(CheckNullFlow.class).runFlow(new CheckNullFlow()).getFuture().get());
    }

    @Test
    public void test_CheckNull2() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(CheckNullFlow2.class);
        flower.registerEventProfile(CheckNullEventProfile.class);
        flower.registerEventProfile(CheckNullEventProfile2.class);
        flower.initialize();
        flower.getFlowExec(CheckNullFlow2.class).runFlow(new CheckNullFlow2()).getFuture().get();
        //TODO: while CheckNullEventProfile throws exception as expected due to null check, but there is no way ATM to obtain exceptions from Event Profiles
        //TODO: implement when we have a mechanism to track such exceptions
        //M.B. EventExceptionCallback?
    }
}

@FlowType(firstStep = "step")
class NullableFinalFlow {
    @State @Nullable final Integer i = null;

    @StepFunction(transit = "transit")
    static void step(@In Integer i) {
        System.out.println(i);
    }

    @TransitFunction
    static Transition transit(@Terminal Transition end) {
        return end;
    }
}

@EventProfileContainer(name = "TestEventProfile")
class NullableFinalEventProfile {
    @State @Nullable final Integer i = null;

    public NullableFinalEventProfile() {
        System.out.println("TestEventProfile: constructor");
    }

    @EventFunction(types = EventType.BEFORE_FLOW)
    static void beforeFlowEvent(@In Integer i) {
        System.out.println("TestEventProfile: BEFORE_FLOW");
    }
}

@FlowType(firstStep = "step")
class CheckNullFlow {
    @State @Nullable final Integer i = null;

    @StepFunction(transit = "transit")
    static void step(@In(throwIfNull=true) Integer i) {
        System.out.println(i);
    }

    @TransitFunction
    static Transition transit(@Terminal Transition end) {
        return end;
    }
}

@FlowType(firstStep = "step")
@EventProfiles({"CheckNullEventProfile", "CheckNullEventProfile2"})
class CheckNullFlow2 {
    @State final Integer i = 6;

    @StepFunction(transit = "transit")
    static void step(@In Integer i) {
        System.out.println(i);
    }

    @TransitFunction
    static Transition transit(@Terminal Transition end) {
        return end;
    }
}

@EventProfileContainer(name = "CheckNullEventProfile")
class CheckNullEventProfile {
    @State @Nullable final Integer i = null;

    public CheckNullEventProfile() {
        System.out.println("TestEventProfile: constructor");
    }

    @EventFunction(types = EventType.BEFORE_FLOW)
    static void beforeFlowEvent(@In(throwIfNull=true) Integer i) {
        System.out.println("CheckNullEventProfile: BEFORE_FLOW");
    }
}

@EventProfileContainer(name = "CheckNullEventProfile2")
class CheckNullEventProfile2 {
    @State final Integer i = 2;

    public CheckNullEventProfile2() {
        System.out.println("TestEventProfile: constructor");
    }

    @EventFunction(types = EventType.BEFORE_FLOW)
    static void beforeFlowEvent(@In(throwIfNull=true) Integer i) {
        System.out.println("CheckNullEventProfile2: BEFORE_FLOW");
    }
}