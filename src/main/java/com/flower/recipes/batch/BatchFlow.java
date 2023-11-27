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
public class BatchFlow<ID, MSG, FLOW> {
    @State final Collection<ID> ids;
    @State final BiFunction<ID, BatchActionProgressCallback<MSG>, FLOW> createActionFlow;
    @State final BatchProgressCallback<ID, MSG> batchProgressCallback;
    @State final Integer maximumSimultaneousExecutions;
    @State final Boolean throwOnChildException;

    @State final Map<ID, FlowFuture<FLOW>> inProgressMap = new ConcurrentHashMap<>();
    @State @Nullable Iterator<ID> idsIterator;

    public BatchFlow(Collection<ID> ids,
                     BiFunction<ID, BatchActionProgressCallback<MSG>, FLOW> createActionFlow,
                     BatchProgressCallback<ID, MSG> batchProgressCallback,
                     Integer maximumSimultaneousExecutions) {
        this(ids,
            createActionFlow,
            batchProgressCallback,
            maximumSimultaneousExecutions,
            true);
    }

    public BatchFlow(Collection<ID> ids,
                     BiFunction<ID, BatchActionProgressCallback<MSG>, FLOW> createActionFlow,
                     BatchProgressCallback<ID, MSG> batchProgressCallback,
                     Integer maximumSimultaneousExecutions,
                     Boolean throwOnChildException) {
        this.ids = ids;
        this.createActionFlow = createActionFlow;
        this.batchProgressCallback = batchProgressCallback;
        this.maximumSimultaneousExecutions = maximumSimultaneousExecutions;
        this.throwOnChildException = throwOnChildException;
    }

    @SimpleStepFunction
    public static <ID> Transition init(@In Collection<ID> ids,
                                  @Out OutPrm<Iterator<ID>> idsIterator,
                                  @StepRef Transition runBatch) {
        idsIterator.setOutValue(ids.iterator());
        return runBatch;
    }

    @SimpleStepFunction
    public static <ID, MSG, FLOW> ListenableFuture<Transition> runBatch(@In Iterator<ID> idsIterator,
                                                        @In BatchProgressCallback<ID, MSG> batchProgressCallback,
                                                        @In Integer maximumSimultaneousExecutions,
                                                        @In BiFunction<ID, BatchActionProgressCallback<MSG>, FLOW> createActionFlow,
                                                        @In Map<ID, FlowFuture<FLOW>> inProgressMap,
                                                        @In Boolean throwOnChildException,
                                                        @FlowFactory(dynamic=true) FlowFactoryPrm<FLOW> flowFactory,
                                                        @StepRef Transition runBatch,
                                                        @Terminal Transition end) throws ExecutionException, InterruptedException {
        //Cleanup finished flows, if any
        List<ID> doneIds = new ArrayList<>();
        for (Map.Entry<ID, FlowFuture<FLOW>> entry : inProgressMap.entrySet()) {
            ID actionId = entry.getKey();
            FlowFuture<FLOW> flowFuture = entry.getValue();
            if (flowFuture.getFuture().isDone()) {
                if (throwOnChildException) {
                    flowFuture.getFuture().get();
                }
                doneIds.add(actionId);
            }
        }
        for (ID doneId : doneIds) {
            batchProgressCallback.progressCallback(doneId, true, null, null);
            inProgressMap.remove(doneId);
        }
        int activeFlows = inProgressMap.size();

        if (idsIterator.hasNext()) {
            //Start more flows, if possible
            int flowsToAdd = maximumSimultaneousExecutions - activeFlows;
            for (int i = 0; idsIterator.hasNext() && i < flowsToAdd; i++) {
                ID nextId = idsIterator.next();
                FLOW nextActionFlow = createActionFlow.apply(nextId,
                    (isFinal, message, exception) -> batchProgressCallback.progressCallback(nextId, isFinal, null, exception));
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
