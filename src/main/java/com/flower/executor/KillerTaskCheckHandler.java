package com.flower.executor;

import javax.annotation.Nullable;

/** EXPERIMENTAL:
 * Check handler that terminates a task if it's got a problem.
 * Somehow it ends task execution without killing a thread in a thread pool,
 * but I can't explain how it works or whether this behavior is portable.
 *
 * Thread.stop() is deprecated
 * https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
 */
public class KillerTaskCheckHandler extends DefaultTaskCheckHandler {
    public KillerTaskCheckHandler() {
    }

    public KillerTaskCheckHandler(CustomReporter customReporter) {
        super(customReporter);
    }

    @Override
    public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
        super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
        thread.stop();
    }
}
