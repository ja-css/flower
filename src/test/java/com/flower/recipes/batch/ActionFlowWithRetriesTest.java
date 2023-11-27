package com.flower.recipes.batch;

import com.flower.anno.flow.FlowType;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.params.common.In;
import com.flower.conf.FlowExec;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActionFlowWithRetriesTest {
    @Test
    public void test() throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(ActionFlowWithRetries.class);
        flower.registerFlow(TestActionFlowWithRetries.class);
        flower.initialize();

        FlowExec<TestActionFlowWithRetries> flowExec = flower.getFlowExec(TestActionFlowWithRetries.class);
        TestActionFlowWithRetries flowFails2RetryAttempts = new TestActionFlowWithRetries("dummyActionId",
            2,
            "dummyOperationType",
            (isFinal, msg, exception) -> {
                System.out.println(exception);
            });
        assertThrows(ExecutionException.class, () -> flowExec.runFlow(flowFails2RetryAttempts).getFuture().get());

        TestActionFlowWithRetries flowSucceeds3RetryAttempts = new TestActionFlowWithRetries("dummyActionId",
            3,
            "dummyOperationType",
            (isFinal, msg, exception) -> {
                System.out.println(exception);
            });
        //Doesn't throw
        flowExec.runFlow(flowSucceeds3RetryAttempts).getFuture().get();
    }
}

@FlowType(extendz="ActionFlowWithRetries", firstStep="init")
class TestActionFlowWithRetries extends ActionFlowWithRetries<String, String> {
    public TestActionFlowWithRetries(String actionId,
                                     Integer maxRetryAttempts,
                                     String operationType,
                                     BatchActionProgressCallback<String> actionCallback) {
        super(actionId, maxRetryAttempts, operationType, actionCallback);
    }

    @StepFunction(transit = "retry")
    public static ListenableFuture<Void> action(@In Integer currentAttempt) {
        if (currentAttempt == 3) {
            //Success
            return null;
        } else {
            return Futures.immediateFailedFuture(new Exception("Test Exception. Failure #" + currentAttempt));
        }
    }
}
