package com.flower.anno.params.step.transitOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Override of TransitFunction StepRef and Terminal parameter mappings. For use in StepFunction
 * ONLY!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitStepRefPrm {
  /** @return TransitFunction parameter name to override */
  String paramName();

  /** @return Referenced Step name */
  String stepName();
}
