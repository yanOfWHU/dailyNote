package com.yan.java.common.multithread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 顺序打印 ABC
 */
public class ABCSeqPrint_Condition {

    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    private static int times = 0;

    public static final int LOOP_TIME = 10;

    public static void main(String [] args) throws Exception {

        ABCSeqPrint_Condition ins = new ABCSeqPrint_Condition();

        System.out.println("begin");

        Thread t1 = new Thread(
                () -> {
                    for (int i = 0 ; i < LOOP_TIME; i++) {
                        try {
                            ins.printA();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        Thread t2 = new Thread(
                () -> {
                    for (int i = 0 ; i < LOOP_TIME; i++) {
                        try {
                            ins.printB();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        Thread t3 = new Thread(
                () -> {
                    for (int i = 0 ; i < LOOP_TIME; i++) {
                        try {
                            ins.printC();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        System.out.println("\nfinished");


    }

    private void printC() throws Exception {
        try {
            lock.lock();
            while (times % 3 != 2) {
                c3.await();
            }
            times = times + 1;
            print("C");
            c1.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void printB() throws Exception {
        try {
            lock.lock();
            while (times % 3 != 1) {
                c2.await();
            }
            times = times + 1;
            print("B");
            c3.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void printA() throws Exception {
        try {
            lock.lock();
            while (times % 3 != 0) {
                c1.await();
            }
            times = times + 1;
            print("A");
            c2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void print(String s) {
        System.out.print(s);
    }

}
