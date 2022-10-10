package com.flower.engine.runner.event;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.flower.anno.event.EventType;
import com.flower.conf.Transition;
import com.flower.engine.runner.callfunc.EventFunctionCallState;
import com.flower.engine.runner.callfunc.FunctionCallContext;
import com.flower.engine.runner.callfunc.FunctionCallUtil;
import com.flower.engine.runner.state.ObjectStateAccess;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class EventRunUtil {
  @SuppressWarnings("CheckReturnValue")
  public static ListenableFuture<Void> runEvents(
      List<EventFunctionContext> functions,
      EventParametersProvider eventParametersProvider,
      EventType eventType,
      @Nullable Transition transition,
      @Nullable Throwable flowException) {
    if (functions.isEmpty()) return Futures.immediateVoidFuture();

    List<EventFunctionContext> parallelFunctions = new ArrayList<>();
    List<EventFunctionContext> blockingFunctions = new ArrayList<>();
    List<EventFunctionContext> synchronizedFunctions = new ArrayList<>();
    List<EventFunctionContext> synchronizedBreakingFunctions = new ArrayList<>();

    for (EventFunctionContext function : functions) {
      switch (function.eventFunction.concurrency) {
        case PARALLEL:
          parallelFunctions.add(function);
          break;
        case BLOCKING:
          blockingFunctions.add(function);
          break;
        case SYNCHRONIZED:
          synchronizedFunctions.add(function);
          break;
        case SYNCHRONIZED_BREAKING:
          synchronizedBreakingFunctions.add(function);
          break;
      }
    }

    return FluentFuture.from(
            runSequential(
                synchronizedBreakingFunctions,
                eventParametersProvider,
                eventType,
                transition,
                flowException,
                false))
        .transform(
            void_ ->
                runSequential(
                    synchronizedFunctions,
                    eventParametersProvider,
                    eventType,
                    transition,
                    flowException,
                    true),
            directExecutor())
        .transform(
            void_ ->
                runParallel(
                    blockingFunctions,
                    eventParametersProvider,
                    eventType,
                    transition,
                    flowException),
            directExecutor())
        .transform(
            void_ -> {
              runParallel(
                  parallelFunctions, eventParametersProvider, eventType, transition, flowException);
              return null;
            },
            directExecutor());
  }

  static ListenableFuture<Void> runSequential(
      List<EventFunctionContext> functions,
      EventParametersProvider eventParametersProvider,
      EventType eventType,
      @Nullable Transition transition,
      @Nullable Throwable flowException,
      boolean hideExceptions) {
    ListenableFuture<Void> currentFuture = Futures.immediateVoidFuture();
    for (EventFunctionContext function : functions) {
      currentFuture =
          Futures.transformAsync(
              currentFuture,
              void_ ->
                  invokeEventFunction(
                      function.eventProfileStateAccess,
                      function.flowStateAccess,
                      function.eventFunction.functionCallContext,
                      eventParametersProvider,
                      eventType,
                      transition,
                      flowException),
              directExecutor());

      if (hideExceptions) {
        currentFuture =
            Futures.catchingAsync(
                currentFuture,
                Throwable.class,
                e -> Futures.immediateVoidFuture(), // TODO: exception handling - log?
                directExecutor());
      }
    }
    return currentFuture;
  }

  static ListenableFuture<Void> runParallel(
      List<EventFunctionContext> functions,
      EventParametersProvider eventParametersProvider,
      EventType eventType,
      @Nullable Transition transition,
      @Nullable Throwable flowException) {
    List<ListenableFuture<Void>> futures = new ArrayList<>();
    for (EventFunctionContext function : functions) {
      futures.add(
          Futures.catchingAsync(
              invokeEventFunction(
                  function.eventProfileStateAccess,
                  function.flowStateAccess,
                  function.eventFunction.functionCallContext,
                  eventParametersProvider,
                  eventType,
                  transition,
                  flowException),
              Throwable.class,
              e -> Futures.immediateVoidFuture(), // TODO: exception handling - log?
              directExecutor()));
    }

    return Futures.whenAllComplete(futures)
        .callAsync(Futures::immediateVoidFuture, directExecutor());
  }

  static ListenableFuture<Void> invokeEventFunction(
      ObjectStateAccess eventProfileStateAccess,
      ObjectStateAccess flowStateAccess,
      FunctionCallContext functionCallContext,
      EventParametersProvider eventParametersProvider,
      EventType eventType,
      @Nullable Transition transition,
      @Nullable Throwable flowException) {
    EventFunctionCallState callState =
        new EventFunctionCallState(flowStateAccess, eventProfileStateAccess);
    return FunctionCallUtil.invokeEventFunction(
        callState,
        functionCallContext,
        eventParametersProvider,
        eventType,
        transition,
        flowException);
  }
}
