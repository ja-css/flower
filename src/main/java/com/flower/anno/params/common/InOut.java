package com.flower.anno.params.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Input/Output function parameter. Mapping of State field to a parameter, and back to a State
 * field. Parameter must be of type conf.InOutPrm
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface InOut {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";

  /** @return Parameter mapping - input from and output to State field */
  String fromAndTo() default "";

  /**
   * MANDATORY (default) - setting Out value is mandatory, NullAway considers it a field
   * initializer. The way it's enforced is that failing to set Out value will cause an exception at
   * runtime.
   *
   * TODO: For now, it's better than nothing and clearly outlines the idea. Hopefully in
   *  future versions the check will trigger at init or compile time.
   *
   * UPDATE: Since runtime Exception is so ANNOYING and DISRUPTIVE I disabled it in the engine,
   * until we have compile-time or init-time check.
   *
   * <p>OPTIONAL - setting Out value is not mandatory, but NullAway won't consider it a field
   * initializer,
   *
   * @return whether setting Out value is mandatory
   */
  Output out() default Output.MANDATORY;

  /**
   * Use with CAUTION, this alters Flower NullAway behavior!
   * Informs Flower that at this step the field will never be null.
   * Flower will throw Exception at runtime if the field wasn't initialized at this step.
   * Flower NullAway will consider the field as initialized for this and subsequent steps.
   *
   * @return true - throw Exception if field is not initialized.
   */
  boolean throwIfNull() default false;
}
