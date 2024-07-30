package com.flower.utilities.messagepassing;

import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.Nullable;

public interface SimpleMessageSource<M> {
    @Nullable M poll();

    @Nullable M peek();

    ListenableFuture<Void> getMessageListener();
}
