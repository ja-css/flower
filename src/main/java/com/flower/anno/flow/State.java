package com.flower.anno.flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Flow state field. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface State {
  /**
   * Flow state field name. If not specified, reflection will be used to get a corresponding
   * parameter name.
   *
   * @return Flow state field name
   */
  String name() default "";

  /**
   * Whether the state field is constant. The corresponding state class field must be final.
   * Constant fields can't be used as Out and InOut parameters.
   *
   * @return True, if the state field is constant.
   */
  boolean isConst() default false;
}
