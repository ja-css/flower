package com.flower.conf;

/** Must be injected by FlowerEngine via @InOut annotation. Do NOT implement. */
public interface InOutPrm<T> extends NullableInOutPrm<T> {
  /**
   * Get value from Flow State ATTENTION! This is NOT the value set by setOutValue / setOutFuture
   *
   * @return value from Flow State
   */
  T getInValue();
}
