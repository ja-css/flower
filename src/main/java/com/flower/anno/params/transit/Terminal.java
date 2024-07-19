package com.flower.anno.params.transit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Terminal flow state - flow execution ends. For use in TransitFunction ONLY!
 *
 * <p>Parameter must be of type conf.Transition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Terminal {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";

  /**
   * Optional: describes under what circumstances and with what goals this transition can happen.
   * This value can be overridden by TransitTerminalOverride.
   *
   * @return Transition desc
   */
  String desc() default "";
}
