package com.flower.engine.function;

import com.flower.conf.InOutPrm;

public class FlowerInOutPrm<T> extends FlowerOutPrm<T> implements InOutPrm<T> {
  T value;

  public FlowerInOutPrm(T value) {
    this.value = value;
  }

  @Override
  public T getInValue() {
    return value;
  }
}
