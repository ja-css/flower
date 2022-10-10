package com.flower.engine.configuration;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public enum FunctionParameterType {
  IN,
  OUT,
  IN_OUT,

  IN_FROM_FLOW,
  OUT_FROM_FLOW,
  IN_OUT_FROM_FLOW,

  IN_RET,
  IN_RET_OR_EXCEPTION,

  STEP_REF,
  TERMINAL,
  FLOW_TYPE_FACTORY,

  FLOW_INFO,
  STEP_INFO,
  TRANSITION_INFO,
  EVENT_INFO,
  FLOW_EXCEPTION; // ,

  // RUNNING_TIME_STATS;

  static <T> boolean bothIn(T t1, T t2, Set<T> inSet) {
    return inSet.contains(t1) && inSet.contains(t2);
  }

  public static boolean isCompatible(FunctionParameterType type1, FunctionParameterType type2) {
    if (type1 == type2) return true;

    return bothIn(type1, type2, ImmutableSet.of(IN, IN_RET))
        || bothIn(type1, type2, ImmutableSet.of(IN, IN_FROM_FLOW))
        || bothIn(type1, type2, ImmutableSet.of(OUT, OUT_FROM_FLOW))
        || bothIn(type1, type2, ImmutableSet.of(IN_OUT, IN_OUT_FROM_FLOW))
        || bothIn(type1, type2, ImmutableSet.of(STEP_REF, TERMINAL));
  }
}
