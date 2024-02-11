package com.flower.executor;

public class TaskExecutionTimeCheckingThread extends Thread {
    public long startTime = 0;

    public TaskExecutionTimeCheckingThread() {
    }

    public TaskExecutionTimeCheckingThread(Runnable task) {
        super(task);
    }

    public TaskExecutionTimeCheckingThread(ThreadGroup group, Runnable task) {
        super(group, task);
    }

    public TaskExecutionTimeCheckingThread(String name) {
        super(name);
    }

    public TaskExecutionTimeCheckingThread(ThreadGroup group, String name) {
        super(group, name);
    }

    public TaskExecutionTimeCheckingThread(Runnable task, String name) {
        super(task, name);
    }

    public TaskExecutionTimeCheckingThread(ThreadGroup group, Runnable task, String name) {
        super(group, task, name);
    }

    public TaskExecutionTimeCheckingThread(ThreadGroup group, Runnable task, String name, long stackSize) {
        super(group, task, name, stackSize);
    }

    public TaskExecutionTimeCheckingThread(ThreadGroup group, Runnable task, String name, long stackSize, boolean inheritInheritableThreadLocals) {
        super(group, task, name, stackSize, inheritInheritableThreadLocals);
    }

    void taskExecuteStart() {
        startTime = System.nanoTime();
    }

    void taskExecuteEnd() {
        startTime = 0;
    }

    public long startTime() {
        return startTime;
    }
}
