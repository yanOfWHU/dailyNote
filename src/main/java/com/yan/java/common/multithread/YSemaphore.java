package com.yan.java.common.multithread;

import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanxujiang on 2019-11-09.
 * Semaphore 信号量使用
 * 常用于 生产者与消费者 模型例子
 * Semaphore 可以控制某个资源被同时访问的个数
 * acquire() 获取一个资源, 如果没有则等待
 * release() 释放一个资源
 *
 * note: Semaphore 没有设置初始值的方法
 * 构造方法只是设置了所有线程可以同时获取的资源阈值
 * 如果要设置初始值
 * 先调用 semaphore.acquire(initialCount)
 */
public class YSemaphore {
  private static final int POOL_SIZE = 50;
  private static final int TOTAL_THREAD_COUNT = 500;
  private static final int TIME_OUT = 500;
  private static Semaphore commonPool = new Semaphore(POOL_SIZE);
  // case 500个线程，同时竞争50个资源 保证同一时间最多只能有50个线程有资源
  public static void main(String[] args) {
    int i = 0;
    while (i < TOTAL_THREAD_COUNT) {
      i++;
      new Thread(new SemaphoreThread()).start();
    }
  }

  private static class SemaphoreThread implements Runnable {
    @Override
    public void run() {
      try {
        Object obj = acquireObj();
        System.out.println("get an obj" + obj.toString());
        releaseObj(obj);
      } catch (Exception e){
        e.printStackTrace();
      }
    }

    private void releaseObj(Object obj) {
      /*释放*/
      commonPool.release();
      System.out.println("release an obj." + obj.toString());
    }

    private Object acquireObj() {
      try {
        boolean canGet = commonPool.tryAcquire(TIME_OUT, TimeUnit.SECONDS);
        if (canGet) {
          return UUID.randomUUID().toString();
        }
      }catch (Exception e){
        e.printStackTrace();
      }
      throw  new IllegalArgumentException("timeout");
    }
  }
}
