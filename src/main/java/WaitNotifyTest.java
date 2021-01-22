/**
 * Created by yanxujiang on 2020-11-25.
 *  * 使用 Lock Condition 来实现三个线程同时打印十遍 A B C
 *  * 同时遵守 ABC ABC 的格式
 */
public class WaitNotifyTest {
  private static final WaitNotifyTest instance = new WaitNotifyTest();

  private static final int THREAD_COUNT = 3;

  volatile int num;

  private static final Object lock1 = new Object();

  private static final Object lock2 = new Object();

  private static final Object lock3 = new Object();

  public static void main(String[] args) {
    new Thread(() -> {
      for (int i = 0; i < 10 ; i ++) {
        instance.loop(lock1, lock2, 0);
      }
    }, "A").start();

    new Thread(() -> {
      for (int i = 0; i < 10 ; i ++) {
        instance.loop(lock2, lock3, 1);
      }
    }, "B").start();

    new Thread(() -> {
      for (int i = 0; i < 10 ; i ++) {
        instance.loop(lock3, lock1, 2);
      }
    }, "C").start();


  }

  private void loop(Object current, Object next, int runNumber) {
    synchronized (current) {
      if (num != runNumber) {
        try {
          current.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      synchronized (next) {
        System.out.println(Thread.currentThread().getName());
        num = (runNumber + 1 ) % THREAD_COUNT;
        next.notify();
      }
    }
  }
}
