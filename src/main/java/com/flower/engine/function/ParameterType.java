package com.flower.engine.function;

public enum ParameterType {
  // Common
  IN,
  OUT,
  IN_OUT,
  EXEC,

  // ======= BEGIN Exec-specific =======
  CHILD_FLOW_FACTORY_REF, // Special object FlowTypeFactory
  FLOW_REPO,
  // FLOW_TYPE_REF, //isn't that the same as CHILD_FLOW_FACTORY_REF?
  // ======= END Exec-specific =======

  // ======= BEGIN Transit-specific =======
  TRANSIT_IN_RET,
  TRANSIT_IN_RET_OR_EXCEPTION, // one of: either return value from exec or an exception that was
  // thrown
  // ======= END Transit-specific =======

  // ======= BEGIN Transit and StepAndTransit-specific =======
  STEP_REF, // Special object conf.StepRefPrm
  TERMINAL, // Special object conf.StepRefPrm
  // ======= END Transit and StepAndTransit-specific =======

  // ======= BEGIN Event-specific =======
  IN_FROM_FLOW,

  FLOW_INFO,
  STEP_INFO,
  // TRANSITIONER_INFO, same as step info
  TRANSITION_INFO, // Transition that a transitioner has returned
  EVENT_INFO, // , // In case an event function is bound to multiple events, defines EventType
  FLOW_EXCEPTION

  // RUNNING_TIME_STATS
  // ======= END Event-specific =======
}
