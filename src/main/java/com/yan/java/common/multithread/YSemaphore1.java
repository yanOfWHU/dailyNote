package com.yan.java.common.multithread;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanxujiang on 2019-11-09.
 * 模拟两个线程 交替运行
 * 即缓冲区只有1个，生产者和消费者交替生产和消费
 * 而且生产者必须先生产
 * a->b->a->b->a...
 *
 * note: Semaphore 没有 init 初始值的方法，所以需要 init ，则要调用 acquire(N) 来实现
 */
public class YSemaphore1 {
  // 生产者生产的总数量
  private static int produceCount = 0;
  // 消费者消费的总数量
  private static int consumeCount = 0;
  // 两个 CountDownLatch 用户保证主线程和自线程运行顺序正确
  private static final CountDownLatch begin = new CountDownLatch(1);
  private static final CountDownLatch end = new CountDownLatch(1);
  // 两个信号量
  // produceSemaphore 是消费者能否消费的信号量
  private static final Semaphore produceSemaphore = new Semaphore(1);
  // consumeSemaphore 是生产者能否生产的信号量
  private static final Semaphore consumeSemaphore = new Semaphore(1);
  private static final int TIME_OUT = 500;
  private static final int TIMES = 100;
  public static void main(String[] args) {
    for (int i = 0; i< TIMES; i ++){
      new Thread(new YProducer(i)).start();
    }
    for (int i = 0; i <TIMES; i++){
      new Thread(new YConsumer(i)).start();
    }
    try {
      System.out.println("begin print");
      // note: 以下方法调用 是必须的 为了程序最开始保证生产者必须先生产
      // 互斥模型
      // 生产者信号量初始值为0
      // 消费者信号量初始值为1
      produceSemaphore.acquire();
      begin.countDown();
      end.await();
      System.out.println("end print");
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  private static class YProducer implements Runnable {
    int index;
    public YProducer(int i){
      this.index = i;
    }
    @Override
    public void run() {
      try {
        begin.await();
        consumeSemaphore.tryAcquire(TIME_OUT, TimeUnit.SECONDS);
        System.out.println("producer" + index + ", currentCount:"+ ++produceCount);
        produceSemaphore.release();
      }catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  private static class YConsumer implements Runnable {
    int index;
    public YConsumer(int i){
      this.index = i;
    }
    @Override
    public void run() {
      try {
        begin.await();
        produceSemaphore.tryAcquire(TIME_OUT, TimeUnit.SECONDS);
        System.out.println("consumer" + index + ", currentCount:"+ ++consumeCount);
        consumeSemaphore.release();
        if (consumeCount == TIMES) {
          end.countDown();
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    }
  }
}
