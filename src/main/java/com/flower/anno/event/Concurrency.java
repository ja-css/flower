package com.flower.anno.event;

/**
 * Defines Concurrency.
 *
 * Running order:
 * 1) Fire and forget all PARALLEL for all Profiles;
 * 2) Run all BLOCKING for all Profiles:
 *      - concurrently between Profiles, but sequentially within the same Profile;
 * 3) Run all SYNCHRONIZED for all Profiles, strictly sequentially;
 * 4) Run all SYNCHRONIZED_BREAKING for all Profiles, strictly sequentially;
 * 5) Resume Flow execution.
 */
public enum Concurrency {
  // |---------------|----------------------|
  // |   Main flow   | Other Event Handlers |
  // |---------------|----------------------|
  // | Doesn't block |    Doesn't block     | - PARALLEL
  // | Blocks        |    Doesn't block     | - BLOCKING
  // | Blocks        |    Blocks            | - SYNCHRONIZED, SYNCHRONIZED_BREAKING
  // |---------------|----------------------|
  //TODO: Need to clarify above - `Other Event Handlers` vs `Event Handlers from other Event Profiles`

  /** Doesn't block main flow. Doesn't block other event handlers. */
  PARALLEL,
  /** Blocks main flow. Doesn't block other event handlers. */
  BLOCKING,
  /** Blocks main flow. Blocks other event handlers. */
  SYNCHRONIZED,
  /**
   * Blocks main flow. Blocks other event handlers. Raised Exception will be considered a Flow
   * Exception and fail the flow.
   * This Exception can't be caught in a Transitioner, and wil fail the Flow immediately.
   */
  SYNCHRONIZED_BREAKING

  // SYNCHRONIZED_BREAKING / Practical:
  //  I'm thinking about something like, if we have Flow persistence implemented as
  //  EventProfile, we may want to fail Flow execution on this particular Runner in case
  //  we fail to persist at a certain checkpoint.
  //  The idea is that in this case some other Runner should pick up the Flow and continue
  //  execution (because the failed status won't get saved as well).
  //  But it seems kind of unclear.
  //  Need to make sure that this is the best way to enable such features in Events.

  // SYNCHRONIZED_BREAKING / Philosophy:
  //  It's possible to think about BREAKING as of a property independent of CONCURRENCY. They're
  //  somewhat connected though, for example BREAKING can't be used with PARALLEL, because BREAKING
  //  as a concept assumes that it can't be run in parallel with Flow functionality. Similarly,
  //  implementing BREAKING with BLOCKING is possible, but will require rethinking of Flow being
  //  single threaded, with only one Exception possible to happen in the main path of Execution. For
  //  the above reasons at this time we enable "BREAKING" exclusively with SYNCHRONIZED Event
  //  Functions.

  // TODO:
  //  Consideration for OUT parameters and EventProfile state change.
  //  - everything is Nullable unless it's set by SYNCHRONIZED_BREAKING, because of possible exceptions;
  //  - When we run PARALLEL, there is no guarantee about when and in what order it finishes, so we can't let
  //    OUT come from PARALLEL;
  //  - Do we allow multiple event handlers of the BLOCKING type on Event Profile? If we do, and they run in parallel,
  //    then the same consideration can be applied to BLOCKING, because 2 BLOCKING handlers can try to OUT-update the same value;
  //    If, however, they run sequentially, then that's not an issue.
  //  - We can still indirectly update state in PARALLEL: for example by `@In AtomicInteger i`, `i.incrementAndGet()`.

  //TODO: Note:
  //  Note that in order to enable state save we need
}
