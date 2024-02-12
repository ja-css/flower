package com.flower.executor;

public interface TaskChecker {
    void registerTask(TaskCheckingThread thread, Runnable task);

    //Called on shutdown and shutdownNow
    void shutdown();
}
