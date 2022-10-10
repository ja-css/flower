package com.flower.anno.event;

/**
 * Defines Concurrency.
 *
 * <p>Idea about execution is to unblock main Flow ASAP provided the constraints. Therefore, the
 * ordering will look as follows: 1) Run all EXCLUSIVE sequentially; 2) Once (1) is done, start all
 * SYNCHRONIZED and INDEPENDENT in parallel; 3) When all SYNCHRONIZED are done (INDEPENDENT may
 * still run), resume Main Flow.
 */
public enum Concurrency {
  // |---------------|----------------------|
  // |   Main flow   | Other Event Handlers |
  // |---------------|----------------------|
  // | Doesn't block |    Doesn't block     | - INDEPENDENT
  // | Blocks        |    Doesn't block     | - BLOCKING
  // | Blocks        |    Blocks            | - SYNCHRONIZED, SYNCHRONIZED_BREAKING
  // |---------------|----------------------|

  /** Doesn't block main flow. Doesn't block other event handlers. */
  PARALLEL,
  /** Blocks main flow. Doesn't block other event handlers. */
  BLOCKING,
  /** Blocks main flow. Blocks other event handlers. */
  SYNCHRONIZED,
  /**
   * Blocks main flow. Blocks other event handlers. Raised Exception will be considered a Flow
   * Exception and fail the flow.
   *
   * <p>It's possible to think about BREAKING as of a property independent of CONCURRENCY. They're
   * somewhat connected though, for example BREAKING can't be used with PARALLEL, because BREAKING
   * as a concept assumes that it can't be run in parallel with Flow functionality. Similarly,
   * implementing BREAKING with BLOCKING is possible, but will require rethinking of Flow being
   * single threaded, with only one Exception possible to happen in the main path of Execution. For
   * the above reasons at this time we enable "BREAKING" exclusively with SYNCHRONIZED Event
   * Functions.
   */
  SYNCHRONIZED_BREAKING
  // TODO: Practical: I'm thinking about something like, if we have Flow persistence implemented as
  // TODO: EventProfile, we may want to fail Flow execution on this particular Runner in case
  // TODO: we fail to persist at a certain checkpoint.
  // TODO: The idea is that in this case some other Runner should pick up the Flow and continue
  // TODO: execution (because, erm, the failed status won't get saved as well? - which can be
  // forced).
  // TODO: But it seems kind of unclear.
  // TODO: Need to make sure that this is the best way to enable such features in Events.
}
