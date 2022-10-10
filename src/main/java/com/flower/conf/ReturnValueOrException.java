package com.flower.conf;

import java.util.Optional;

/**
 * Please test that an Exception has occurred by checking exception().isPresent(). returnValue() can
 * be empty in cases when StepFunction returned null or void, and when StepFunction raised an
 * Exception, so it doesn't indicate by its being empty whether the Exception has actually happened
 * or not.
 *
 * @param <T> return type of StepFunction
 */
public interface ReturnValueOrException<T> {
  /**
   * Please note that (returnValue().isPresent() == false) doesn't indicate whether an exception in
   * StepFunction has occurred or not. returnValue() can be empty in cases when StepFunction
   * returned null or void. Instead, use exception().isPresent() to determine that an Exception was
   * raised.
   *
   * @return return value, if any received.
   */
  Optional<T> returnValue();

  /**
   * Use exception().isPresent() to determine if the Exception has occurred in StepFunction.
   *
   * @return Exception, if it was raised.
   */
  Optional<Throwable> exception();
}
