package com.flower.executor;

import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutionTimeCheckingExecutor extends ScheduledThreadPoolExecutor {
    /** The default number of threads to be used = 2 * number of cores on the machine */
    public static final int DEFAULT_CORE_POOL_SIZE = 2 * AvailableProcessors.availableProcessors();

    private final TaskExecutionTimeChecker checker;

    public TaskExecutionTimeCheckingExecutor() {
        this(DEFAULT_CORE_POOL_SIZE);
    }

    public TaskExecutionTimeCheckingExecutor(TaskExecutionTimeCheckReporter reporter) {
        this(DEFAULT_CORE_POOL_SIZE, reporter);
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory());
        this.checker = new TaskExecutionTimeChecker();
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize, TaskExecutionTimeCheckReporter reporter) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory());
        this.checker = new TaskExecutionTimeChecker(reporter);
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory(), handler);
        this.checker = new TaskExecutionTimeChecker();
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize, RejectedExecutionHandler handler, TaskExecutionTimeCheckReporter reporter) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory(), handler);
        this.checker = new TaskExecutionTimeChecker(reporter);
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize,
                                             long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                             long maxTaskExecTime, TimeUnit maxTaskExecTimeUnit) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory());
        this.checker = new TaskExecutionTimeChecker(taskExecTimeCheckInterval, taskExecTimeCheckIntervalUnit, maxTaskExecTime, maxTaskExecTimeUnit);
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize,
                                             long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                             long maxTaskExecTime, TimeUnit maxTaskExecTimeUnit, TaskExecutionTimeCheckReporter reporter) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory());
        this.checker = new TaskExecutionTimeChecker(taskExecTimeCheckInterval, taskExecTimeCheckIntervalUnit, maxTaskExecTime, maxTaskExecTimeUnit, reporter);
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize, RejectedExecutionHandler handler,
                                             long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                             long maxTaskExecTime, TimeUnit maxTaskExecTimeUnit) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory(), handler);
        this.checker = new TaskExecutionTimeChecker(taskExecTimeCheckInterval, taskExecTimeCheckIntervalUnit, maxTaskExecTime, maxTaskExecTimeUnit);
    }

    public TaskExecutionTimeCheckingExecutor(int corePoolSize, RejectedExecutionHandler handler,
                                             long taskExecTimeCheckInterval, TimeUnit taskExecTimeCheckIntervalUnit,
                                             long maxTaskExecTime, TimeUnit maxTaskExecTimeUnit,
                                             TaskExecutionTimeCheckReporter reporter) {
        super(corePoolSize, new TaskExecutionTimeCheckingThreadFactory(), handler);
        this.checker = new TaskExecutionTimeChecker(taskExecTimeCheckInterval, taskExecTimeCheckIntervalUnit,
                                                    maxTaskExecTime, maxTaskExecTimeUnit, reporter);
    }


    @Override
    protected void beforeExecute(Thread thread, Runnable r) {
        TaskExecutionTimeCheckingThread t = (TaskExecutionTimeCheckingThread)(thread);
        t.taskExecuteStart();
        checker.registerTask(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable e) {
        TaskExecutionTimeCheckingThread t = (TaskExecutionTimeCheckingThread)(Thread.currentThread());
        t.taskExecuteEnd();
    }

    @Override
    public void shutdown() {
        checker.close();
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        checker.close();
        return super.shutdownNow();
    }
}
