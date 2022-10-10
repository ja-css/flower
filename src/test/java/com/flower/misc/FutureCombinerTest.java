package com.flower.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.flower.utilities.FutureCombiner;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.testing.TestingExecutors;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class FutureCombinerTest {
  private static final String EXCEPTION_MESSAGE = "FutureCombiner test exception";

  static class MyTestException extends RuntimeException {
    public MyTestException(String message) {
      super(message);
    }
  }

  final int num1 = 123, num2 = 456;

  @Test
  public void testChainedFutures_Future1() throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future = Futures.immediateFuture(func(num1));

    ListenableFuture<List<Integer>> list = FutureCombiner.chainResults(future);
    assertEquals(list.get().size(), 1);
    assertTrue(list.get().contains(num1));
  }

  @Test
  public void testChainedFutures_Future2() throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future = Futures.immediateFuture(func(num2));

    ListenableFuture<List<Integer>> resultList = FutureCombiner.chainResults(future);
    assertEquals(resultList.get().size(), 1);
    assertTrue(resultList.get().contains(num2));
  }

  @Test
  public void testChainedFutures_FutureWithList() throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future1 = Futures.immediateFuture(func(num1));
    ListenableFuture<Integer> future2 = Futures.immediateFuture(func(num2));
    ListenableFuture<List<Integer>> list = FutureCombiner.chainResults(future2);

    ListenableFuture<List<Integer>> resultList = FutureCombiner.chainResults(future1, list);
    assertEquals(resultList.get().size(), 2);
    assertTrue(resultList.get().contains(num1));
    assertTrue(resultList.get().contains(num2));
  }

  @Test
  public void testChainedFutures_FutureWithNullList()
      throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future = Futures.immediateFuture(func(num1));

    ListenableFuture<List<Integer>> resultList = FutureCombiner.chainResults(future, null);
    assertEquals(resultList.get().size(), 1);
    assertTrue(resultList.get().contains(num1));
  }

  @Test
  public void testChainedFutures_ListWithList() throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future1 = Futures.immediateFuture(func(num1));
    ListenableFuture<Integer> future2 = Futures.immediateFuture(func(num2));

    ListenableFuture<List<Integer>> list1 = FutureCombiner.chainResults(future1);
    ListenableFuture<List<Integer>> list2 = FutureCombiner.chainResults(future2);

    ListenableFuture<List<Integer>> resultList = FutureCombiner.chainListResults(list1, list2);
    assertEquals(resultList.get().size(), 2);
    assertTrue(resultList.get().contains(num1));
    assertTrue(resultList.get().contains(num2));
  }

  @Test
  public void testChainedFutures_ListWithNullList()
      throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future = Futures.immediateFuture(func(num1));
    ListenableFuture<List<Integer>> list = FutureCombiner.chainResults(future);

    ListenableFuture<List<Integer>> resultList = FutureCombiner.chainListResults(list, null);
    assertEquals(resultList.get().size(), 1);
    assertTrue(resultList.get().contains(num1));
  }

  @Test
  public void testChainedFutures_NullListWithList()
      throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future = Futures.immediateFuture(func(num2));
    ListenableFuture<List<Integer>> list = FutureCombiner.chainResults(future);
    ListenableFuture<List<Integer>> resultList = FutureCombiner.chainListResults(null, list);
    assertEquals(resultList.get().size(), 1);
    assertTrue(resultList.get().contains(num2));
  }

  @Test
  public void testChainedFutures_NullListWithNullList()
      throws ExecutionException, InterruptedException {
    ListenableFuture<List<Integer>> list = FutureCombiner.chainListResults(null, null);
    assertTrue(list.get().isEmpty());
  }

  @Test
  public void testChainedFutures_varArgs_shouldConcatenateFutures()
      throws ExecutionException, InterruptedException {
    ListenableFuture<Integer> future1 = Futures.immediateFuture(func(num1));
    ListenableFuture<Integer> future2 = Futures.immediateFuture(func(num2));

    ListenableFuture<List<Integer>> list1 = FutureCombiner.chainResults(future1);
    ListenableFuture<List<Integer>> list2 = FutureCombiner.chainResults(future2);

    List<Integer> resultList = FutureCombiner.chainListResults(null, list1, list2).get();
    assertEquals(ImmutableList.of(num1, num2), resultList);
  }

  @Test
  public void testChainedFutures_varArgsWhenAFutureFails_shouldReturnFailedFuture() {
    ListenableFuture<Integer> future1 = Futures.immediateFuture(func(num1));
    ListenableFuture<Integer> future2 = Futures.immediateFailedFuture(new RuntimeException());

    ListenableFuture<List<Integer>> list1 = FutureCombiner.chainResults(future1);
    ListenableFuture<List<Integer>> list2 = FutureCombiner.chainResults(future2);

    assertThrows(
        ExecutionException.class, () -> FutureCombiner.chainListResults(null, list1, list2).get());
  }

  @Test
  public void testDelayedExecution() throws ExecutionException, InterruptedException {
    ListeningScheduledExecutorService scheduler = TestingExecutors.sameThreadScheduledExecutor();

    final int val = 324;
    ListenableFuture<Integer> future =
        FutureCombiner.delayExecution(scheduler, 10, TimeUnit.MILLISECONDS, () -> func(val));
    assertEquals(future.get(), val);
  }

  @Test
  public void testDelayedExecutionAsync() throws ExecutionException, InterruptedException {
    ListeningScheduledExecutorService scheduler = TestingExecutors.sameThreadScheduledExecutor();

    final int val = 324;
    ListenableFuture<Integer> future =
        FutureCombiner.delayExecutionAsync(
            scheduler, 10, TimeUnit.MILLISECONDS, () -> Futures.immediateFuture(func(val)));
    assertEquals(future.get(), val);
  }

  Void exceptionSync() {
    throw new MyTestException(EXCEPTION_MESSAGE);
  }

  @Test
  public void testDelayedExecutionException() throws InterruptedException {
    ListeningScheduledExecutorService scheduler = TestingExecutors.sameThreadScheduledExecutor();

    ListenableFuture<Void> future =
        FutureCombiner.delayExecution(scheduler, 10, TimeUnit.MILLISECONDS, this::exceptionSync);

    try {
      future.get();
      fail();
    } catch (ExecutionException e) {
      assertEquals(e.getCause().getClass(), MyTestException.class);
      assertEquals(e.getCause().getMessage(), EXCEPTION_MESSAGE);
    }
  }

  @Test
  public void testDelayedExecutionAsyncException() throws InterruptedException {
    ListeningScheduledExecutorService scheduler = TestingExecutors.sameThreadScheduledExecutor();

    ListenableFuture<Void> future =
        FutureCombiner.delayExecutionAsync(
            scheduler, 10, TimeUnit.MILLISECONDS, () -> Futures.immediateFuture(exceptionSync()));

    try {
      future.get();
      fail();
    } catch (ExecutionException e) {
      assertEquals(e.getCause().getClass(), MyTestException.class);
      assertEquals(e.getCause().getMessage(), EXCEPTION_MESSAGE);
    }
  }

  @Test
  public void testDelayedExecutionParameterPassing() {
    final int delay1 = 10;
    final int delay2 = 20;
    final TimeUnit timeUnit1 = TimeUnit.MICROSECONDS;
    final TimeUnit timeUnit2 = TimeUnit.MILLISECONDS;

    Callable<ListenableFuture<Integer>> callable1 = () -> Futures.immediateFuture(func(123));
    Callable<ListenableFuture<Integer>> callable2 = () -> Futures.immediateFuture(func(456));

    final ListeningScheduledExecutorService scheduler =
        mock(ListeningScheduledExecutorService.class);
    ListeningScheduledExecutorService testScheduler =
        TestingExecutors.sameThreadScheduledExecutor();

    // Since we're testing parameter passing, return values don't matter
    when(scheduler.schedule(callable1, delay1, timeUnit1))
        .thenReturn(testScheduler.schedule(callable1, 0, TimeUnit.MILLISECONDS));
    when(scheduler.schedule(callable2, delay2, timeUnit2))
        .thenReturn(testScheduler.schedule(callable2, 0, TimeUnit.MILLISECONDS));

    FutureCombiner.delayExecutionAsync(scheduler, delay1, timeUnit1, callable1);
    FutureCombiner.delayExecutionAsync(scheduler, delay2, timeUnit2, callable2);
  }

  Integer func(int i) {
    return i;
  }
}
