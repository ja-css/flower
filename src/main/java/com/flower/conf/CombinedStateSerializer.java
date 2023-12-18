package com.flower.conf;

import java.nio.ByteBuffer;

/**
 * This interface also needs to support combined flow/event profile state serialization in EventHandlers.
 */
public interface CombinedStateSerializer<T> extends StateSerializer<T>{
    /**
     * Get a combined snapshot of states that includes Flow State as well as states of all Event Profiles
     */
    CombinedState getCombinedState();

    /**
     * Convenience method to serialize combined state into a single BLOB
     */
    ByteBuffer serializeCombinedState(CombinedState combinedState);
}
