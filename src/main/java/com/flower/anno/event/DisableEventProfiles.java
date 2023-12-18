package com.flower.anno.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Disables Engine EventProfile for a Flow */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DisableEventProfiles {
  /** @return External EventProfiles to disable. */
  Class<?>[] value() default {};

  /** @return true to disable all external Event Profiles */
  boolean disableAllExternal() default false;

  // TODO: do we need boolean disableAllEngine()?
}
