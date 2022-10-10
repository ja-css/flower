package com.flower.anno.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Defines Event Profile Container */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EventProfileContainer {
  /**
   * Event Profile name. If not specified, reflection will be used to get a corresponding full class
   * name.
   *
   * @return Event Profile name
   */
  String name() default "";
}
