package com.flower.engine.runner.step;

import com.flower.conf.Transition;

import java.time.Duration;
import javax.annotation.Nullable;

public class TransitionImpl implements InternalTransition {
  private final boolean isTerminal;
  @Nullable private final String stepName;
  @Nullable private final String note;
  @Nullable private final Duration delay;

  private TransitionImpl(String stepName, @Nullable String note) {
    this.isTerminal = false;
    this.stepName = stepName;
    this.delay = null;
    this.note = note;
  }

  private TransitionImpl(@Nullable String note) {
    this.isTerminal = true;
    this.stepName = null;
    this.delay = null;
    this.note = note;
  }

  private TransitionImpl(TransitionImpl clone, @Nullable Duration newDelay) {
    this.stepName = clone.stepName;
    this.isTerminal = clone.isTerminal;
    this.delay = newDelay;
    this.note = clone.getNote();
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
  public String getNote() { return note; }

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

  // pre-hashing all possible steps and returning them from Immutable Hash Map is redundant,
  // because all steps are effectively static - created once on init and reused in form of
  // parameter's SpecialObjects
  public static InternalTransition getStepTransition(String stepName, @Nullable String note) {
    return new TransitionImpl(stepName, note);
  }

  public static InternalTransition getTerminalTransition(@Nullable String note) {
    return new TransitionImpl(note);
  }
}
