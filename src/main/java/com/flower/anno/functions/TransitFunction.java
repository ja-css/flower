package com.flower.anno.functions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TransitFunction (a.k.a. Transitioner, not to be confused with Transition) Function must be
 * static. Function must return Transit or ListenableFuture&lt;Transit&gt;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitFunction {
  /**
   * TransitFunction name. Recommended to specify explicitly. If not specified, reflection will be
   * used to get a corresponding method name.
   *
   * @return TransitFunction name
   */
  String name() default "";
}
