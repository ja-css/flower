package com.flower.events;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

public class TryFutures2Test {
  ListenableFuture<Integer> success() {
    return Futures.immediateFuture(123);
  }

  ListenableFuture<Integer> failure() {
    return Futures.immediateFailedFuture(new Exception("OOPS"));
  }

  // @Test
  public void tesAllSuccess1() throws InterruptedException {
    ListenableFuture<Integer> future1 = success();
    ListenableFuture<Integer> future2 = success();
    ListenableFuture<Integer> future3 = success();

    Futures.whenAllSucceed(future1, future2, future3)
        .call(
            () -> {
              System.out.println("weew 1");
              return null;
            },
            MoreExecutors.directExecutor());

    Thread.sleep(10000);
  }

  // @Test
  public void tesAllSuccess2() throws InterruptedException {
    ListenableFuture<Integer> future1 = success();
    ListenableFuture<Integer> future2 = success();
    ListenableFuture<Integer> future3 = failure();

    Futures.whenAllSucceed(future1, future2, future3)
        .call(
            () -> {
              System.out.println("weew 2");
              return null;
            },
            MoreExecutors.directExecutor());

    Thread.sleep(10000);
  }

  // @Test
  public void tesAllSuccess3() throws InterruptedException {
    ListenableFuture<Integer> future1 = success();
    ListenableFuture<Integer> future2 = success();
    ListenableFuture<Integer> future3 = failure();

    Futures.whenAllComplete(future1, future2, future3)
        .call(
            () -> {
              System.out.println("weew 3");
              return null;
            },
            MoreExecutors.directExecutor());

    Thread.sleep(10000);
  }
}
