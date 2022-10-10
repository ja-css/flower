package com.flower.anno.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Globally reusable function. Function must be static. Function can return an object or a
 * ListenableFuture. Steps and transitioners can be declared as @StepCall and @TransitCall-s -
 * references to those functions
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GlobalFunction {
  /**
   * Global function name. If not specified, reflection will be used to get a corresponding method
   * name.
   *
   * @return Global function
   */
  String name() default "";

  /**
   * Mapping of Global Function's return value to a Flow field. If empty, return value is dropped.
   *
   * @return Parameter passing - return value output
   */
  // TODO: not needed?
  //  String returnTo() default "";
}
