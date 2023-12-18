package com.flower.events;

import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.events.FlowException;
import com.flower.anno.params.events.InFromFlow;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.FlowInfoPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UseFlowParametersInEventProfileTest {
    static Throwable DETECTED_EXCEPTION;

    @Test
    public void test() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(TestFlowParametersInEventProfile.class);
        flower.registerEventProfile(TestEventProfileFlowParameters.class);
        flower.initialize();

        FlowExec<TestFlowParametersInEventProfile> flowExec = flower.getFlowExec(TestFlowParametersInEventProfile.class);

        TestFlowParametersInEventProfile testFlow = new TestFlowParametersInEventProfile("jamirov");
        FlowFuture<TestFlowParametersInEventProfile> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        assertThrows(ExecutionException.class, () -> flowFuture.getFuture().get(), "This is exception from TestFlowParametersInEventProfile");
        assertEquals(DETECTED_EXCEPTION.getMessage(), "This is exception from TestFlowParametersInEventProfile");
    }
}

@FlowType(firstStep = "testStep")
@EventProfiles({TestEventProfileFlowParameters.class})
class TestFlowParametersInEventProfile {
    @State private final String username;

    public TestFlowParametersInEventProfile(String username) {
        this.username = username;
    }

    @SimpleStepFunction
    static Transition testStep(@In String username,
                               @Terminal Transition end) {
        System.out.printf("This is called from testStep for user %s.%n", username);
        throw new RuntimeException("This is exception from TestFlowParametersInEventProfile");
    }
}

@EventProfileContainer(name = "TestEventProfileFlowParameters")
class TestEventProfileFlowParameters {
    public TestEventProfileFlowParameters() {
        System.out.println("TestEventProfile: constructor");
    }

    @EventFunction(types = EventType.FLOW_EXCEPTION)
    public static void flowExceptionEvent(@InFromFlow @Nullable String username,
                                          @FlowException Throwable flowException) {
        UseFlowParametersInEventProfileTest.DETECTED_EXCEPTION = flowException;

        System.out.println("TestEventProfile: FLOW_EXCEPTION");
        System.out.println("For user: " + username);
        System.out.println("Detects exception " + flowException);
    }
}
