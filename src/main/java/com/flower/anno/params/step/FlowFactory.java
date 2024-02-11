package com.flower.anno.params.step;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flow factory parameter. For use in StepFunction ONLY!
 *
 * <p>Represents a reference to a Factory for a FlowType.
 *
 * <p>Parameter must be of type conf.FlowFactoryPrm
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface FlowFactory {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";

  /** @return Referenced Flow name */
  String flowTypeName() default "";

  /** @return Referenced Flow class */
  Class<?> flowType() default void.class;

  /** As deducible from the name, dynamic FlowFactory can't validate the possibility of running FlowType on startup.
   * That can potentially result in runtime errors related to flow registry.
   * If possible, avoid using dynamic and specify FlowType explicitly, to fail fast in case of any configuration issues. */
  boolean dynamic() default false;
}
