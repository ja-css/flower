package com.flower.executor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultTaskChecker implements TaskChecker {
    /** The default value of task execution check interval = 1000 ms. */
    public static final long DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    /** The default value of task execution check interval unit = {@link TimeUnit#MILLISECONDS} */
    public static final TimeUnit DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL_UNIT = TimeUnit.MILLISECONDS;

    public static class Builder {
        @Nullable protected Long taskExecTimeCheckInterval = null;
        @Nullable protected TimeUnit taskExecTimeCheckIntervalUnit = null;
        @Nullable protected Long maxTaskExecTime = null;
        @Nullable protected TimeUnit maxTaskExecTimeUnit = null;
        @Nullable protected TaskCheckHandler checkHandler = null;
        @Nullable protected CustomReporter customReporter = null;

        public Builder taskExecTimeCheckInterval(Long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit) {
            this.taskExecTimeCheckInterval = taskExecTimeCheckInterval;
            this.taskExecTimeCheckIntervalUnit = taskExecTimeCheckIntervalUnit;
            return this;
        }

        public Builder maxTaskExecTime(Long maxTaskExecTime, TimeUnit maxTaskExecTimeUnit) {
            this.maxTaskExecTime = maxTaskExecTime;
            this.maxTaskExecTimeUnit = maxTaskExecTimeUnit;
            return this;
        }

        /** Overrides custom reporter. */
        public Builder checkHandler(TaskCheckHandler checkHandler) {
            this.checkHandler = checkHandler;
            return this;
        }

        /** If checkHandler is not set, a DefaultTaskCheckHandler will be created with this custom reporter */
        public Builder customReporter(CustomReporter customReporter) {
            this.customReporter = customReporter;
            return this;
        }

        public DefaultTaskChecker build() {
            if (taskExecTimeCheckInterval == null) { taskExecTimeCheckInterval = DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL; }
            if (taskExecTimeCheckIntervalUnit == null) { taskExecTimeCheckIntervalUnit = DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL_UNIT; }
            if (checkHandler == null) {
                if (customReporter == null) {
                    checkHandler = new DefaultTaskCheckHandler();
                } else {
                    checkHandler = new DefaultTaskCheckHandler(customReporter);
                }
            }
            return new DefaultTaskChecker(taskExecTimeCheckInterval, taskExecTimeCheckIntervalUnit,
                maxTaskExecTime, maxTaskExecTimeUnit, checkHandler);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    protected final Map<TaskCheckingThread, Boolean> threads;
    // Need to use our own timer - can't use event loop for this
    protected final Timer timer;

    protected DefaultTaskChecker(long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                 @Nullable Long maxTaskExecTime, @Nullable TimeUnit maxTaskExecTimeUnit,
                                 TaskCheckHandler checkHandler) {
        threads = new WeakHashMap<>();

        @Nullable final Long timeLimitNanos;
        if (maxTaskExecTime != null && maxTaskExecTimeUnit != null) {
            timeLimitNanos = TimeUnit.NANOSECONDS.convert(maxTaskExecTime, maxTaskExecTimeUnit);
        } else {
            timeLimitNanos = null;
        }
        final long taskExecTimeCheckIntervalMillis = taskExecTimeCheckIntervalUnit.toMillis(taskExecTimeCheckInterval);

        timer = new Timer("task-exec-time-checker", true);
        timer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    synchronized (DefaultTaskChecker.this) {
                        List<TaskCheckingThread> terminatedTreads = new ArrayList<>();
                        for (TaskCheckingThread thread : threads.keySet()) {
                            Thread.State threadState = thread.getState();

                            if (threadState == Thread.State.TERMINATED) {
                                terminatedTreads.add(thread);
                            } else {
                                //TODO: in the original BlockedThreadChecker startTime isn't volatile. Test that for correctness and speed improvements.
                                long taskExecStartNanos = thread.startTimeNanos;
                                //If a task is running:
                                if (taskExecStartNanos != 0) {
                                    //If the thread is blocked
                                    boolean isThreadBlocked = threadState == Thread.State.BLOCKED
                                        || threadState == Thread.State.WAITING
                                        || threadState == Thread.State.TIMED_WAITING;

                                    //Or if the task runs longer than execution time limit (if set)
                                    long durationNanos = System.nanoTime() - taskExecStartNanos;
                                    boolean isPastMaxExecTime = timeLimitNanos != null && durationNanos >= timeLimitNanos;

                                    //Run handler logic
                                    if (isPastMaxExecTime || isThreadBlocked) {
                                        //TODO: add thread state to reporting
                                        checkHandler.checkTask(thread, durationNanos, timeLimitNanos);
                                    }
                                }
                            }
                        }
                        for (TaskCheckingThread thread : terminatedTreads) {
                            final String message =
                                String.format("Warning: removing terminated thread from the map. Thread [%s] %s",
                                    thread.getState(),
                                    thread);

                            Exception stackTraceOutput = new Exception(message);
                            stackTraceOutput.setStackTrace(thread.getStackTrace());
                            stackTraceOutput.printStackTrace();

                            threads.remove(thread);
                        }
                    }
                }
            },
            taskExecTimeCheckIntervalMillis,
            taskExecTimeCheckIntervalMillis
        );
    }

    @Override
    public synchronized void registerThread(TaskCheckingThread thread) {
        threads.put(thread, true);
    }

    //Called on shutdown and shutdownNow
    @Override
    public void shutdown() {
        timer.cancel();
    }
}