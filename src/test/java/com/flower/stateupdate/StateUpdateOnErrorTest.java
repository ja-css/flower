package com.flower.stateupdate;

import com.flower.anno.event.EventFunction;
import com.flower.anno.event.EventProfileContainer;
import com.flower.anno.event.EventProfiles;
import com.flower.anno.event.EventType;
import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.OutPrm;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StateUpdateOnErrorTest {
    @Test
    public void partialStateUpdateTest() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(TestStateChange.class);
        flower.initialize();

        FlowExec<TestStateChange> flowExec = flower.getFlowExec(TestStateChange.class);

        TestStateChange testFlow = new TestStateChange();
        FlowFuture<TestStateChange> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        TestStateChange doneFlow = flowFuture.getFuture().get();
        assertEquals(doneFlow.exception.getMessage(), "TEST_EXCEPTION");
        assertEquals(doneFlow.outStr, "OUT");
    }

    @Test
    public void partialStateUpdate2Test() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(TestStateChange2.class);
        flower.initialize();

        FlowExec<TestStateChange2> flowExec = flower.getFlowExec(TestStateChange2.class);

        TestStateChange2 testFlow = new TestStateChange2();
        FlowFuture<TestStateChange2> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        try {
            flowFuture.getFuture().get();
        } catch (Exception e) {
        }
        assertEquals(testFlow.outStr, "OUT");
    }

    @Test
    public void partialStateUpdate3Test() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(TestStateChange3.class);
        flower.initialize();

        FlowExec<TestStateChange3> flowExec = flower.getFlowExec(TestStateChange3.class);

        TestStateChange3 testFlow = new TestStateChange3();
        FlowFuture<TestStateChange3> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        try {
            flowFuture.getFuture().get();
        } catch (Exception e) {
        }
        assertEquals(testFlow.outStr, "OUT");
    }

    @Test
    public void partialStateUpdate4Test() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(TestStateChange4.class);
        flower.initialize();

        FlowExec<TestStateChange4> flowExec = flower.getFlowExec(TestStateChange4.class);

        TestStateChange4 testFlow = new TestStateChange4();
        FlowFuture<TestStateChange4> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        try {
            flowFuture.getFuture().get();
        } catch (Exception e) {
            assertEquals(e.getCause().getMessage(), "TEST_EXCEPTION2");
        }
        assertEquals(testFlow.outStr, "OUT");
    }

    static AtomicReference<String> outStrString = new AtomicReference<>();
    @Test
    public void partialStateUpdate5Test() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(TestStateChange5.class);
        flower.registerEventProfile(OnErrorEventProfile.class);
        flower.initialize();

        FlowExec<TestStateChange5> flowExec = flower.getFlowExec(TestStateChange5.class);

        TestStateChange5 testFlow = new TestStateChange5();
        FlowFuture<TestStateChange5> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        try {
            flowFuture.getFuture().get();
        } catch (Exception e) {
            e.printStackTrace();
            //assertEquals(e.getCause().getMessage(), "TEST_EXCEPTION2");
        }
        assertEquals(testFlow.outStr, null);
        assertEquals(outStrString.get(), "Out From Event");
    }

    @Test
    public void partialStateUpdate6Test() {
        Flower flower = new Flower();
        flower.registerFlow(TestStateChange6.class);
        flower.initialize();

        FlowExec<TestStateChange6> flowExec = flower.getFlowExec(TestStateChange6.class);

        TestStateChange6 testFlow = new TestStateChange6();
        FlowFuture<TestStateChange6> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        try {
            flowFuture.getFuture().get();
        } catch (Exception e) {
            assertEquals(e.getCause().getMessage(), "TEST_EXCEPTION");
        }
        assertEquals(testFlow.outStr, "OUT");
    }

    @Test
    public void transitExceptionCatchTest() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(TransitExceptionCatchFlow.class);
        flower.initialize();

        FlowExec<TransitExceptionCatchFlow> flowExec = flower.getFlowExec(TransitExceptionCatchFlow.class);

        TransitExceptionCatchFlow testFlow = new TransitExceptionCatchFlow();
        FlowFuture<TransitExceptionCatchFlow> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        TransitExceptionCatchFlow doneFlow = flowFuture.getFuture().get();
        assertNull(doneFlow.nullField);
        assertTrue(doneFlow.exception.getMessage().contains("Function parameter marked as `CheckNotNull` has value null: [IN]. FlowField [nullField]"));

        System.out.println("Flow done. " + flowFuture.getFuture().get());
    }
}

//TODO: Also Test eventFunction
@FlowType(firstStep="erroneousStep")
class TestStateChange {
    @State String outStr;
    @State Throwable exception;

    @StepFunction(transit = "endProcessing")
    static void erroneousStep(@Out OutPrm<String> outStr) {
        outStr.setOutValue("OUT");
        throw new RuntimeException("TEST_EXCEPTION");
    }

    @TransitFunction
    static Transition endProcessing(@Out OutPrm<Throwable> exception,
                                    @Terminal Transition end,
                                    @InRetOrException ReturnValueOrException<Void> returnValueOrException) {
        if (returnValueOrException.exception().isPresent()) {
            exception.setOutValue(returnValueOrException.exception().get());
        }
        return end;
    }
}

@FlowType(firstStep="erroneousStep")
class TestStateChange2 {
    @State String outStr;

    @SimpleStepFunction()
    static Transition erroneousStep(@Out OutPrm<String> outStr) {
        outStr.setOutValue("OUT");
        throw new RuntimeException("TEST_EXCEPTION");
    }
}

@FlowType(firstStep="step")
class TestStateChange3 {
    @State String outStr;

    @StepFunction(transit = "endProcessing")
    static void step() {
    }

    @TransitFunction
    static Transition endProcessing(@Out OutPrm<String> outStr) {
        outStr.setOutValue("OUT");
        throw new RuntimeException("TEST_EXCEPTION");
    }
}

@FlowType(firstStep="erroneousStep")
class TestStateChange4 {
    @State String outStr;

    @StepFunction(transit = "endProcessing")
    static void erroneousStep() {
        throw new RuntimeException("TEST_EXCEPTION");
    }

    @TransitFunction
    static Transition endProcessing(@Out OutPrm<String> outStr,
                                    @InRetOrException ReturnValueOrException<Void> returnValueOrException) {
        outStr.setOutValue("OUT");
        throw new RuntimeException("TEST_EXCEPTION2");
    }
}

@FlowType(firstStep="step")
class TransitExceptionCatchFlow {
    @State String nullField = null;
    @State Throwable exception;

    @StepFunction(transit = "endProcessing")
    static void step(@In(checkNotNull=true) String nullField) {
        System.out.println(nullField);
    }

    @TransitFunction
    static Transition endProcessing(@Out OutPrm<Throwable> exception,
                                    @Terminal Transition end,
                                    @InRetOrException ReturnValueOrException<Void> returnValueOrException) {
        if (returnValueOrException.exception().isPresent()) {
            exception.setOutValue(returnValueOrException.exception().get());
        }
        return end;
    }
}

@FlowType(firstStep="erroneousStep")
@EventProfiles("OnErrorEventProfile")
class TestStateChange5 {
    @State String outStr;

    @SimpleStepFunction()
    static Transition erroneousStep(@In @Nullable String outStr,
                                    @Terminal Transition end) {
        System.out.println(outStr);
        return end;
    }
}

@EventProfileContainer(name = "OnErrorEventProfile")
class OnErrorEventProfile {
    @State String outStr;

    public OnErrorEventProfile() {}

    @EventFunction(types = EventType.BEFORE_FLOW)
    static void beforeFlowEvent(@Out OutPrm<String> outStr) {
        outStr.setOutValue("Out From Event");
        throw new RuntimeException("TEST_EXCEPTION");
    }

    @EventFunction(types = EventType.AFTER_FLOW)
    static void afterFlowEvent(@In String outStr) {
        System.out.println(outStr);
        StateUpdateOnErrorTest.outStrString.set(outStr);
        System.out.println("TestEventProfile: AFTER_FLOW");
    }
}

@FlowType(firstStep="step")
class TestStateChange6 {
    @State String outStr;
    @State String outStr2;

    @SimpleStepFunction()
    static Transition step(@Out OutPrm<String> outStr,
                     @Out OutPrm<String> outStr2,
                     @Terminal Transition end) {
        outStr2.setOutFuture(Futures.immediateFailedFuture(new Exception("TEST_EXCEPTION")));
        outStr.setOutValue("OUT");
        return end;
    }
}
