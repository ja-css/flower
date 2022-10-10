package com.flower.anno.params.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Step Info parameter. For use in BEFORE_STEP, AFTER_STEP, BEFORE_STEP_ITERATION,
 * AFTER_STEP_ITERATION, BEFORE_EXEC, AFTER_EXEC, BEFORE_TRANSIT, AFTER_TRANSIT Event Handlers.
 *
 * <p>Represents information about Running Step.
 *
 * <p>Parameter must be of type StepInfoPrm.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface StepInfo {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";
}
