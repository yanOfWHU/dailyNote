package com.yan.java.common.multithread;

/**
 * Created by yanxujiang on 2019-11-08.
 *
 * ThreadLocal 是一个关于创建线程局部变量的类
 *
 * 通常情况下，我们创建的变量是可以被任意一个线程访问并且修改的
 * 而 ThreadLocal 创建的变量只能被当前线程访问 其他线程无法访问修改
 *
 * 原理： set && get 源码
 * set:
 * 1. 获取当前线程
 * 2. 利用当前线程作为句柄获取 ThreadLocalMap 对象（Thread 类有 ThreadLocalMap 成员变量）
 * 3. 如果不为空 则设置值，否则创建一个 ThreadLocalMap 对象 并设置值
 * get:
 * 原理同 set
 *
 * Java 中，栈内存属于单个线程
 * 堆内存对所有线程共享。
 * 但是 ThreadLocal 对象并不是存在于堆中。
 * ThreadLocal 本质也是被创建的类(线程)持有，ThreadLocal 对象实际也是被线程实例持有
 * 只是通过一种巧妙的方式修改了只有当前线程可见。
 *
 * 最后 ThreadLocal 的内部类 ThreadLocalMap，选择 key 的时候，只是当前ThreadLocal实例 的弱引用
 * 所以并不会造成内存泄漏
 */
public class YThreadLocal {

  private void testThreadLocal() {
    Thread t = new Thread() {
      // 不设置初始值 get
      // ThreadLocal <String> internalVar = new ThreadLocal<>();
      // 设置初始值
      ThreadLocal <String> internalVar = ThreadLocal.withInitial(() -> Thread.currentThread().getName());

      @Override
      public void run() {
        super.run();
        /**
         * todo
         * 查看 set 以及 get 源码
         */
        internalVar.set(currentThread().getName());
        System.out.println(internalVar.get());
      }
    };
    t.start();
  }

  public static void main(String[] args) {
    for (int i = 0; i < 5; i++) {
      new YThreadLocal().testThreadLocal();
    }
  }
}
