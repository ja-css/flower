package com.flower.conf;

public interface StepInfoPrm {
  String stepName();

  String transitName();

  boolean isFirstStep();

  /**
   * Simple step's Exec and Transit is the same Function.
   * Thus, we need this information to make sense in which way we receive the following events,
   * and whether the calls are combined or not.
   *   Events: BEFORE_EXEC, AFTER_EXEC, BEFORE_TRANSIT, AFTER_TRANSIT
   * Can be useful to avoid doing the same thing twice AFTER_EXEC and AFTER_TRANSIT, if there is no real Transit call.
   */
  boolean isSimpleStep();
}
