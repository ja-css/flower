package com.flower.anno.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * StepFunction call - defines a call to GlobalFunction. Call Function must be static. Call Function
 * must return void. Parameters, if any, must override GlobalFunction parameters. Call
 * implementation is not needed. If any implementation is present, it won't be executed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface StepCall {
  /**
   * Step name. If not specified, reflection will be used to get a corresponding method name.
   *
   * @return Step name
   */
  String name() default "";

  /** @return GlobalFunction container */
  Class<?> globalFunctionContainer();

  /** @return GlobalFunction name */
  String globalFunctionName();

  /** @return TransitFunction or GlobalFunction name */
  String transit() default "";

  /** @return GlobalFunctionContainer reference for Global transitioner function */
  Class<?> globalTransitContainer() default void.class;
  /** @return TransitFunction reference to Global function */
  String globalTransit() default "";

  /**
   * Mapping of Step function's return value to a Flow field. If empty, return value is dropped.
   *
   * @return Parameter passing - return value output
   */
  String returnTo() default "";
}
