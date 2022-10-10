package com.flower.conf;

import javax.annotation.Nullable;

/** Must be injected by FlowerEngine via @InOut annotation. Do NOT implement. */
public interface NullableInOutPrm<T> extends OutPrm<T> {
  /**
   * Get value from Flow State ATTENTION! This is NOT the value set by setOutValue / setOutFuture
   *
   * @return value from Flow State
   */
  @Nullable
  T getInValue();
}
