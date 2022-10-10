package com.flower.engine.runner.event;

import com.flower.conf.FlowInfoPrm;
import com.flower.conf.StepInfoPrm;

import javax.annotation.Nullable;

public interface EventParametersProvider {
  FlowInfoPrm getFlowInfo();

  StepInfoPrm getStepInfo();

  void setStepInfo(@Nullable StepInfoPrm stepInfoPrm);
}
