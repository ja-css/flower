package com.flower.anno.flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GlobalFunctionContainer {
  /**
   * Global Function Container name. If not specified, reflection will be used to get a
   * corresponding full class name.
   *
   * @return Global Function Container name
   */
  String name() default "";
}
