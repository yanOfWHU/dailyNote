package com.yan.java.common.algorithm.thread;

import java.util.concurrent.Semaphore;

public class Utils {


    private static Semaphore H2O_H = new Semaphore(2);
    private static Semaphore H2O_O = new Semaphore(0);

    public static void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        H2O_H.acquire();
        // releaseHydrogen.run() outputs "H". Do not change or remove this line.
        releaseHydrogen.run();
        H2O_O.release();
    }

    public static void oxygen(Runnable releaseOxygen) throws InterruptedException {
        H2O_O.acquire(2);
        // releaseOxygen.run() outputs "O". Do not change or remove this line.
        releaseOxygen.run();
        H2O_H.release(2);
    }


    public static void main(String [] args) {
        Utils ins = new Utils();
        String s = "OOHHOHHHHHH";
        for (char ch : s.toCharArray()) {
            if (ch == 'O') {
                new Thread(() -> {
                    try {
                        oxygen(() -> System.out.print("O"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                new Thread(() -> {
                    try {
                        hydrogen(() -> System.out.print("H"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}
