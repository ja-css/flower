package com.flower.anno.params.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Transition Info Parameter. For use in AFTER_TRANSIT, AFTER_STEP_ITERATION, AFTER_STEP, AFTER_FLOW
 * Event Handlers.
 *
 * <p>Represents information about Transition returned by a Transitioner.
 *
 * <p>Parameter must be of type Transition.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface TransitionInfo {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";
}
