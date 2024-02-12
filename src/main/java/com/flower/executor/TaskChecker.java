package com.flower.executor;

public interface TaskChecker {
    void registerThread(TaskCheckingThread thread);

    //Called on shutdown and shutdownNow
    void shutdown();
}
