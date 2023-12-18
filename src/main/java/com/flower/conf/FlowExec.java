package com.flower.conf;

import javax.annotation.Nullable;
import java.time.Duration;

/**
 * This interface is user-facing, and we don't allow explixitly set FlowId or start child flows.
 *
 * For that reason the following methods are extracted to InternalFlowExec sub-interface.
 * FlowFuture<T> runChildFlow(FlowId parentFlowId, T flow);
 * FlowFuture<T> runChildFlow(FlowId parentFlowId, T flow, Duration startupDelay);
 *
 * InternalFlowExec
 * @param <T>
 */
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

  @Nullable StateSerializer<T> getStateSerializer();
}
