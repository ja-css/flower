package com.flower.utilities.messagepassing;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collection;

/**
 * This interface is introduced to bar Message Producer from using Consumer methods
 */
public interface MessageSink<M, R> {
    ListenableFuture<R> add(M message);
    Collection<ListenableFuture<R>> addAll(Collection<? extends M> messages);

    /**
     * Wake up all waiting consumers without sending a message
     */
    void notifyMessageListeners();
}
