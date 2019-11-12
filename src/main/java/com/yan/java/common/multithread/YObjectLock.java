package com.yan.java.common.multithread;

import java.util.function.IntConsumer;

/**
 * Created by yanxujiang on 2019-11-11.
 * Object wait notify notifyAll 方法使用
 * wait：当前线程释放获取到的对象锁，等待其他线程运行
 * notify：随机唤醒一个等待的线程，提醒其锁已经释放，可以运行
 * notify：唤醒所有等待的线程
 */
public class YObjectLock {
  // 示范：三个线程，一个打印0 一个打印奇数 一个打印偶数
  // 打印结果 0 1 0 2 0 3 0 4 0 5 。。。
  private static final int PRINT_COUNT = 50;
  public static void main(String[] args) {
    IntConsumer intConsumer = value->System.out.print(value);

    PrintEntry entry = new PrintEntry();
    new Thread(()-> {
      try {
        entry.printZero(intConsumer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();

    new Thread(()-> {
      try {
        entry.printEven(intConsumer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();

    new Thread(()-> {
      try {
        entry.printOdd(intConsumer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();

  }

  private static class PrintEntry {
    private int count;
    public PrintEntry() {
      count = 0;
    }

    private void printZero(IntConsumer consumer) throws InterruptedException {
      for (int i = 0; i < PRINT_COUNT; i ++) {
        synchronized (this) {
          while (count % 2 != 0) {
            wait();
          }
          consumer.accept(0);
          count++;
          notifyAll();
        }
      }
    }

    private void printEven(IntConsumer consumer) throws InterruptedException {
      for (int i = 1; i < PRINT_COUNT+1; i=i+2){
        synchronized (this) {
          while (count % 2 == 0 || count % 4 != 1) {
            wait();
          }
          consumer.accept(i);
          count++;
          notifyAll();
        }
      }
    }

    private void printOdd(IntConsumer consumer) throws InterruptedException {
      for(int i = 2; i<=PRINT_COUNT; i=i+2) {
        synchronized (this) {
          while (count % 2 == 0 || count % 4 != 3) {
            wait();
          }
          consumer.accept(i);
          count++;
          notifyAll();
        }
      }
    }
  }
}
