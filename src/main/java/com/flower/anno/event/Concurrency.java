package com.flower.anno.event;

/**
 * Defines Concurrency.
 *
 * Execution order:
 * 1) Fire and forget all PARALLEL Events on all EventProfiles;
 * 2) At the same time, start all BLOCKING Events in parallel on all EventProfiles;
 *      keep track of BLOCKING Events per EventProfile and wait for them to finish;
 * 3) For a given EventProfile, when all BLOCKING Events on that EventProfile are done, run SYNCHRONIZED Events sequentially for that EventProfile
 *      (i.e. different EventProfiles still run their SYNCHRONIZED Events in parallel,
 *      but for a particular EventProfile its SYNCHRONIZED Events are executed sequentially);
 * 4) Form a common queue for all EventProfiles for SYNCHRONIZED_BREAKING events:
 *      - add all SYNCHRONIZED_BREAKING Events for a EventProfile to the queue once all SYNCHRONIZED Events for that EventProfile are done
 *          (start ASAP, even if SYNCHRONIZED of even BLOCKING Events for other EventProfiles are still executing);
 *      - run SYNCHRONIZED_BREAKING sequentially across all EventProfiles,
 *          i.e. if more SYNCHRONIZED_BREAKING Events from other EventProfiles are added to the queue,
 *          only one event from the queue is executed at the same time;
 *      - if one of SYNCHRONIZED_BREAKING events fails with Exception, Flow is failed with that Exception;
 *      - even if one of SYNCHRONIZED_BREAKING Events fails, we still run all of them,
 *          and if more than one fails - we throw a combined Exception in the end.
 * 5) Resume Flow execution, if no SYNCHRONIZED_BREAKING failed.
 */
public enum Concurrency {
  /** Doesn't block the Flow. Doesn't block other event handlers.
   * EventProfile State update using @Out/@InOut is NOT allowed. */
  PARALLEL,
  /** Blocks the Flow. Doesn't block other event handlers.
   * EventProfile State using @Out/@InOut is NOT allowed.  */
  BLOCKING,
  /** Blocks the Flow. Blocks other event handlers.
   * EventProfile State update using @Out/@InOut is ALLOWED. */
  SYNCHRONIZED,
  /**
   * Blocks the Flow. Blocks other event handlers. Any raised Exception will also fail the Flow (that's why `BREAKING`).
   * Raised Exception can't be caught in a Transitioner, the Flow will fail!
   * EventProfile State update using @Out/@InOut is ALLOWED.
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
  //  Note that in order to enable state save we need some final state on Flow and all stateful EventProfiles.
  //  We consider EventProfiles stateful if they are serializable.
  //  That's why we can't allow updating EventProfileState from PARALLEL, and that's why if we allow such update from BLOCKING
  //  we must run all of BLOCKING on EventProfile sequentially, so that we don't get any issues with concurrent state updates.
  //  Alternatively, we can only allow state update from SYNCHRONIZED.
}
