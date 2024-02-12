package com.flower.executor;

import java.util.concurrent.atomic.AtomicLong;

public class TaskCheckingThread extends Thread {
    protected final AtomicLong startTimeNanos = new AtomicLong(0);

    public TaskCheckingThread() {
    }

    public TaskCheckingThread(Runnable task) {
        super(task);
    }

    public TaskCheckingThread(ThreadGroup group, Runnable task) {
        super(group, task);
    }

    public TaskCheckingThread(String name) {
        super(name);
    }

    public TaskCheckingThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public TaskCheckingThread(Runnable task, String name) {
        super(task, name);
    }

    public TaskCheckingThread(ThreadGroup group, Runnable task, String name) {
        super(group, task, name);
    }

    public TaskCheckingThread(ThreadGroup group, Runnable task, String name, long stackSize) {
        super(group, task, name, stackSize);
    }

    public TaskCheckingThread(ThreadGroup group, Runnable task, String name, long stackSize, boolean inheritInheritableThreadLocals) {
        super(group, task, name, stackSize, inheritInheritableThreadLocals);
    }

    protected void taskExecuteStart() { startTimeNanos.set(System.nanoTime()); }

    protected void taskExecuteEnd() { startTimeNanos.set(0); }

    protected long startTimeNanos() { return startTimeNanos.get(); }
}
