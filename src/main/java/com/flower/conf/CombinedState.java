package com.flower.conf;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Combined snapshot of states that includes Flow State as well as states of all Event Profiles
 */
public interface CombinedState {
    //Flow metadata
    //------------------------------
    String flowName();

    boolean isFinal();
    @Nullable String stepNameToExecute();
    @Nullable String stepPhaseToExecute();
    /** Timestamp from System.currentTimeMillis(); null - immediate execution */
    @Nullable Long executeAtTimestamp();

    /** e.g. Flow-breaking exception in String format */
    @Nullable String error();
    //------------------------------

    //Serialized Flow and its EventProfiles states:
    //------------------------------
    ByteBuffer flowState();
    /** That's Map<{EventProfileName}, {EventProfileState}> */
    Map<String, ByteBuffer> eventProfileStates();
    //------------------------------
}
