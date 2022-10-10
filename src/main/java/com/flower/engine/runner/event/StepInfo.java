package com.flower.engine.runner.event;

import com.flower.conf.StepInfoPrm;

import java.util.Objects;

public class StepInfo implements StepInfoPrm {
  private final String stepName;
  private final String transitName;
  private final boolean isFirstStep;

  public StepInfo(String stepName, String transitName, boolean isFirstStep) {
    this.stepName = stepName;
    this.transitName = transitName;
    this.isFirstStep = isFirstStep;
  }

  @Override
  public String stepName() {
    return stepName;
  }

  @Override
  public String transitName() {
    return transitName;
  }

  @Override
  public boolean isFirstStep() {
    return isFirstStep;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof StepInfo)) return false;
    StepInfo stepInfo = (StepInfo) o;
    return isFirstStep == stepInfo.isFirstStep
        && Objects.equals(stepName, stepInfo.stepName)
        && Objects.equals(transitName, stepInfo.transitName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stepName, transitName, isFirstStep);
  }

  @Override
  public String toString() {
    return "StepInfo{"
        + "stepName='"
        + stepName
        + '\''
        + ", transitName='"
        + transitName
        + '\''
        + ", isFirstStep="
        + isFirstStep
        + '}';
  }
}
