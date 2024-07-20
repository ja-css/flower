package com.flower.conf;

import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;

//TODO: make FlowRepo generic to be able to understand which flow type we're querying on diagram?
public interface FlowRepoPrm {
  @Nullable
  ListenableFuture<?> getFlowFuture(FlowId flowId);
}
