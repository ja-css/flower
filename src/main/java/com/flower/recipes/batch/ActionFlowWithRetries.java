package com.flower.recipes.batch;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.functions.StepFunction;
import com.flower.anno.functions.TransitFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.common.Output;
import com.flower.anno.params.transit.InRetOrException;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.InOutPrm;
import com.flower.conf.OutPrm;
import com.flower.conf.ReturnValueOrException;
import com.flower.conf.Transition;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.Nullable;

@FlowType(name="ActionFlowWithRetries", firstStep = "init")
public class ActionFlowWithRetries<ID> {
    @State final ID entityId;
    @State final Integer maxRetryAttempts;
    @State final String operationType;
    @State final BatchActionProgressCallback actionCallback;

    @State @Nullable Integer currentAttempt;

    public ActionFlowWithRetries(ID entityId,
                                 Integer maxRetryAttempts,
                                 String operationType,
                                 BatchActionProgressCallback actionCallback) {
        this.entityId = entityId;
        this.maxRetryAttempts = maxRetryAttempts;
        this.operationType = operationType;
        this.actionCallback = actionCallback;
    }

    @SimpleStepFunction
    public static Transition init(@Out OutPrm<Integer> currentAttempt,
                                  @StepRef Transition action) {
        currentAttempt.setOutValue(1);
        return action;
    }

    @StepFunction(transit = "retry")
    public static ListenableFuture<Void> action() {
        throw new UnsupportedOperationException("This Step is abstract, needs to be implemented by a subclass");
    }

    @TransitFunction
    public static <ID> ListenableFuture<Transition> retry(@InRetOrException ReturnValueOrException<Void> retValOrExc,
                                                          @In ID entityId,
                                                          @In String operationType,
                                                          @InOut(out=Output.OPTIONAL) InOutPrm<Integer> currentAttempt,
                                                          @In Integer maxRetryAttempts,
                                                          @In BatchActionProgressCallback actionCallback,
                                                          @StepRef Transition action,
                                                          @Terminal Transition end) {
        int currentAttemptVal = currentAttempt.getInValue();
        currentAttempt.setOutValue(currentAttemptVal);

        if (retValOrExc.exception().isPresent()) {
            Throwable exception = retValOrExc.exception().get();
            actionCallback.progressCallback(false, exception);
            if (currentAttemptVal < maxRetryAttempts) {
                //Increment attempt counter and retry
                currentAttempt.setOutValue(currentAttemptVal + 1);
                return Futures.immediateFuture(action);
            } else {
                //Attempts exceeded, fatal.
                return Futures.immediateFailedFuture(exception);
            }
        }
        return Futures.immediateFuture(end);
    }
}
