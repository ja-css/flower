package com.flower.engine.function;

import com.flower.conf.ReturnValueOrException;
import java.util.Optional;
import javax.annotation.Nullable;

public class FlowerRetValOrException<T> implements ReturnValueOrException<T> {
  private final Optional<T> returnValue;
  private final Optional<Throwable> exception;

  public FlowerRetValOrException(@Nullable T returnValue) {
    this.returnValue = Optional.ofNullable(returnValue);
    this.exception = Optional.empty();
  }

  public FlowerRetValOrException(@Nullable Throwable exception) {
    this.exception = Optional.ofNullable(exception);
    this.returnValue = Optional.empty();
  }

  @Override
  public Optional<T> returnValue() {
    return returnValue;
  }

  @Override
  public Optional<Throwable> exception() {
    return exception;
  }
}
