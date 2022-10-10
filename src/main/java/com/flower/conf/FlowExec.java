package com.flower.conf;

import java.time.Duration;

public interface FlowExec<T> {
  /**
   * Run a flow.
   *
   * @param flow Flow instance
   * @return Flow instance in a final state
   */
  FlowFuture<T> runFlow(T flow);

  FlowFuture<T> runFlow(T flow, Duration startupDelay);

  String buildMermaidGraph();

  Class<T> getFlowType();
}
