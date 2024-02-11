package com.flower.executor;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertTrue;

public class TaskExecutionTimeCheckingExecutorTest {
    @Test
    public void testThreadSleep() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        TaskExecutionTimeCheckReporter reporter = new TaskExecutionTimeChecker.DefaultCheckReporter() {
            public void report(Thread thread, Runnable task, long taskDurationNanos, long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.report(thread, task, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskExecutionTimeCheckingExecutor executor = new TaskExecutionTimeCheckingExecutor(reporter);
        executor.execute(() -> {
            System.out.println("Hello");
            try {
                Thread.sleep(9500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Done");
        });

        Thread.sleep(10000);
        executor.shutdown();

        //Ideally is == 8, but it's a bit slow at init time
        assertTrue(reportCount.get() >= 6);
    }

    @Test
    public void testActiveWait() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        TaskExecutionTimeCheckReporter reporter = new TaskExecutionTimeChecker.DefaultCheckReporter() {
            public void report(Thread thread, Runnable task, long taskDurationNanos, long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.report(thread, task, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskExecutionTimeCheckingExecutor executor = new TaskExecutionTimeCheckingExecutor(reporter);
        executor.execute(() -> {
            System.out.println("Hello");
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time + 9500) {}
            System.out.println("Done");
        });

        Thread.sleep(10000);
        executor.shutdown();

        //Ideally is == 8, but it's a bit slow at init time
        assertTrue(reportCount.get() >= 6);
    }
}
