package com.yan.java.common.multithread;

import java.util.concurrent.CountDownLatch;

/**
 * Created by yanxujiang on 2019-11-09.
 * CountDownLatch 使用
 * CountDownLatch 使用场景： 保证多线程按照顺序运行
 * 1. CountDownLatch 构造的时候传参数 N, CountDownLatch end = new CountDownLatch(N)
 * 2. end.await() 阻塞当前线程，直到 end 的数字到达 0 才会拉起当前线程
 * 3. end.countDown() 可以在其他线程调用 每一次调用，值减少 1
 */
public class YCountDownLatch {
  // 该实例是 运动员比赛示范 只保证主/子线程运行顺序 并且保证主线程在子线程运行完毕再结束
  // 主线程 begin -->> 10个player运行 --> 主线程 end
  public static void main(String[] args) {
    CountDownLatch begin = new CountDownLatch(1);
    CountDownLatch end = new CountDownLatch(10);

    for (int i = 0; i < 10; i++) {
      new Thread(new Player(begin, end)).start();
    }

    try {
      System.out.println("the race begin");
      begin.countDown();
      end.await();
      System.out.println("the race end");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static class Player implements Runnable {
    private CountDownLatch begin;
    private CountDownLatch end;

    public Player(CountDownLatch begin, CountDownLatch end) {
      this.begin = begin;
      this.end = end;
    }

    @Override
    public void run() {
      try {
        begin.await();
        System.out.println(Thread.currentThread().getName() + "arrived");
        end.countDown();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
