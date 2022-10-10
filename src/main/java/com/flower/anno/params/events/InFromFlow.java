package com.flower.anno.params.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Input function parameter for Event function. Mapping of Flow field to a parameter. MUST
 * be @Nullable (in case Flow doesn't have a corresponding field).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface InFromFlow {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";

  /**
   * Parameter mapping - input from Flow field. If not specified, reflection will be used to get a
   * corresponding parameter name.
   *
   * @return Parameter mapping - input from Flow field
   */
  String from() default "";
}
