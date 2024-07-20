package com.flower.anno.params.transit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Step reference parameter. For use in TransitFunction ONLY!
 *
 * <p>Represents a reference to an existing Step. Can be overridden by TransitStepRefOverride or
 * TransitTerminalOverride on StepFunction.
 *
 * <p>Parameter must be of type conf.Transition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface StepRef {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";

  /**
   * Referenced Step name. This value can be overridden by TransitStepRefOverride, and for that
   * reason isn't mandatory on TransitFunction.
   *
   * @return Referenced Step name
   */
  String stepName() default "";

  /**
   * Optional: describes under what circumstances and with what goals this transition can happen.
   * This value can be overridden by TransitStepRefOverride.
   *
   * @return Transition desc
   */
  String note() default "";
}
