package com.flower.anno.flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FlowType {
  /**
   * Flow name. If not specified, reflection will be used to get a corresponding full class name.
   *
   * @return Flow name
   */
  String name() default "";

  /**
   * Name of a super-flow that the current flow extends. This Flow inheritance should be accompanied
   * by java class inheritance.
   *
   * @return Super-Flow name
   */
  String extendz() default "";

  /** @return First Step name */
  String firstStep() default "";
}
