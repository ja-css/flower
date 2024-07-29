package com.flower.utilities.messagepassing;


import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Provides a basic queue structure to be used for message passing.
 *
 * @param <M> Message
 */
public class SimpleMessageQueue<M> extends AbstractMessageQueue<M> {
    public void add(M message) {
        innerAdd(message);
    }

    public void addAll(Collection<? extends M> messages) {
        innerAddAll(messages);
    }

    @Nullable
    public M poll() {
        return innerPoll();
    }

    @Nullable
    public M peek() {
        return innerPeek();
    }
}
