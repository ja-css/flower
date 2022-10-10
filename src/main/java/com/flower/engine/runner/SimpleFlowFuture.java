package com.flower.engine.runner;

import com.flower.conf.FlowFuture;
import com.flower.conf.FlowId;
import com.google.common.util.concurrent.ListenableFuture;

public class SimpleFlowFuture<T> implements FlowFuture<T> {
  final FlowId flowId;
  final ListenableFuture<T> future;

  public SimpleFlowFuture(FlowId flowId, ListenableFuture<T> future) {
    this.flowId = flowId;
    this.future = future;
  }

  @Override
  public FlowId getFlowId() {
    return flowId;
  }

  @Override
  public ListenableFuture<T> getFuture() {
    return future;
  }
}
