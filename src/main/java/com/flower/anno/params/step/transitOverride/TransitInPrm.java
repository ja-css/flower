package com.flower.anno.params.step.transitOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Override of TransitFunction Input parameter mapping. Can override In and InRet. Mapping of Flow
 * field to a parameter. For use in StepFunction ONLY!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitInPrm {
  /** @return TransitFunction parameter name to override */
  String paramName();

  /** @return Parameter mapping - input from Flow field */
  String from();

  /**
   * Ensure that the field is not null, similarly to com.google.common.base.Preconditions.checkNotNull
   * Will throw an Exception if the field wasn't initialized.
   *
   * @return true - throw Exception if field is not initialized.
   */
  boolean checkNotNull() default false;
}
