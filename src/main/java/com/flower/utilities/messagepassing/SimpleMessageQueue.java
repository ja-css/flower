package com.flower.utilities.messagepassing;


import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Provides a basic queue structure to be used for message passing.
 *
 * @param <M> Message
 */
public class SimpleMessageQueue<M> extends AbstractMessageQueue<M> implements SimpleMessageSink<M>, SimpleMessageSource<M> {
    @Override
    public void add(M message) {
        innerAdd(message);
    }

    @Override
    public void addAll(Collection<? extends M> messages) {
        innerAddAll(messages);
    }

    @Override
    @Nullable public M poll() {
        return innerPoll();
    }

    @Override
    @Nullable public M peek() {
        return innerPeek();
    }
}
