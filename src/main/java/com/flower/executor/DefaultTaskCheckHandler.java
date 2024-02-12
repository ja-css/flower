package com.flower.executor;

import javax.annotation.Nullable;

/** Default check handler mimicking Vertx reporting behavior */
public class DefaultTaskCheckHandler implements TaskCheckHandler {
    @Nullable private final CustomReporter customReporter;

    public DefaultTaskCheckHandler() {
        this.customReporter = null;
    }

    public DefaultTaskCheckHandler(CustomReporter customReporter) {
        this.customReporter = customReporter;
    }

    public void checkTask(TaskCheckingThread thread, Runnable task, long taskDurationNanos,
                          @Nullable Long taskTimeLimitNanos) {
        final String message =
            String.format("Task has been executing for %d ms, time limit is %s. Thread [%s] %s; task %s",
                taskDurationNanos / 1_000_000L,
                taskTimeLimitNanos == null ? "N/A" : taskTimeLimitNanos / 1_000_000L + " ms",
                thread.getState(),
                thread,
                task);

        Exception stackTraceOutput = new Exception(message);
        stackTraceOutput.setStackTrace(thread.getStackTrace());
        stackTraceOutput.printStackTrace();

        if (customReporter != null) {
            customReporter.report(stackTraceOutput);
        }
    }
}