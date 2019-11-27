package com.yan.java.common.DesignPatterns.SingletonMode;

/**
 * Created by yanxujiang on 2019-11-12.
 * 解决了方法2单线程问题，该单例模式可以多线程运行
 * 缺点是由于是加了synchronized，所以一次只会允许一个线程进入方法，其他线程需要阻塞等待当前线程运行完毕
 */
public class Singleton_3_MultiThread {

  private Singleton_3_MultiThread() {

  }

  private static Singleton_3_MultiThread instance;

  public synchronized static Singleton_3_MultiThread getInstance() {
    if (instance == null) {
      instance = new Singleton_3_MultiThread();
    }
    return instance;
  }
}
