package com.flower.recipes.batch;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class BatchTest {
    static final int RUN_ACTION_COUNT = 10;
    static final int MAXIMUM_SIMULTANEOUS_EXECUTIONS = 5;

    @Test
    public void test() throws ExecutionException, InterruptedException {
        Flower flower = new Flower(10);
        flower.registerFlow(BatchFlow.class);
        flower.registerFlow(TestActionFlow.class);
        flower.initialize();

        FlowExec<BatchFlow> flowExec = flower.getFlowExec(BatchFlow.class);
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger activeCounter = new AtomicInteger(0);
        AtomicInteger minSimultaneousExecutions = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger maxSimultaneousExecutions = new AtomicInteger(Integer.MIN_VALUE);

        BatchFlow<Integer, TestActionFlow> batchFlow = new BatchFlow<>(
            IntStream.rangeClosed(1, RUN_ACTION_COUNT).boxed().toList(),
            (id, batchActionProgressCallback) -> new TestActionFlow(id, counter, activeCounter, minSimultaneousExecutions, maxSimultaneousExecutions),
            (id, isFinal, exception) -> { /*Noop*/ },
            MAXIMUM_SIMULTANEOUS_EXECUTIONS
        );

        //Doesn't throw
        flowExec.runFlow(batchFlow).getFuture().get();

        assertEquals(RUN_ACTION_COUNT, counter.get());
        assertEquals(0, activeCounter.get());
        assertEquals(0, minSimultaneousExecutions.get());
        assertEquals(MAXIMUM_SIMULTANEOUS_EXECUTIONS, maxSimultaneousExecutions.get());
    }
}

@FlowType(firstStep="action")
class TestActionFlow {
    @State final Integer id;
    @State final AtomicInteger counter;
    @State final AtomicInteger activeCounter;
    @State final AtomicInteger min;
    @State final AtomicInteger max;

    public TestActionFlow(Integer id,
                          AtomicInteger counter,
                          AtomicInteger activeCounter,
                          AtomicInteger min,
                          AtomicInteger max) {
        this.id = id;
        this.counter = counter;
        this.activeCounter = activeCounter;
        this.min = min;
        this.max = max;
    }

    @SimpleStepFunction
    public static Transition action(@In Integer id,
                                    @In AtomicInteger counter,
                                    @In AtomicInteger activeCounter,
                                    @In AtomicInteger min,
                                    @In AtomicInteger max,
                                    @Terminal Transition end) {
        System.out.println("Start: id " + id);

        counter.incrementAndGet();
        int minCandidate = activeCounter.getAndIncrement();
        try { Thread.sleep(1000); } catch (Exception e) { throw new RuntimeException(e); }
        int maxCandidate = activeCounter.getAndDecrement();

        for (int currentMin = min.get(); currentMin > minCandidate; currentMin = min.get()) {
            if (min.compareAndSet(currentMin, minCandidate)) {
                break;
            }
        }
        for (int currentMax = max.get(); currentMax < maxCandidate; currentMax = max.get()) {
            if (max.compareAndSet(currentMax, maxCandidate)) {
                break;
            }
        }

        System.out.println("Finish: id " + id);
        return end;
    }
}
