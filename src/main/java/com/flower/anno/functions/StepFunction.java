package com.flower.anno.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** StepFunction. Function must be static. Function can return an object or a ListenableFuture. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface StepFunction {
  /**
   * Step name. If not specified, reflection will be used to get a corresponding method name.
   *
   * @return Step name
   */
  String name() default "";

  /** @return TransitFunction name */
  String transit() default "";

  /** @return Global function container for TransitFunction */
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
