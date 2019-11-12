package com.yan.java.common.multithread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yanxujiang on 2019-11-09.
 * <p>
 * Summary: 让一组线程等待，直到等待的线程数量到达barrier上限，然后唤醒所有线程。
 * <p>
 * CyclicBarrier是一个同步辅助类，它允许一组线程互相等待，直到到达某个公共屏障点 (common barrier point)。
 * 在涉及一组固定大小的线程的程序中，这些线程必须不时地互相等待，此时 CyclicBarrier 很有用。
 * 因为该 barrier 在释放等待线程后可以重用，所以称它为循环 的 barrier
 * <p>
 * CyclicBarrier类似于CountDownLatch也是个计数器，
 * 不同的是CyclicBarrier数的是调用了CyclicBarrier.await()进入等待的线程数，
 * 当线程数达到了CyclicBarrier初始时规定的数目时，所有进入等待状态的线程被唤醒并继续
 * <p>
 * 示范：
 * 2000个人参与长跑
 * 每次只能20个人参与(随机取人)
 */
public class YCyclicBarrier {
  private static final int TOTAL_COUNT = 2000;
  private static final int CYCLE_COUNT = 20;
  private static CountDownLatch begin = new CountDownLatch(1);
  private static CountDownLatch end = new CountDownLatch(1);
  private static CyclicBarrier barrier = new CyclicBarrier(CYCLE_COUNT);
  private static AtomicInteger currentCount = new AtomicInteger(0);

  public static void main(String[] args) {
    for (int i = 0; i < TOTAL_COUNT; i++) {
      new Thread(new YRunner(i)).start();
    }
    try {
      System.out.println("begin");
      begin.countDown();
      end.await();
      System.out.println("end");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static class YRunner implements Runnable {

    private int index;

    public YRunner(int i) {
      this.index = i;
    }

    @Override
    public void run() {
      try {
        begin.await();
        Thread.sleep((long)(Math.random() * 10000));
        System.out.println(String.format("Runner %d arrived, current people %d", index, barrier.getNumberWaiting()));
        barrier.await();
        System.out.println(String.format("Runner %d finished", index));
        if (currentCount.incrementAndGet() == TOTAL_COUNT) {
          end.countDown();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
