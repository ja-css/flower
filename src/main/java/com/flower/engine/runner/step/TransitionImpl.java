package com.flower.engine.runner.step;

import com.flower.conf.Transition;

import java.time.Duration;
import javax.annotation.Nullable;

public class TransitionImpl implements InternalTransition {
  private final boolean isTerminal;
  @Nullable private final String stepName;
  @Nullable private final Duration delay;

  private TransitionImpl(String stepName) {
    this.isTerminal = false;
    this.stepName = stepName;
    this.delay = null;
  }

  private TransitionImpl() {
    this.isTerminal = true;
    this.stepName = null;
    this.delay = null;
  }

  private TransitionImpl(TransitionImpl clone, @Nullable Duration newDelay) {
    this.stepName = clone.stepName;
    this.isTerminal = clone.isTerminal;
    this.delay = newDelay;
  }

  @Nullable
  @Override
  public String getStepName() {
    return stepName;
  }

  @Override
  public boolean isTerminal() {
    return isTerminal;
  }

  @Nullable
  @Override
  public Duration getDelay() {
    return delay;
  }

  @Override
  public Transition setDelay(@Nullable Duration delay) {
    return new TransitionImpl(this, delay);
  }

  @Override
  public String toString() {
    return "TransitionImpl{"
        + "isTerminal="
        + isTerminal
        + ", stepName='"
        + stepName
        + '\''
        + ", delay="
        + delay
        + '}';
  }

  private static final InternalTransition TERMINAL = new TransitionImpl();

  // pre-hashing all possible steps and returning them from Immutable Hash Map is redundant,
  // because all steps are effectively static - created once on init and reused in form of
  // parameter's SpecialObjects
  public static InternalTransition getStepTransition(String stepName) {
    return new TransitionImpl(stepName);
  }

  public static InternalTransition getTerminalTransition() {
    return TERMINAL;
  }
}
