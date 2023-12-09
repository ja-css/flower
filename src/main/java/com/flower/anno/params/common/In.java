package com.flower.anno.params.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Input function parameter. Mapping of State field to a parameter. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface In {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";

  /**
   * Parameter mapping - input from State field. If not specified, reflection will be used to get a
   * corresponding parameter name.
   *
   * @return Parameter mapping - input from State field
   */
  String from() default "";

  /**
   * Ensure that the field is not null, similarly to com.google.common.base.Preconditions.checkNotNull
   * Will throw an Exception if the field wasn't initialized.
   *
   * @return true - throw Exception if field is not initialized.
   */
  boolean checkNotNull() default false;
}
