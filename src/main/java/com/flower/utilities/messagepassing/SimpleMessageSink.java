package com.flower.utilities.messagepassing;

import java.util.Collection;

/**
 * This interface is introduced to bar Message Producer from using Consumer methods
 */
public interface SimpleMessageSink<M> {
    void add(M message);
    void addAll(Collection<? extends M> messages);

    /**
     * Wake up all waiting consumers without sending a message
     */
    void notifyMessageListeners();
}
