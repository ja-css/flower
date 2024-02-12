package com.flower.executor;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultTaskChecker implements TaskChecker {
    /** The default value of task execution check interval = 1000 ms. */
    public static final long DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(1);;
    /** The default value of task execution check interval unit = {@link TimeUnit#MILLISECONDS} */
    public static final TimeUnit DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL_UNIT = TimeUnit.MILLISECONDS;

    public static class Builder {
        @Nullable protected Long taskExecTimeCheckInterval = null;
        @Nullable protected TimeUnit taskExecTimeCheckIntervalUnit = null;
        @Nullable protected Long maxTaskExecTime = null;
        @Nullable protected TimeUnit maxTaskExecTimeUnit = null;
        @Nullable protected TaskCheckHandler checkHandler = null;
        @Nullable protected Boolean useWeakMap = null;
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

        public Builder useWeakMap(Boolean useWeakMap) {
            this.useWeakMap = useWeakMap;
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
            if (useWeakMap == null) { useWeakMap = true; }

            return new DefaultTaskChecker(taskExecTimeCheckInterval, taskExecTimeCheckIntervalUnit,
                maxTaskExecTime, maxTaskExecTimeUnit, checkHandler, useWeakMap);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    protected final boolean useWeakMap;
    protected final Map<TaskCheckingThread, Runnable> threads;
    // Need to use our own timer - can't use event loop for this
    protected final Timer timer;

    protected DefaultTaskChecker(long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                 @Nullable Long maxTaskExecTime, @Nullable TimeUnit maxTaskExecTimeUnit,
                                 TaskCheckHandler checkHandler, boolean useWeakMap) {
        this.useWeakMap = useWeakMap;
        if (useWeakMap) {
            threads = new WeakHashMap<>();
        } else {
            threads = new ConcurrentHashMap<>();
        }

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
                        List<Pair<TaskCheckingThread, Runnable>> terminatedTreads = new ArrayList<>();
                        for (Map.Entry<TaskCheckingThread, Runnable> entry : threads.entrySet()) {
                            TaskCheckingThread thread = entry.getKey();
                            Runnable task = entry.getValue();
                            Thread.State threadState = thread.getState();

                            if (threadState == Thread.State.TERMINATED) {
                                terminatedTreads.add(Pair.of(thread, task));
                            } else {
                                //TODO: should startTime be volatile? Original BlockedThreadChecker impl doesn't seem to care.
                                long taskExecStartNanos = thread.startTimeNanos();
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
                                        checkHandler.checkTask(thread, task, durationNanos, timeLimitNanos);
                                    }
                                }
                            }
                        }
                        for (Pair<TaskCheckingThread, Runnable> threadPair : terminatedTreads) {
                            TaskCheckingThread thread = threadPair.getKey();
                            Runnable task = threadPair.getValue();
                            if (useWeakMap) {
                                final String message =
                                    String.format("Warning: removing terminated thread form WeakHashMap. Thread [%s] %s; task %s",
                                        thread.getState(),
                                        thread,
                                        task);

                                Exception stackTraceOutput = new Exception(message);
                                stackTraceOutput.setStackTrace(thread.getStackTrace());
                                stackTraceOutput.printStackTrace();
                            }
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
    public synchronized void registerTask(TaskCheckingThread thread, Runnable task) {
        threads.put(thread, task);
    }

    //Called on shutdown and shutdownNow
    @Override
    public void shutdown() {
        timer.cancel();
    }
}