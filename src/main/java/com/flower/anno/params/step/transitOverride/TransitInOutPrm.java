package com.flower.anno.params.step.transitOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Override of TransitFunction Input/Output parameter mapping. Mapping of Flow field to a parameter,
 * and back to a Flow field. For use in StepFunction ONLY!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitInOutPrm {
  /** @return TransitFunction parameter name to override */
  String paramName();

  /** @return Parameter mapping - input from Flow field */
  String fromAndTo();
}
