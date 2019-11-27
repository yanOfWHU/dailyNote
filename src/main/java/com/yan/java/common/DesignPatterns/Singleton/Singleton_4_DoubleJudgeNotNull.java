package com.yan.java.common.DesignPatterns.Singleton;

/**
 * Created by yanxujiang on 2019-11-12.
 * 解决了写法3只允许一个线程调用的问题
 * 但是缺点在于JVM执行引用赋值的时候并不是一个原子操作。
 * 操作顺序
 * 1. 堆内存为单例对象 Singleton 分配内存空间
 * 2. 对 Singleton 执行初始化操作
 * 3. 最后将引用变量指向实例所对应的内存地址，完成赋值
 * JVM 对该步骤完成了优化，步骤2和3可能不按顺序执行。
 * 如果此时多个线程访问，则可能会造成当前线程还在实例化(执行了步骤13，先挂起一段时间执行2)，
 * 但是另一个线程调用该方法，此时instance引用变量不为空，
 * 然而此时单例对象还没有初始化，则会造成报错
 */
public class Singleton_4_DoubleJudgeNotNull {

  private Singleton_4_DoubleJudgeNotNull() {

  }

  private static Singleton_4_DoubleJudgeNotNull instance;

  public static Singleton_4_DoubleJudgeNotNull getInstance() {
    if (instance == null) {
      synchronized (Singleton_4_DoubleJudgeNotNull.class) {
        if (instance == null) {
          instance = new Singleton_4_DoubleJudgeNotNull();
        }
      }
    }
    return instance;
  }
}
