package com.flower.conf;

import com.google.common.util.concurrent.ListenableFuture;

public interface FlowFuture<T> {
  FlowId getFlowId();

  ListenableFuture<T> getFuture();
}
