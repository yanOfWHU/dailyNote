package com.yan.java.common.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by yanxujiang on 2019-11-09.
 * 另一个示范
 * 保证 50个线程按照 main 1 2 3 4 。。。50 main 的顺序运行
 */
public class YCountDownLatch1 {
  private static final int COUNT = 50;
  private static CountDownLatch main = new CountDownLatch(1);
  private static List<CountDownLatch> countDownLatches = createCountDownLatch(COUNT);
  public static void main(String[] args) {

    for (int i = 0; i < COUNT; i++){
      new Thread(new Player(i)).start();
    }
    try {
      System.out.println("begin race");
      main.countDown();
      countDownLatches.get(countDownLatches.size() - 1).await();
      System.out.println("end race");
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  private static List<CountDownLatch> createCountDownLatch(int count) {
    List<CountDownLatch> countDownLatches = new ArrayList<>();
    for (int i = 0; i<count; i++){
      countDownLatches.add(new CountDownLatch(1));
    }
    return countDownLatches;
  }

  private static class Player implements Runnable {
    private CountDownLatch current;
    private CountDownLatch prev;
    int index;
    public Player(int i) {
      if (i == 0){
        prev = null;
      } else {
        prev = countDownLatches.get(i-1);
      }
      index = i;
      current = countDownLatches.get(i);
    }

    @Override
    public void run() {
      try {
        if (index == 0) {
          main.await();
        } else {
          prev.await();
        }
        System.out.println(Thread.currentThread().getName() + " arrived");
        current.countDown();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
