package com.yan.java.common.DesignPatterns.SingletonMode;

/**
 * Created by yanxujiang on 2019-11-12.
 * 单例模式1
 * 最简单的单例模式
 * 饿汉式单例模式
 * 缺点，如其名，在类定义的时候，就已经初始化了类的实例
 */
public class Singleton_1_Simplest {

  private Singleton_1_Simplest() {

  }
  private static Singleton_1_Simplest instance = new Singleton_1_Simplest();

  public static Singleton_1_Simplest getInstance() {
    return instance;
  }
}
