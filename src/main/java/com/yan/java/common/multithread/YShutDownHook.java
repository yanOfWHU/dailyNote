package com.yan.java.common.multithread;

/**
 * 测试 addShutdownHook()程序  保证程序一定正常退出
 * 对数据库操作，依赖数九事务保证程序关闭时数据的完整性
 * 但是 有些时候，一个业务要求的原子操作，不止包括事务，比如外部接口，消息队列等等。
 * addShutdownHook()方法可以加入一个钩子，在程序退出的时候触发该钩子
 * (退出是指 ctrl+c 或者 kill -15，但是如果是kill -9是没办法的。)
 * kill的 signal http://www.cnblogs.com/taobataoma/archive/2007/08/30/875743.html
 *
 * @author yanxujiang
 * @date 2019-10-11 17:36
 */
public class YShutDownHook extends Thread{
  /**
   * 测试线程  用于模拟一个原子操作
   */
  private static class TaskThread extends Thread {
    @Override
    public void run() {
      System.out.println("thread begin");
      try {
        YShutDownHook.sleep(1000);
        System.out.println("task 1 ok ...");
        YShutDownHook.sleep(1000);
        System.out.println("task 2 ok ...");
        YShutDownHook.sleep(1000);
        System.out.println("task 3 ok ...");
        YShutDownHook.sleep(1000);
        System.out.println("task 4 ok ...");
        YShutDownHook.sleep(1000);
        System.out.println("task 5 ok ...");
      } catch (InterruptedException e){
        e.printStackTrace();
      }

      System.out.println("thread end\n\n");
    }
  }

  /**
   * 注册hook程序，保证线程能够正常运行
   *
   * @param checkThread
   */
  private static void addShutdownHook(final Thread checkThread) {
    // 为了保证 TaskThread 不在中途退出， 添加 shutdownHook
    Runtime.getRuntime().addShutdownHook(new Thread(
        ()->{
          System.out.println("收到关闭信号，hook 启动， 开始检测线程状态");
          // 不断检测状态
          for (int i = 0; i < 100; i++) {
            if(checkThread.getState() == State.TERMINATED) {
              System.out.println("检测到线程执行完毕， 退出hook");
              return;
            }
            try {
              YShutDownHook.sleep(100);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          System.out.println("检测超时，放弃等待，退出 hook, 此时线程会被强制关闭");
        }
    ));
  }

  public static void main(String[] args) throws InterruptedException {
    final TaskThread taskThread = new TaskThread();

    // thread 运行之前 添加钩子
    addShutdownHook(taskThread);

    taskThread.start();
  }
}
