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
   * Event Profile name. If not specified, reflection will be used to get a corresponding full class name.
   * This name is used for serialization and shows in logs, to keep the context in case obfuscation is used.
   *
   * @return Event Profile name
   */
  String name() default "";

  /** State Serializer */
  Class<?> serializer() default void.class;
}
