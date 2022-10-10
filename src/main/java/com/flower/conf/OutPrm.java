package com.flower.conf;

import com.google.common.util.concurrent.ListenableFuture;

/** Must be injected by FlowerEngine via @Out annotation. Do NOT implement. */
public interface OutPrm<T> {
  /**
   * Set value to update Flow state with ATTENTION! Setting this value will not change the value
   * returned by getInValue() for @InOut
   *
   * @param value Value to update Flow state with
   */
  void setOutValue(T value);

  /**
   * Set Future for the value to update Flow state with ATTENTION! Setting this Future will not
   * change the value returned by getInValue() for @InOut
   *
   * @param value Future for the value to update Flow state with
   */
  void setOutFuture(ListenableFuture<T> value);
}
