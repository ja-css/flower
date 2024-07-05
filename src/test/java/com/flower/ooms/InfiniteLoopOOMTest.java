package com.flower.ooms;

import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.engine.Flower;
import java.util.concurrent.ExecutionException;

/**
 * Manual test.
 * Run and watch process's RAM usage.
 * Should not OOM or grow, memory usage should be stable.
 * I'm running with:
 *      -XX:+UseSerialGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/john/dumps -Xms16m -Xmx32m
 */
public class InfiniteLoopOOMTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Flower flower = new Flower();
        flower.registerFlow(InfiniteLoopFlow.class);
        flower.initialize();

        FlowExec<InfiniteLoopFlow> flowExec = flower.getFlowExec(InfiniteLoopFlow.class);

        InfiniteLoopFlow testFlow = new InfiniteLoopFlow();
        FlowFuture<InfiniteLoopFlow> flowFuture = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture.getFlowId());

        InfiniteLoopFlow flow = flowFuture.getFuture().get();

        System.out.println("Flows done. " + flow);
        flower.shutdownScheduler();
    }
}
