package com.yan.java;

/**
 * Created by yanxujiang on 2020-07-30.
 */
public class ArrMain {
  public static void main(String[] args) {
    NThread nThread = new NThread();
    System.out.println("interrupt执行前");
    Thread thread = new Thread(() -> System.out.println("test"));
    thread.start();
    System.out.println(thread.isInterrupted());
    nThread.start();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    nThread.interrupt();
    System.out.println("interrupt执行后");
  }

  /**
   * 测试多线程的中断机制
   */
  static class NThread extends Thread{
    @Override
    public void run() {
      super.run();
      while(!isInterrupted()){
        System.out.println("依然存活...");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
