package com.flower.anno.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * StepAndTransitFunction call - defines a call to GlobalFunction. Call Function must be static.
 * Call Function must return void. Parameters, if any, must override GlobalFunction parameters. Call
 * implementation is not needed. If any implementation is present, it won't be executed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SimpleStepCall {
  /**
   * StepAndTransitFunction name. If not specified, reflection will be used to get a corresponding
   * method name.
   *
   * @return StepAndTransitFunction name
   */
  String name() default "";

  /** @return GlobalFunction name */
  String globalFunctionName();
}
