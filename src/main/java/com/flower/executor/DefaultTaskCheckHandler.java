package com.flower.executor;

import javax.annotation.Nullable;

/** Default check handler mimicking Vertx reporting behavior
 * <p>
 * TODO: I've discovered that false positives are possible in Flower when all Flows are asleep.
 * TODO: might require detect/ignore in situations when blocking comes directly from Flower due to no work available.
 * <p>
 * E.g. the following output happens in MessageQueueTest when one of the Flows/Threads experiences work starvation:
 * java.lang.Exception: Task has been executing for 1522036390 ns, time limit is N/A. Thread [BLOCKED] Thread[task-exec-time-checking-pool-1-thread-1,5,main]
 * 	at java.base@17.0.11/jdk.internal.misc.Unsafe.allocateInstance(Native Method)
 * 	at java.base@17.0.11/java.lang.invoke.DirectMethodHandle.allocateInstance(DirectMethodHandle.java:520)
 * 	at java.base@17.0.11/java.lang.invoke.DirectMethodHandle$Holder.newInvokeSpecial(DirectMethodHandle$Holder)
 * 	at java.base@17.0.11/java.lang.invoke.Invokers$Holder.linkToTargetMethod(Invokers$Holder)
 * 	at app//com.flower.engine.runner.step.StepAndTransitContext.call(StepAndTransitContext.java:84)
 * 	at app//com.flower.engine.runner.FlowCallContext.lambda$runStep$2(FlowCallContext.java:141)
 * 	at app//com.flower.engine.runner.FlowCallContext$$Lambda$397/0x00007f8e1c171b40.apply(Unknown Source)
 * 	at app//com.google.common.util.concurrent.AbstractTransformFuture$AsyncTransformFuture.doTransform(AbstractTransformFuture.java:221)
 * 	at app//com.google.common.util.concurrent.AbstractTransformFuture$AsyncTransformFuture.doTransform(AbstractTransformFuture.java:208)
 * 	at app//com.google.common.util.concurrent.AbstractTransformFuture.run(AbstractTransformFuture.java:122)
 * 	at java.base@17.0.11/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539)
 * 	at java.base@17.0.11/java.util.concurrent.FutureTask.run(FutureTask.java:264)
 * 	at java.base@17.0.11/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
 * 	at java.base@17.0.11/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
 * 	at java.base@17.0.11/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
 * 	at java.base@17.0.11/java.lang.Thread.run(Thread.java:842)
 */
public class DefaultTaskCheckHandler implements TaskCheckHandler {
    @Nullable private final CustomReporter customReporter;

    public DefaultTaskCheckHandler() {
        this.customReporter = null;
    }

    public DefaultTaskCheckHandler(CustomReporter customReporter) {
        this.customReporter = customReporter;
    }

    public void checkTask(TaskCheckingThread thread, long taskDurationNanos,
                          @Nullable Long taskTimeLimitNanos) {
        final String message =
            String.format("Task has been executing for %d ns, time limit is %s. Thread [%s] %s",
                taskDurationNanos,
                taskTimeLimitNanos == null ? "N/A" : taskTimeLimitNanos + " ns",
                thread.getState(),
                thread);

        Exception stackTraceOutput = new Exception(message);
        stackTraceOutput.setStackTrace(thread.getStackTrace());
        stackTraceOutput.printStackTrace();

        if (customReporter != null) {
            customReporter.report(stackTraceOutput);
        }
    }
}