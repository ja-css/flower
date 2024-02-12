package com.flower.executor;

import javax.annotation.Nullable;

public interface TaskCheckHandler {
    void checkTask(TaskCheckingThread thread, Runnable task, long taskDurationNanos,
                   @Nullable Long taskTimeLimitNanos);
}