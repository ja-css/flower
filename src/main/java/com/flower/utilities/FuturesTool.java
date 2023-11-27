package com.flower.utilities;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.atomic.AtomicBoolean;

//TODO: test coverage
public class FuturesTool {
    public static <V> ListenableFuture<V> whenAnyComplete(Iterable<? extends ListenableFuture<? extends V>> futures) {
        SettableFuture<V> retFuture = SettableFuture.create();
        AtomicBoolean doneFlag = new AtomicBoolean(false);
        for (ListenableFuture<? extends V> future : futures) {
            ListenableFuture<V> ignored = Futures.catching(
                Futures.transform(future,
                    v -> {
                        if (false == doneFlag.compareAndExchange(false, true)) {
                            retFuture.set(v);
                        }
                        return null;
                    },
                    MoreExecutors.directExecutor()),
                Throwable.class,
                t -> {
                    if (false == doneFlag.compareAndExchange(false, true)) {
                        retFuture.setException(t);
                    }
                    return null;
                },
                MoreExecutors.directExecutor()
            );
        }

        return retFuture;
    }

    public static ListenableFuture<Void> whenAnyCompleteIgnoreResult(Iterable<? extends ListenableFuture<?>> futures) {
        SettableFuture<Void> retFuture = SettableFuture.create();
        AtomicBoolean doneFlag = new AtomicBoolean(false);
        for (ListenableFuture<?> future : futures) {
            ListenableFuture<Void> ignored = Futures.catching(
                Futures.transform(future,
                    v -> {
                        if (false == doneFlag.compareAndExchange(false, true)) {
                            retFuture.set(null);
                        }
                        return null;
                    },
                    MoreExecutors.directExecutor()),
                Throwable.class,
                t -> {
                    if (false == doneFlag.compareAndExchange(false, true)) {
                        retFuture.set(null);
                    }
                    return null;
                },
                MoreExecutors.directExecutor()
            );
        }

        return retFuture;
    }
}
