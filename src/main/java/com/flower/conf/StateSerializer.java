package com.flower.conf;

import java.nio.ByteBuffer;

public interface StateSerializer<T> {
    ByteBuffer serialize(T state);
    T deserialize(ByteBuffer serializedState);
}
