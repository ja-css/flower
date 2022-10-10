package com.flower.conf;

import java.time.Duration;

public interface InternalFlowExec<T> extends FlowExec<T> {
  FlowFuture<T> runChildFlow(FlowId parentFlowId, T flow);

  FlowFuture<T> runChildFlow(FlowId parentFlowId, T flow, Duration startupDelay);
}
