package com.flower.anno.params.step.transitOverride;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Override of TransitFunction parameters mappings. For use in StepFunction ONLY! */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TransitParametersOverride {
  TransitInPrm[] in() default {};

  TransitOutPrm[] out() default {};

  TransitInOutPrm[] inOut() default {};

  TransitInRetPrm[] inRet() default {};

  TransitStepRefPrm[] stepRef() default {};

  TransitTerminalPrm[] terminal() default {};
}
