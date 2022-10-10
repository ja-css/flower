package com.flower.conf;

/** Engine that runs Flows. */
public interface FlowerEngine {
  /**
   * Run a flow.
   *
   * @param flowType Flow type
   * @param <T> Type of Flow
   * @return Flow instance in a final state
   */
  <T> FlowExec<T> getFlowExec(Class<T> flowType);
}
