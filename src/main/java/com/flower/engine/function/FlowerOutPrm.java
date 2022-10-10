package com.flower.engine.function;

import com.flower.conf.OutPrm;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Optional;

public class FlowerOutPrm<T> implements OutPrm<T> {
  Optional<T> value = Optional.empty();
  Optional<ListenableFuture<T>> valueFuture = Optional.empty();

  @Override
  public void setOutValue(T value) {
    this.value = Optional.of(value);
    this.valueFuture = Optional.empty();
  }

  @Override
  public void setOutFuture(ListenableFuture<T> valueFuture) {
    this.value = Optional.empty();
    this.valueFuture = Optional.of(valueFuture);
  }

  public Optional<T> getOpt() {
    return value;
  }

  public Optional<ListenableFuture<T>> getOptFuture() {
    return valueFuture;
  }
}
