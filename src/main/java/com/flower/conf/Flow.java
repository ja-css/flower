package com.flower.conf;

import java.util.Optional;

public interface Flow<T> {
  /** @return Flow instance identifier */
  FlowId getId();

  // TODO: should this be here?
  // TODO not used? remove
  /** @return Flow status */
  Optional<String> getStatus();

  // TODO: should this be here?
  /** @return Current Step name */
  String getCurrentStep();

  // TODO: should this be here? EventProfile states aren't
  /** @return Flow state */
  T getState();
}
