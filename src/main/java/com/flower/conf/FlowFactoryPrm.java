package com.flower.conf;

import java.time.Duration;

public interface FlowFactoryPrm<T> extends FlowRepoPrm<T> {
  /**
   * Run a child flow of a current flow. Current flow is determined by flow execution context.
   *
   * <p>Child flow reference should be obtained via @FlowTypeRef parameter of a StepFunction. This
   * setting is required for engine to be able to see the entire possible Parent-Child flow tree.
   * It's important for automated creation of Flow diagrams and enables further analysis, like cycle
   * detection.
   *
   * @param childFlow Child flow instance
   * @return Child Flow instance in a final state
   */
  FlowFuture<T> runChildFlow(T childFlow);

  FlowFuture<T> runChildFlow(T childFlow, Duration delay);
}
