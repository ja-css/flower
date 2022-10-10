package com.flower.anno.params.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Output function parameter. Mapping of an output parameter to a State field. Parameter must be of
 * type conf.OutPrm
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Out {
  /**
   * Function parameter name. Recommended specifying it explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";

  /** @return Parameter mapping - output to State field */
  String to() default "";

  /**
   * MANDATORY (default) - setting Out value is mandatory, NullAway considers it a field
   * initializer. The way it's enforced is that failing to set Out value will cause an exception at
   * runtime. TODO: For now, it's better than nothing and clearly outlines the idea. Hopefully in
   * future versions the check will trigger at init or compile time.
   *
   * <p>OPTIONAL - setting Out value is not mandatory, but NullAway won't consider it a field
   * initializer,
   *
   * @return whether setting Out value is mandatory
   */
  Output out() default Output.MANDATORY;
}
