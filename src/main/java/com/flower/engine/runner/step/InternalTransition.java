package com.flower.engine.runner.step;

import com.flower.conf.Transition;

import java.time.Duration;
import javax.annotation.Nullable;

public interface InternalTransition extends Transition {
  @Nullable
  String getStepName();

  boolean isTerminal();

  @Nullable
  String getNote();

  @Nullable
  Duration getDelay();
}
