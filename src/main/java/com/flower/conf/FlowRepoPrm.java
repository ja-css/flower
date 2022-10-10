package com.flower.conf;

import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;

public interface FlowRepoPrm {
  String serializeFlowId(FlowId flowId);

  FlowId deserializeFlowId(String flowIdToken);

  @Nullable
  ListenableFuture<?> getFlowFuture(FlowId flowId);
}
