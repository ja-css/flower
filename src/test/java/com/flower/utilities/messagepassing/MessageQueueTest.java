package com.flower.utilities.messagepassing;

import com.flower.anno.flow.FlowType;
import com.flower.anno.flow.State;
import com.flower.anno.functions.SimpleStepFunction;
import com.flower.anno.params.common.In;
import com.flower.anno.params.common.InOut;
import com.flower.anno.params.transit.StepRef;
import com.flower.anno.params.transit.Terminal;
import com.flower.conf.FlowExec;
import com.flower.conf.FlowFuture;
import com.flower.conf.InOutPrm;
import com.flower.conf.Transition;
import com.flower.engine.Flower;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageQueueTest {
    static final int MESSAGE_COUNT = 10_000;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        MessageQueue<Integer, Integer> messageQueue = new MessageQueue<>();

        Flower flower = new Flower(2);
        flower.registerFlow(ProcessorFlow.class);
        flower.initialize();

        FlowExec<ProcessorFlow> flowExec = flower.getFlowExec(ProcessorFlow.class);
        AtomicInteger processedCount = new AtomicInteger(0);

        ProcessorFlow testFlow = new ProcessorFlow(messageQueue, processedCount, 1);
        FlowFuture<ProcessorFlow> flowFuture1 = flowExec.runFlow(testFlow);
        System.out.println("Flow created. Id: " + flowFuture1.getFlowId());
        ProcessorFlow testFlow2 = new ProcessorFlow(messageQueue, processedCount, 2);
        FlowFuture<ProcessorFlow> flowFuture2 = flowExec.runFlow(testFlow2);
        System.out.println("Flow created. Id: " + flowFuture2.getFlowId());

        // We use MessageSink here to hide all non-Producer related functions
        MessageSink<Integer, Integer> messageSink = messageQueue;
        List<ListenableFuture<Integer>> list = new ArrayList<>();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            System.out.println("Sending " + i);
            ListenableFuture<Integer> future = messageSink.add(i);
            list.add(future);
            if (i % 1000 == 0) {
                Thread.sleep(10);
            }
        }

        SettableFuture<Void> lock = SettableFuture.create();
        Futures.whenAllComplete(list).call(() -> lock.set(null), MoreExecutors.directExecutor());
        lock.get();

        while (!flowFuture1.getFuture().isDone() ||
                !flowFuture2.getFuture().isDone()) {
          System.out.println("Forcing wake-up to finalize flows");
          messageSink.notifyMessageListeners();
          Thread.sleep(10);
        }

        System.out.println("IsDone 1 " + flowFuture1.getFuture().isDone());
        System.out.println("IsDone 2 " + flowFuture2.getFuture().isDone());

        ProcessorFlow processorFlow1 = flowFuture1.getFuture().get();
        ProcessorFlow processorFlow2 = flowFuture2.getFuture().get();
        assertEquals(MESSAGE_COUNT, processorFlow1.processedByMe + processorFlow2.processedByMe);

        assertEquals(MESSAGE_COUNT, processedCount.get());
        for (int i = 0; i < list.size(); i++) {
            ListenableFuture<Integer> future = list.get(i);
            int result = future.get();
            assertEquals(result, i);
        }
    }

    @FlowType(firstStep = "PROCESS_MESSAGE")
    static class ProcessorFlow {
        @State final MessageQueue<Integer, Integer> messageQueue;
        @State final AtomicInteger processedCount;
        @State final int flowNumber;
        @State int processedByMe;

        public ProcessorFlow(MessageQueue<Integer, Integer> messageQueue,
                             AtomicInteger processedCount,
                             int flowNumber) {
          this.messageQueue = messageQueue;
          this.processedCount = processedCount;
          this.flowNumber = flowNumber;
        }

        @SimpleStepFunction
        public static ListenableFuture<Transition> PROCESS_MESSAGE(@In MessageQueue<Integer, Integer> messageQueue,
                                                 @In AtomicInteger processedCount,
                                                 @In int flowNumber,
                                                 @InOut(throwIfNull = true) InOutPrm<Integer> processedByMe,
                                                 @StepRef Transition PROCESS_MESSAGE,
                                                 @Terminal Transition END) {
            int processedByMeVal = processedByMe.getInValue();

            System.out.println(flowNumber + " Flow woke up. Processed: " + processedCount.get() + "; By me: " + processedByMeVal);

            while (!messageQueue.isEmpty()) {
                Pair<Integer, SettableFuture<Integer>> message = messageQueue.poll();
                // We must add this null check because the queue is concurrent and race conditions are possible
                if (message != null) {
                  int i = message.getKey();
                  System.out.println(flowNumber + " Processing " + i);
                  message.getRight().set(i);
                  processedCount.incrementAndGet();
                  processedByMeVal++;
                } else {
                    System.out.println(flowNumber + " Null retrieved from queue");
                }
            }
            processedByMe.setOutValue(processedByMeVal);

            if (processedCount.get() >= MESSAGE_COUNT) {
                System.out.println(flowNumber + " Flow DONE. Processed: " + processedCount.get() + "; By me: " + processedByMeVal);
                return Futures.immediateFuture(END);
            } else {
                System.out.println(flowNumber + " Flow Gon'sleep. Processed: " + processedCount.get() + "; By me: " + processedByMeVal);
                return Futures.transform(messageQueue.getMessageListener(),
                    unused_ -> PROCESS_MESSAGE,
                    MoreExecutors.directExecutor()
                );
            }
        }
    }
}
