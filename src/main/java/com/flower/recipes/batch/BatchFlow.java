package com.flower.recipes.batch;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.Out;
import com.flower.anno.params.step.FlowFactory;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowFactoryPrm;
import com.flower.conf.FlowFuture;
import com.flower.conf.OutPrm;
import com.flower.conf.Transition;
import com.flower.utilities.FuturesTool;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

@FlowType(firstStep = "init")
public class BatchFlow<CHILD_ID, PROGRESS_MSG, FLOW> {
    @State final Collection<CHILD_ID> ids;
    @State final BiFunction<CHILD_ID, BatchActionProgressCallback<PROGRESS_MSG>, FLOW> createActionFlow;
    @State final BatchProgressCallback<CHILD_ID, PROGRESS_MSG> batchProgressCallback;
    @State final Integer maximumSimultaneousExecutions;
    @State final Boolean throwOnChildException;

    @State final Map<CHILD_ID, FlowFuture<FLOW>> inProgressMap = new ConcurrentHashMap<>();
    @State @Nullable Iterator<CHILD_ID> idsIterator;

    public BatchFlow(Collection<CHILD_ID> ids,
                     BiFunction<CHILD_ID, BatchActionProgressCallback<PROGRESS_MSG>, FLOW> createActionFlow,
                     BatchProgressCallback<CHILD_ID, PROGRESS_MSG> batchProgressCallback,
                     Integer maximumSimultaneousExecutions) {
        this(ids,
            createActionFlow,
            batchProgressCallback,
            maximumSimultaneousExecutions,
            true);
    }

    public BatchFlow(Collection<CHILD_ID> ids,
                     BiFunction<CHILD_ID, BatchActionProgressCallback<PROGRESS_MSG>, FLOW> createActionFlow,
                     BatchProgressCallback<CHILD_ID, PROGRESS_MSG> batchProgressCallback,
                     Integer maximumSimultaneousExecutions,
                     Boolean throwOnChildException) {
        this.ids = ids;
        this.createActionFlow = createActionFlow;
        this.batchProgressCallback = batchProgressCallback;
        this.maximumSimultaneousExecutions = maximumSimultaneousExecutions;
        this.throwOnChildException = throwOnChildException;
    }

    @SimpleStepFunction
    public static <CHILD_ID> Transition init(@In Collection<CHILD_ID> ids,
                                  @Out OutPrm<Iterator<CHILD_ID>> idsIterator,
                                  @StepRef Transition runBatch) {
        idsIterator.setOutValue(ids.iterator());
        return runBatch;
    }

    @SimpleStepFunction
    public static <CHILD_ID, PROGRESS_MSG, FLOW> ListenableFuture<Transition> runBatch(@In Iterator<CHILD_ID> idsIterator,
                                                                                       @In BatchProgressCallback<CHILD_ID, PROGRESS_MSG> batchProgressCallback,
                                                                                       @In Integer maximumSimultaneousExecutions,
                                                                                       @In BiFunction<CHILD_ID, BatchActionProgressCallback<PROGRESS_MSG>, FLOW> createActionFlow,
                                                                                       @In Map<CHILD_ID, FlowFuture<FLOW>> inProgressMap,
                                                                                       @In Boolean throwOnChildException,
                                                                                       @FlowFactory(dynamic=true) FlowFactoryPrm<FLOW> flowFactory,
                                                                                       @StepRef Transition runBatch,
                                                                                       @Terminal Transition end) throws ExecutionException, InterruptedException {
        //Cleanup finished flows, if any
        List<CHILD_ID> doneIds = new ArrayList<>();
        for (Map.Entry<CHILD_ID, FlowFuture<FLOW>> entry : inProgressMap.entrySet()) {
            CHILD_ID actionId = entry.getKey();
            FlowFuture<FLOW> flowFuture = entry.getValue();
            if (flowFuture.getFuture().isDone()) {
                if (throwOnChildException) {
                    flowFuture.getFuture().get();
                }
                doneIds.add(actionId);
            }
        }
        for (CHILD_ID doneId : doneIds) {
            //TODO: Is it correct to have this here, apart from proxying callbacks from child flows?
            batchProgressCallback.progressCallback(doneId, true, null, null);
            inProgressMap.remove(doneId);
        }
        int activeFlows = inProgressMap.size();

        if (idsIterator.hasNext()) {
            //Start more flows, if possible
            int flowsToAdd = maximumSimultaneousExecutions - activeFlows;
            for (int i = 0; idsIterator.hasNext() && i < flowsToAdd; i++) {
                CHILD_ID nextId = idsIterator.next();
                FLOW nextActionFlow = createActionFlow.apply(nextId,
                    (isFinal, message, exception) -> batchProgressCallback.progressCallback(nextId, isFinal, message, exception));
                FlowFuture<FLOW> flowFuture = flowFactory.runChildFlow(nextActionFlow);
                inProgressMap.put(nextId, flowFuture);
            }
        } else {
            //Finalize if all done
            if (activeFlows == 0) {
                return Futures.immediateFuture(end);
            }
        }

        //If there are still active flows, wait and repeat
        return Futures.transform(
            FuturesTool.whenAnyCompleteIgnoreResult(inProgressMap.values().stream().map(FlowFuture::getFuture).toList()),
            ignored -> runBatch,
            MoreExecutors.directExecutor());
    }
}
