package com.flower.utilities.messagepassing;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public interface MessageSource<M, R> {
    /**
     * Returns a pair of message and SettableFuture to the Flow.
     * We expect the Flow to set result value to the Future to send back notification about message processing.
     */
    @Nullable Pair<M, SettableFuture<R>> poll();

    @Nullable M peek();

    ListenableFuture<Void> getMessageListener();
}
