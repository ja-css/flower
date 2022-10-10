package com.flower.anno.params.step.transitOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Override of TransitFunction Step function return value parameter mapping. Can override In and
 * InRet. Mapping of Step function return value to a parameter. For use in StepFunction ONLY!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitInRetPrm {
  /** @return TransitFunction parameter name to override */
  String paramName();
}
