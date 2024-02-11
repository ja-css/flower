package com.flower.executor;

public interface TaskExecutionTimeCheckReporter {
    void report(Thread thread, Runnable task, long taskDurationNanos, long taskTimeLimitNanos);
}