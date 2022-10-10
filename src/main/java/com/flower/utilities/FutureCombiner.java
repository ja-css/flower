package com.flower.utilities;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

/** Utility class - allows to combine Futures and execute functions with a delay. */
public class FutureCombiner {
  /**
   * Transforms a Future that returns a result to a Future that returns a List containing original
   * Future's result. Useful as a first step in chaining Futures.
   *
   * @param result Result Future
   * @param <T> Result type
   * @return Future that returns a List containing original Future's result.
   */
  public static <T> ListenableFuture<List<T>> chainResults(ListenableFuture<T> result) {
    return chainResults(result, null);
  }

  /**
   * Creates a Future that returns a List of results containing all elements of mainResultList plus
   * a result of nextResult
   *
   * @param nextResult Future that returns a single result
   * @param mainResultList Future that returns a List of results
   * @param <T> Result type
   * @return Future that returns mainResultList plus nextResult
   */
  public static <T> ListenableFuture<List<T>> chainResults(
      ListenableFuture<T> nextResult, @Nullable ListenableFuture<List<T>> mainResultList) {
    if (mainResultList == null) {
      return chainResults(nextResult, Futures.immediateFuture(new ArrayList<>()));
    } else {
      return Futures.whenAllComplete(nextResult, mainResultList)
          .call(
              () -> {
                List<T> list = mainResultList.get();
                T element = nextResult.get();
                list.add(element);
                return list;
              },
              directExecutor());
    }
  }

  /**
   * Creates a Future that returns a List of results containing all elements of mainResultList and
   * all elements of nextResults
   *
   * @param nextResults Additional Future that returns a List of results
   * @param mainResultList Main Future that returns a List of results
   * @param <T> Result type
   * @return Future that returns mainResultList plus nextResults
   */
  public static <T> ListenableFuture<List<T>> chainListResults(
      @Nullable ListenableFuture<List<T>> nextResults,
      @Nullable ListenableFuture<List<T>> mainResultList) {
    if (mainResultList == null && nextResults == null)
      return Futures.immediateFuture(new ArrayList<>());
    else if (mainResultList == null) return Preconditions.checkNotNull(nextResults);
    else if (nextResults == null) return Preconditions.checkNotNull(mainResultList);
    else {
      return Futures.whenAllComplete(nextResults, mainResultList)
          .call(
              () -> {
                List<T> list1 = mainResultList.get();
                List<T> list2 = nextResults.get();
                list1.addAll(list2);
                return list1;
              },
              directExecutor());
    }
  }

  /**
   * Creates a Future that returns a List of results containing all elements of all futures passed
   * as an argument
   *
   * @param futures Futures that should be combined
   * @param <T> Result type
   * @return Future that returns a list with elements from all futures
   */
  @SafeVarargs
  public static <T> ListenableFuture<List<T>> chainListResults(
      ListenableFuture<List<T>>... futures) {
    ImmutableList<ListenableFuture<List<T>>> futuresList =
        Arrays.stream(futures).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());

    if (futuresList.isEmpty()) {
      return Futures.immediateFuture(ImmutableList.of());
    }

    return Futures.whenAllSucceed(futuresList)
        .call(
            () -> {
              ImmutableList.Builder<T> resultsBuilder = ImmutableList.builder();

              for (ListenableFuture<List<T>> future : futuresList) {
                resultsBuilder.addAll(Futures.getDone(future));
              }

              return resultsBuilder.build();
            },
            directExecutor());
  }

  /**
   * Transforms a Future with a nested Future into a single Future. Can be used to simplify
   * scheduling with ScheduledExecutorService.
   *
   * @param future Future that returns another Future that returns a result
   * @param <T> Result type
   * @return Future that returns a result
   */
  public static <T> ListenableFuture<T> collapseNestedFuture(
      ListenableFuture<ListenableFuture<T>> future) {
    return Futures.transformAsync(
        future, nestedFuture -> nestedFuture, MoreExecutors.directExecutor());
  }

  /**
   * Executes an asynchronous function with a delay
   *
   * @param scheduler Scheduler
   * @param delay Delay
   * @param timeUnit Delay time unit
   * @param callable Asynchronous function to execute with a delay
   * @param <T> Result type
   * @return Future of delayed function execution
   */
  public static <T> ListenableFuture<T> delayExecutionAsync(
      ListeningScheduledExecutorService scheduler,
      long delay,
      TimeUnit timeUnit,
      Callable<ListenableFuture<T>> callable) {
    return collapseNestedFuture(scheduler.schedule(callable, delay, timeUnit));
  }

  /**
   * Executes a function with a delay
   *
   * @param scheduler Scheduler
   * @param delay Delay
   * @param timeUnit Delay time unit
   * @param callable Function to execute with a delay
   * @param <T> Result type
   * @return Future of delayed function execution
   */
  public static <T> ListenableFuture<T> delayExecution(
      ListeningScheduledExecutorService scheduler,
      long delay,
      TimeUnit timeUnit,
      Callable<T> callable) {
    return scheduler.schedule(callable, delay, timeUnit);
  }
}
