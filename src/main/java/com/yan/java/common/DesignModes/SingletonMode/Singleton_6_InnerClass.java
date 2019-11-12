package com.yan.java.common.DesignModes.SingletonMode;

/**
 * Created by yanxujiang on 2019-11-12.
 * 使用静态内部类的方法
 * 静态内部类初始化和外部类是分开的。
 * 调用 getInstance 之前，静态内部类不会初始化
 * 第一次调用之后，该方法就唯一生成了一个实例
 */
public class Singleton_6_InnerClass {

  private Singleton_6_InnerClass() {

  }

  private static class SingletonHolder {
    private static Singleton_6_InnerClass instance = new Singleton_6_InnerClass();
  }

  public static Singleton_6_InnerClass getInstance() {
    return SingletonHolder.instance;
  }

}
