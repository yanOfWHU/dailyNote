package com.yan.java.common.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class YCallableFutureTask {

  public static void main(String[] args) {
    ExecutorService executor = Executors.newCachedThreadPool();
    YCallableFuture task = new YCallableFuture();
    FutureTask<Integer> futureTask = new FutureTask<>(task);
    executor.submit(futureTask);
    executor.shutdown();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("main thread executing");

    try {
      if (futureTask.get() != null) {
        System.out.println("task result: " + futureTask.get());
      } else {
        System.out.println("get none");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    System.out.println("main thread finish");
  }
}
