package com.flower.executor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TaskCheckingExecutor extends ScheduledThreadPoolExecutor {
    /** The default number of threads to be used = 2 * number of cores on the machine */
    public static final int DEFAULT_CORE_POOL_SIZE = 2 * AvailableProcessors.availableProcessors();

    protected final TaskChecker checker;

    public static class Builder {
        @Nullable protected Integer corePoolSize = null;
        @Nullable protected RejectedExecutionHandler handler = null;
        @Nullable protected TaskChecker checker = null;
        @Nullable protected TaskCheckHandler checkHandler = null;
        @Nullable protected CustomReporter customReporter = null;

        public Builder corePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder handler(RejectedExecutionHandler handler) {
            this.handler = handler;
            return this;
        }

        /** Overrides check handler and custom reporter. */
        public Builder checker(TaskChecker checker) {
            this.checker = checker;
            return this;
        }

        /** If checker is not set, a DefaultTaskChecker will be created with this check handler. Overrides custom reporter. */
        public Builder checkHandler(TaskCheckHandler checkHandler) {
            this.checkHandler = checkHandler;
            return this;
        }

        /** If checker and checkHandler are not set, a DefaultTaskChecker will be created with DefaultTaskCheckHandler and this custom reporter */
        public Builder customReporter(CustomReporter customReporter) {
            this.customReporter = customReporter;
            return this;
        }

        public TaskCheckingExecutor build() {
            if (corePoolSize == null) { corePoolSize = DEFAULT_CORE_POOL_SIZE; }
            if (checker == null) {
                if (checkHandler == null) {
                    if (customReporter == null) {
                        checker = DefaultTaskChecker.builder().build();
                    } else {
                        checker = DefaultTaskChecker.builder().customReporter(customReporter).build();
                    }
                } else {
                    checker = DefaultTaskChecker.builder().checkHandler(checkHandler).build();
                }
            }

            if (handler != null) {
                return new TaskCheckingExecutor(corePoolSize, handler, checker);
            } else {
                return new TaskCheckingExecutor(corePoolSize, checker);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    protected TaskCheckingExecutor(int corePoolSize, TaskChecker checker) {
        super(corePoolSize, new TaskCheckingThreadFactory());
        this.checker = checker;
    }

    protected TaskCheckingExecutor(int corePoolSize, RejectedExecutionHandler handler, TaskChecker checker) {
        super(corePoolSize, new TaskCheckingThreadFactory(), handler);
        this.checker = checker;
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable r) {
        TaskCheckingThread t = (TaskCheckingThread)(thread);
        t.taskExecuteStart();
        checker.registerTask(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable e) {
        TaskCheckingThread t = (TaskCheckingThread)(Thread.currentThread());
        t.taskExecuteEnd();
    }

    @Override
    public void shutdown() {
        checker.shutdown();
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        checker.shutdown();
        return super.shutdownNow();
    }
}
