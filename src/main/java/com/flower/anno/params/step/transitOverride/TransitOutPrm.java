package com.flower.anno.params.step.transitOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Override of TransitFunction Output parameter mapping. Mapping of output parameter to a Flow
 * field. For use in StepFunction ONLY!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitOutPrm {
  /** @return TransitFunction parameter name to override */
  String paramName();

  /** @return Parameter mapping - output to Flow field */
  String to();
}
