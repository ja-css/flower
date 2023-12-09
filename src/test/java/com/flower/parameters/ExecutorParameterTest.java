package com.flower.parameters;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.Exec;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowFuture;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutorParameterTest {
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(P_TestExecutorFlow.class);
        flower.initialize();

        FlowFuture<P_TestExecutorFlow> execFlow = flower.getFlowExec(P_TestExecutorFlow.class).runFlow(new P_TestExecutorFlow());
        assertTrue(execFlow.getFuture().get().executor instanceof ListeningScheduledExecutorService);
    }
}

@FlowType(firstStep = "step")
class P_TestExecutorFlow {
    @State Executor executor;

    @StepFunction(transit = "transit")
    static void step(@Exec Executor flowerExecutor,
                     @Out OutPrm<Executor> executor) {
        executor.setOutValue(flowerExecutor);
    }

    @TransitFunction
    static Transition transit(@Terminal Transition end) {
        return end;
    }
}

//TODO: test-cover other call contexts: Transit, Call, Event, etc