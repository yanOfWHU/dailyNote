package com.yan.java.common.DesignPatterns.Singleton;

/**
 * Created by yanxujiang on 2019-11-12.
 * 同样是比较简单的单例模式，但是解决了1在类初始化的时候就创建实例的问题
 * 但是同样有限制，只能在单线程中使用
 *
 * 懒汉式。
 * 懒汉式的单例模式，只有需要的时候才会去实例化。
 * 简单的懒汉式都是非线程安全的。
 * 后续单例模式3-5都是基于懒汉式的优化。
 */
public class Singleton_2_Simple {

  private Singleton_2_Simple() {
  }

  private static Singleton_2_Simple instance;

  public static Singleton_2_Simple getInstance() {
    if (instance == null) {
      instance = new Singleton_2_Simple();
    }
    return instance;
  }
}

