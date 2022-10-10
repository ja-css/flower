package com.flower.conf;

import com.google.common.util.concurrent.ListenableFuture;

public interface FlowExecCallback {
  void flowStarted(FlowId flowId, ListenableFuture flow);

  void flowFinished(FlowId flowId);
}
