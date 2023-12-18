package com.flower.conf;

import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;

public interface FlowRepoPrm {
  @Nullable
  ListenableFuture<?> getFlowFuture(FlowId flowId);
}
