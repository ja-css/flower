package com.flower.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** Mostly copy-pasted from DefaultThreadFactory */
public class TaskCheckingThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    TaskCheckingThreadFactory() {
        //TODO: update to conform with future java versions?
        @SuppressWarnings("removal")
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
            Thread.currentThread().getThreadGroup();
        namePrefix = "task-exec-time-checking-pool-" +
            poolNumber.getAndIncrement() +
            "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        TaskCheckingThread t = new TaskCheckingThread(group, r,
            namePrefix + threadNumber.getAndIncrement(),
            0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}