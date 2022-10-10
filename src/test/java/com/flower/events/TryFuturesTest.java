package com.flower.events;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class TryFuturesTest {
  ListenableFuture<Integer> success() {
    return Futures.immediateFuture(123);
  }

  ListenableFuture<Integer> failure() {
    return Futures.immediateFailedFuture(new Exception("OOPS"));
  }

  // Plain but cumbersome way
  ListenableFuture<Integer> transformCall(ListenableFuture<Integer> future, String funcName) {
    return Futures.transformAsync(
        Futures.catchingAsync(
            future,
            Throwable.class,
            t -> {
              System.out.printf("Exception caught %s - %s\n", funcName, t.getMessage());
              return Futures.immediateFailedFuture(t);
            },
            directExecutor()),
        i -> {
          System.out.printf("Calling %s - %d\n", funcName, i);
          return Futures.immediateFuture(i);
        },
        directExecutor());
  }

  // Woowee better syntax looks more like try / catch, excellent
  ListenableFuture<Integer> transformCall2(ListenableFuture<Integer> future, String funcName) {
    return FluentFuture.from(future)
        .transformAsync(
            i -> {
              System.out.printf("Calling %s - %d\n", funcName, i);
              return Futures.immediateFuture(i);
            },
            directExecutor())
        .catchingAsync(
            Throwable.class,
            t -> {
              System.out.printf("Exception caught %s - %s\n", funcName, t.getMessage());
              return Futures.immediateFailedFuture(t);
            },
            directExecutor());
  }

  // Double-checking that exceptions thrown from both original future and transform block are being
  // caught
  ListenableFuture<Integer> transformCall3(ListenableFuture<Integer> future, String funcName) {
    return FluentFuture.from(future)
        .transformAsync(
            i -> {
              System.out.printf("Calling %s - %d\n", funcName, i);
              if (true) throw new Exception("OOPS2");
              return Futures.immediateFuture(i);
            },
            directExecutor())
        .catchingAsync(
            Throwable.class,
            t -> {
              System.out.printf("Exception caught %s - %s\n", funcName, t.getMessage());
              return Futures.immediateFailedFuture(t);
            },
            directExecutor());
  }

  // @Test
  public void test() throws InterruptedException {
    transformCall(success(), "success");
    transformCall(failure(), "failure");
    Thread.sleep(10000);
  }

  // @Test
  public void test2() throws InterruptedException {
    transformCall2(success(), "success");
    transformCall2(failure(), "failure");
    Thread.sleep(10000);
  }

  // @Test
  public void test3() throws InterruptedException {
    transformCall3(success(), "success");
    transformCall3(failure(), "failure");
    Thread.sleep(10000);
  }

  // @Test
  public void test4() throws InterruptedException {
    transformCall2(
        Futures.transformAsync(
            success(),
            i -> {
              throw new Exception("OOPS3");
            },
            directExecutor()),
        "success");
    Thread.sleep(10000);
  }

  // @Test
  public void chainTest() throws InterruptedException {
    FluentFuture<Integer> future =
        FluentFuture.from(failure())
            .transformAsync(
                i -> {
                  System.out.printf("Calling success - %d\n", i);
                  return Futures.immediateFuture(i);
                },
                directExecutor())
            .transformAsync(
                i -> {
                  System.out.printf("Calling success 2 - %d\n", i);
                  return Futures.immediateFuture(i);
                },
                directExecutor())
            .catchingAsync(
                Throwable.class,
                t -> {
                  System.out.printf("Exception caught 1 - %s\n", t.getMessage());
                  return Futures.immediateFailedFuture(t);
                },
                directExecutor())
            .transformAsync(
                i -> {
                  System.out.printf("Calling success 3 - %d\n", i);
                  return Futures.immediateFuture(i);
                },
                directExecutor())
            .catchingAsync(
                Throwable.class,
                t -> {
                  System.out.printf("Exception caught 2 - %s\n", t.getMessage());
                  return Futures.immediateFuture(321);
                },
                directExecutor())
            .transformAsync(
                i -> {
                  System.out.printf("Calling success 4 - %d\n", i);
                  return Futures.immediateFuture(i);
                },
                directExecutor())
            .catchingAsync(
                Throwable.class,
                t -> {
                  System.out.printf("Exception caught 3 - %s\n", t.getMessage());
                  return Futures.immediateFailedFuture(t);
                },
                directExecutor());

    Thread.sleep(10000);
  }
}
