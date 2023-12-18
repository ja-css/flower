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
   * Type of a super-flow that the current flow extends. This Flow inheritance should be accompanied
   * by java class inheritance.
   *
   * @return Super-Flow type
   */
  Class<?> extendz() default void.class;

  /** @return First Step name */
  String firstStep() default "";

  /** State Serializer */
  Class<?> serializer() default void.class;

  /** True, if Flow can be restarted after reaching its final state, i.e. `FlowExec.continue(finalState)` will restart the Flow.
   * EventProfile states will be reset in this case.
   *
   * For example, a composite Flow that has finished with partial success - in this case we can allow restarting,
   * which could mean retrying only its failed subtasks (stored in its final state).
   * Just as an example, not limited to this scenario.
   */
  boolean restartable() default false;
}
