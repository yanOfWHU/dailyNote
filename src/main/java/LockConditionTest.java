import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yanxujiang on 2020-11-25.
 *
 * 使用 Lock Condition 来实现三个线程同时打印十遍 A B C
 * 同时遵守 ABC ABC 的格式
 */
public class LockConditionTest {
  Lock lock = new ReentrantLock();
  int num = 1;
  Condition condition1 = lock.newCondition();
  Condition condition2 = lock.newCondition();
  Condition condition3 = lock.newCondition();

  public static void main(String[] args) {

    LockConditionTest obj = new LockConditionTest();

    new Thread(() -> {
      for (int i = 0 ; i < 10; i++) {
        obj.loopA();
      }
    }, "A").start();

    new Thread(() -> {
      for (int i = 0 ; i < 10; i++) {
        obj.loopB();
      }    }, "B").start();

    new Thread(() -> {
      for (int i = 0 ; i < 10; i++) {
        obj.loopC();
      }    }, "C").start();

  }


  private void loopA() {
    lock.lock();
    try {
      if (num != 1) {
        // await method will release the owned lock
        condition1.await();
      }
      System.out.println(Thread.currentThread().getName());
      num = 2;
      condition2.signal();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

  }

  private void loopB() {
    lock.lock();
    try {
      if (num!= 2) {
        condition2.await();
      }
      System.out.println(Thread.currentThread().getName());
      num = 3;
      condition3.signal();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

  }

  private void loopC() {
    lock.lock();
    try {
      if (num!= 3) {
        condition3.await();
      }
      System.out.println(Thread.currentThread().getName());
      num = 1;
      condition1.signal();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

  }
}
