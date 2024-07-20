package com.flower.anno.params.step.transitOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Override of TransitFunction Terminal and StepRef flow step parameter mappings. For use in
 * StepFunction ONLY!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitTerminalPrm {
  /** @return TransitFunction parameter name to override */
  String paramName();

  /**
   * Optional: describes under what circumstances and with what goals this transition can happen.
   *
   * @return Transition desc
   */
  String note() default "";
}
