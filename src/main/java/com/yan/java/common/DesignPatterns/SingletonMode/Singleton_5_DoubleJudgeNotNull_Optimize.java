package com.yan.java.common.DesignPatterns.SingletonMode;

/**
 * Created by yanxujiang on 2019-11-12.
 *
 * 同样是双重判断加锁判断单例对象是否是空
 * 不过通过使用volatile关键字解决了方法4 因为JVM优化，初始化不按顺序执行，导致的多线程初始化问题
 *
 * volatile关键字，禁止指令重排序，保证写操作完成之前不能调用读操作
 */
public class Singleton_5_DoubleJudgeNotNull_Optimize {

  private Singleton_5_DoubleJudgeNotNull_Optimize() {

  }
  private static volatile Singleton_5_DoubleJudgeNotNull_Optimize instance;

  public static Singleton_5_DoubleJudgeNotNull_Optimize getInstance() {
    if (instance == null) {
      synchronized (Singleton_5_DoubleJudgeNotNull_Optimize.class) {
        if (instance == null) {
          instance = new Singleton_5_DoubleJudgeNotNull_Optimize();
        }
      }
    }
    return instance;
  }
}
