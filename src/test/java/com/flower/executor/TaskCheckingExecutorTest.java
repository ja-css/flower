package com.flower.executor;

import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class TaskCheckingExecutorTest {
    @Test
    public void testBlockedThreadReporting() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        TaskCheckHandler checkHandler = new DefaultTaskCheckHandler() {
            @Override
            public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskCheckingExecutor executor = TaskCheckingExecutor.builder().checkHandler(checkHandler).build();
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

        // Cast to ThreadPoolExecutor to access its methods
        int numThreads = executor.getPoolSize();
        System.out.println("Number of threads in the executor: " + numThreads);

        //First report at 1 sec, 9 reports over 9.5 seconds
        assertEquals(9, reportCount.get());
    }

    @Test
    public void testActiveWaitNoTimeLimitNoReports() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        //Since there will be no reports, handler will never get called
        TaskCheckHandler checkHandler = new DefaultTaskCheckHandler() {
            @Override
            public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskCheckingExecutor executor = TaskCheckingExecutor.builder().checkHandler(checkHandler).build();

        executor.execute(() -> {
            System.out.println("Hello");
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time + 9500) {
            }
            System.out.println("Done");
        });

        Thread.sleep(10000);
        executor.shutdown();

        // Cast to ThreadPoolExecutor to access its methods
        int numThreads = executor.getPoolSize();
        System.out.println("Number of threads in the executor: " + numThreads);

        assertEquals(0, reportCount.get());
    }

    @Test
    public void testActiveWaitOverTimeLimitReporting() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        AtomicInteger customReportCount = new AtomicInteger(0);

        TaskCheckHandler checkHandler = new DefaultTaskCheckHandler(e -> customReportCount.incrementAndGet()) {
            @Override
            public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskCheckingExecutor executor = TaskCheckingExecutor.builder()
            .checker(DefaultTaskChecker.builder()
                .maxTaskExecTime(1200L, TimeUnit.MILLISECONDS)
                .checkHandler(checkHandler)
                .build())
            .build();

        executor.execute(() -> {
            System.out.println("Hello");
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time + 9500) {
            }
            System.out.println("Done");
        });

        Thread.sleep(10000);
        executor.shutdown();

        // Cast to ThreadPoolExecutor to access its methods
        int numThreads = executor.getPoolSize();
        System.out.println("Number of threads in the executor: " + numThreads);

        //First report at 2 sec, 8 reports over 8.5 seconds
        assertEquals(8, reportCount.get());
        assertEquals(8, customReportCount.get());
    }
}
