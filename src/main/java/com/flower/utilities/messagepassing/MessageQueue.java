package com.flower.utilities.messagepassing;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Provides a basic queue structure to be used for message passing.
 * Gives the party that adds a message a ListenableFuture connected to message processing result.
 *
 * @param <M> Message
 * @param <R> Processing result
 */
public class MessageQueue<M, R> extends AbstractMessageQueue<Pair<M, SettableFuture<R>>> {
    protected ListenableFuture<R> innerAddWithoutNotify(M message) {
        SettableFuture<R> future = SettableFuture.create();
        innerQueue.add(Pair.of(message, future));
        return future;
    }

    public ListenableFuture<R> add(M message) {
        ListenableFuture<R> resultFuture = innerAddWithoutNotify(message);
        notifyMessageListeners(false);
        return resultFuture;
    }

    public Collection<ListenableFuture<R>> addAll(Collection<? extends M> messages) {
        Collection<ListenableFuture<R>> resultFutures = messages.stream().map(
            this::innerAddWithoutNotify
        ).toList();
        notifyMessageListeners(false);
        return resultFutures;
    }

    /**
     * Returns a pair of message and SettableFuture to the Flow.
     * We expect the Flow to set result value to the Future to send back notification about message processing.
     */
    @Nullable
    public Pair<M, SettableFuture<R>> poll() {
        return innerPoll();
    }

    @Nullable
    public M peek() {
        Pair<M, SettableFuture<R>> pair = innerPeek();
        return pair == null ? null : pair.getLeft();
    }
}
