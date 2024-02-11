package com.flower.executor;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskExecutionTimeChecker {
    private static final Logger LOGGER = Logger.getGlobal();

    /** The default value of report exception time 5000000000 ns (5 seconds)
     If a task is executing for longer than this threshold, the warning log will output and exception with a stack trace */
    public static final long DEFAULT_REPORT_EXCEPTION_TIME = TimeUnit.SECONDS.toNanos(5);
    /** The default value of report exception time unit = {@link TimeUnit#NANOSECONDS} */
    public static final TimeUnit DEFAULT_REPORT_EXCEPTION_TIME_UNIT = TimeUnit.NANOSECONDS;
    /** The default value of task execution check interval = 1000 ms. */
    public static final long DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(1);;
    /** The default value of task execution check interval unit = {@link TimeUnit#MILLISECONDS} */
    public static final TimeUnit DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL_UNIT = TimeUnit.MILLISECONDS;
    /** The default value of max task execute time = 2000000000 ns (2 seconds) */
    public static final long DEFAULT_MAX_TASK_EXECUTE_TIME = TimeUnit.SECONDS.toNanos(2);
    /** The default value of max task execute time unit = {@link TimeUnit#NANOSECONDS} */
    public static final TimeUnit DEFAULT_MAX_TASK_EXECUTE_TIME_UNIT = TimeUnit.NANOSECONDS;

    /** Default reporter mimicking Vertx reporting behavior */
    public static class DefaultCheckReporter implements TaskExecutionTimeCheckReporter {
        final long reportExceptionTime;
        final TimeUnit reportExceptionTimeUnit;

        public DefaultCheckReporter() {
            this(DEFAULT_REPORT_EXCEPTION_TIME, DEFAULT_REPORT_EXCEPTION_TIME_UNIT);
        }

        public DefaultCheckReporter(long reportExceptionTime, TimeUnit reportExceptionTimeUnit) {
            this.reportExceptionTime = reportExceptionTime;
            this.reportExceptionTimeUnit = reportExceptionTimeUnit;
        }

        public void report(Thread thread, Runnable task, long taskDurationNanos, long taskTimeLimitNanos) {
            final String message =
                String.format("Task has been executing for %d ms, time limit is %d ms. Thread %s; task %s",
                    (taskDurationNanos / 1_000_000), (taskTimeLimitNanos / 1_000_000), thread, task);
            if (reportExceptionTimeUnit.convert(taskDurationNanos, TimeUnit.NANOSECONDS) <= reportExceptionTime) {
                LOGGER.log(Level.WARNING, message);
            } else {
                Exception stackTraceOutput = new Exception(message);
                stackTraceOutput.setStackTrace(thread.getStackTrace());
                LOGGER.log(Level.WARNING, "", stackTraceOutput);
            }
        }
    }

    private final Map<TaskExecutionTimeCheckingThread, Runnable> tasks = new WeakHashMap<>();
    // Need to use our own timer - can't use event loop for this
    private final Timer timer;

    public TaskExecutionTimeChecker() {
        this(DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL, DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL_UNIT,
            DEFAULT_MAX_TASK_EXECUTE_TIME, DEFAULT_MAX_TASK_EXECUTE_TIME_UNIT);
    }

    public TaskExecutionTimeChecker(TaskExecutionTimeCheckReporter reporter) {
        this(DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL, DEFAULT_TASK_EXECUTION_TIME_CHECK_INTERVAL_UNIT,
            DEFAULT_MAX_TASK_EXECUTE_TIME, DEFAULT_MAX_TASK_EXECUTE_TIME_UNIT, reporter);

    }

    public TaskExecutionTimeChecker(long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                    long maxTaskExecTime, TimeUnit maxTaskExecTimeUnit) {
        this(taskExecTimeCheckInterval, taskExecTimeCheckIntervalUnit, maxTaskExecTime, maxTaskExecTimeUnit,
            new DefaultCheckReporter());
    }

    public TaskExecutionTimeChecker(long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                    long maxTaskExecTime, TimeUnit maxTaskExecTimeUnit,
                                    TaskExecutionTimeCheckReporter reporter) {
        timer = new Timer("task-exec-time-checker", true);
        final long timeLimitNanos = TimeUnit.NANOSECONDS.convert(maxTaskExecTime, maxTaskExecTimeUnit);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (TaskExecutionTimeChecker.this) {
                    long now = System.nanoTime();
                    for (Map.Entry<TaskExecutionTimeCheckingThread, Runnable> entry : tasks.entrySet()) {
                        //TODO: should startTime be volatile? Original BlockedThreadChecker impl doesn't seem to care.
                        long taskExecStart = entry.getKey().startTime;
                        long duration = now - taskExecStart;
                        long taskExecutionTime = maxTaskExecTimeUnit.convert(duration, TimeUnit.NANOSECONDS);
                        if (taskExecStart != 0 && taskExecutionTime >= maxTaskExecTime) {
                            reporter.report(entry.getKey(), entry.getValue(), duration, timeLimitNanos);
                        }
                    }
                }
            }
        }, taskExecTimeCheckIntervalUnit.toMillis(taskExecTimeCheckInterval), taskExecTimeCheckIntervalUnit.toMillis(taskExecTimeCheckInterval));
    }

    public synchronized void registerTask(TaskExecutionTimeCheckingThread thread, Runnable task) {
        tasks.put(thread, task);
    }

    //Called on shutdown and shutdownNow
    public void close() {
        timer.cancel();
    }
}