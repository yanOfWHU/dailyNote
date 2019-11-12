package com.yan.java.common.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class YCallableFuture implements Callable<Integer> {
  private static final int COUNT = 100;

  @Override
  public Integer call() throws Exception {
    System.out.println("sub thread calculate");
    Thread.sleep(1000);
    int sum = 0;
    for (int j = 0; j < COUNT; j++) {
      sum += j;
    }
    return sum;
  }

  public static void main(String[] args) {
    // create a thread pool
    ExecutorService executor = Executors.newCachedThreadPool();
    // create a callable task
    YCallableFuture task = new YCallableFuture();
    // get the result
    Future<Integer> result = executor.submit(task);
    // shutdown the thread pool
    executor.shutdown();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("main thread executing");

    try {
      if (result.get() != null) {
        System.out.println("task result: " + result.get());
      } else {
        System.out.println("get none");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    System.out.println("all task finished");
  }
}
