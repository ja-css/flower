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