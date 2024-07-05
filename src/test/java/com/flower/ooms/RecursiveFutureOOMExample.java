package com.flower.ooms;

import com.flower.executor.TaskCheckingExecutor;
import com.google.common.util.concurrent.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class RecursiveFutureOOMExample {
  static ListenableFuture<Integer> recursion(
      ListenableFuture<Integer> previous, Executor executor) {
    return Futures.transformAsync(
        previous,
        i -> {
          System.out.println(i);
          if (i == Integer.MAX_VALUE) {
            return Futures.immediateFuture(i);
          } else {
            return recursion(Futures.immediateFuture(i + 1), executor);
          }
        },
        executor);
  }

  static void recursionDecoupled(
      ListenableFuture<Integer> previous, Executor executor, SettableFuture<Integer> set) {
    Futures.whenAllComplete(previous)
        .run(
            () -> {
              Integer i;
              try {
                i = previous.get();
                System.out.println(i);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }

              if (i == Integer.MAX_VALUE) {
                set.set(i);
              } else {
                recursionDecoupled(Futures.immediateFuture(i + 1), executor, set);
              }
            },
            executor);
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    ListeningScheduledExecutorService executor =
        MoreExecutors.listeningDecorator(TaskCheckingExecutor.builder().corePoolSize(1).build());

    // TODO: THIS OOMs (recursion)
    // uncomment for testing
    //        ListenableFuture<Integer> iFuture = recursion(Futures.immediateFuture(1), executor);

    // TODO: THIS doesn't OOM (recursion)
    // uncomment for testing
    SettableFuture<Integer> iFuture = SettableFuture.create();
    recursionDecoupled(Futures.immediateFuture(1), executor, iFuture);

    System.out.println(iFuture.get());
  }
}
