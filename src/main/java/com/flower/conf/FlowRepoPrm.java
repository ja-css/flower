package com.flower.conf;

import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;

/**
 * In the end I decided to make it parameterized with FlowType, because it appears useful to have mermaid diagram reflect
 * which Flow Types' states you're trying to sync on, similarly to how it's important to know which ChildFlow Types
 * you're spawning with FlowFactory.
 */
public interface FlowRepoPrm<T> {
  //TODO: return FlowFuture instead?
  @Nullable
  ListenableFuture<T> getFlowFuture(FlowId flowId);
}
