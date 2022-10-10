package com.flower.engine.runner.event;

import com.flower.anno.event.EventType;
import com.flower.conf.Transition;
import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;

public interface EventRunner {
  ListenableFuture<Void> runEvents(
      EventType eventType,
      EventContext eventContext,
      EventParametersProvider eventParametersProvider,
      @Nullable Transition transition,
      @Nullable Throwable flowException);
}
