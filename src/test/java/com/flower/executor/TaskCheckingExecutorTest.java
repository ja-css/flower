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
        TaskCheckHandler checkHandler = new KillerTaskCheckHandler() {
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

    @Test
    public void testBlockedThreadKiller() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        TaskCheckHandler checkHandler = new KillerTaskCheckHandler() {
            @Override
            public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskCheckingExecutor executor = TaskCheckingExecutor.builder()
            .checker(DefaultTaskChecker.builder()
                .checkHandler(checkHandler)
                .build())
            .build();
        executor.execute(() -> {
            try {
                System.out.println("Hello");
                try {
                    Thread.sleep(9500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Done");
            } catch (ThreadDeath t) {
                System.out.println("Killed");
                t.printStackTrace();
                throw new RuntimeException(t);
            }
        });

        Thread.sleep(3000);
        executor.shutdown();

        // Cast to ThreadPoolExecutor to access its methods
        int numThreads = executor.getPoolSize();
        System.out.println("Number of threads in the executor: " + numThreads);

        //A single report, at which point the task got killed
        assertEquals(1, reportCount.get());
    }

    @Test
    public void testManyBlockedThreadsKiller() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        AtomicInteger killCount = new AtomicInteger(0);
        TaskCheckHandler checkHandler = new KillerTaskCheckHandler() {
            @Override
            public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskCheckingExecutor executor = TaskCheckingExecutor.builder()
            .corePoolSize(50)
            .checker(DefaultTaskChecker.builder()
                .checkHandler(checkHandler)
                .build())
            .build();
        for (int i = 0; i < 555; i++) {
            System.out.println("Starting " + i);
            int ii = i;
            executor.execute(() -> {
                try {
                    System.out.println("Hello " + ii);
                    try {
                        Thread.sleep(9500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Done " + ii);
                } catch (ThreadDeath t) {
                    System.out.println("Killed " + ii);
                    t.printStackTrace();
                    killCount.incrementAndGet();
                }
            });
        }

        Thread.sleep(13000);
        executor.shutdown();

        // Cast to ThreadPoolExecutor to access its methods
        int numThreads = executor.getPoolSize();
        System.out.println("Number of threads in the executor: " + numThreads);

        //1 report and 1 kill per task
        assertEquals(555, reportCount.get());
        assertEquals(555, killCount.get());
    }

    @Test
    public void testActiveWaitOverTimeLimitKiller() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        TaskCheckHandler checkHandler = new KillerTaskCheckHandler() {
            @Override
            public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskCheckingExecutor executor = TaskCheckingExecutor.builder()
            .checker(DefaultTaskChecker.builder()
                .maxTaskExecTime(1L, TimeUnit.SECONDS)
                .checkHandler(checkHandler)
                .build())
            .build();

        executor.execute(() -> {
            try {
                System.out.println("Hello");
                long time = System.currentTimeMillis();
                while (System.currentTimeMillis() < time + 9500) {
                }
                System.out.println("Done");
            } catch (ThreadDeath t) {
                System.out.println("Killed");
                t.printStackTrace();
            }
        });

        Thread.sleep(3000);
        executor.shutdown();

        // Cast to ThreadPoolExecutor to access its methods
        int numThreads = executor.getPoolSize();
        System.out.println("Number of threads in the executor: " + numThreads);

        //A single report, at which point the task got killed
        assertEquals(1, reportCount.get());
    }

    /** flaky test */
    @Test
    public void testManyActiveWaitsOverTimeLimitKiller() throws InterruptedException {
        AtomicInteger reportCount = new AtomicInteger(0);
        AtomicInteger killCount = new AtomicInteger(0);
        TaskCheckHandler checkHandler = new KillerTaskCheckHandler() {
            @Override
            public void checkTask(TaskCheckingThread thread, long taskDurationNanos, @Nullable Long taskTimeLimitNanos) {
                reportCount.incrementAndGet();
                super.checkTask(thread, taskDurationNanos, taskTimeLimitNanos);
            }
        };

        TaskCheckingExecutor executor = TaskCheckingExecutor.builder()
            .corePoolSize(50)
            .checker(DefaultTaskChecker.builder()
                .maxTaskExecTime(1L, TimeUnit.SECONDS)
                .checkHandler(checkHandler)
                .build())
            .build();

        for (int i = 0; i < 555; i++) {
            System.out.println("Starting " + i);
            int ii = i;
            executor.execute(() -> {
                try {
                    System.out.println("Hello " + ii);
                    long time = System.currentTimeMillis();
                    while (System.currentTimeMillis() < time + 9500) {
                    }
                    System.out.println("Done " + ii);
                } catch (ThreadDeath t) {
                    System.out.println("Killed " + ii);
                    t.printStackTrace();
                    killCount.incrementAndGet();
                }
            });
        }

        Thread.sleep(35000);

        // Cast to ThreadPoolExecutor to access its methods
        int numThreads = executor.getPoolSize();
        System.out.println("Number of threads in the executor: " + numThreads);

        executor.shutdown();

        //1 report and 1 kill per task
        assertEquals(555, reportCount.get());

        //This doesn't always work with this test
        assertEquals(555, killCount.get());
    }
}
