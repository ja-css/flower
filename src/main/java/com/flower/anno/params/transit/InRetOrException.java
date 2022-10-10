package com.flower.anno.params.transit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Step function return value parameter mapping (input). For use in TransitFunction ONLY! Mapping of
 * StepFunction return value to an input parameter of its TransitFunction.
 *
 * <p>Parameter must be of type conf.ReturnValueOrException&lt;T&gt; where generic parameter T
 * should correspond to the return type of step function
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface InRetOrException {
  /**
   * Function parameter name. Recommended to specify explicitly, since parameter names via
   * reflection look like (arg0, arg1, arg2, ...) in release builds. If not specified, reflection
   * will be used to get a corresponding parameter name.
   *
   * @return Parameter name
   */
  String name() default "";
}
