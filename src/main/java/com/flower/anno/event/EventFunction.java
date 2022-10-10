package com.flower.anno.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Event Handler Function and its Binding to EventType-s. Function must be static. Function must
 * return void or ListenableFuture&lt;Void&gt;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventFunction {
  /**
   * Event Handler name. If not specified, reflection will be used to get a corresponding method
   * name.
   *
   * @return Event Handler name
   */
  String name() default "";

  /** @return Event Types to bind an Event Function to */
  EventType[] types();

  /** @return Concurrency level */
  Concurrency concurrency() default Concurrency.PARALLEL;
}
