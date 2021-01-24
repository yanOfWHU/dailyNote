package com.yan.java.common.multithread;

import java.util.concurrent.Semaphore;

public class ABCSeqPrint_Semaphore {

    private static Semaphore semaphoreA = new Semaphore(1);

    private static Semaphore semaphoreB = new Semaphore(0);

    private static Semaphore semaphoreC = new Semaphore(0);

    private static final int MAX_LOOP = 10;

    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            for (int i = 0; i < MAX_LOOP; i++) {
                printA();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < MAX_LOOP; i++) {
                printB();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < MAX_LOOP; i++) {
                printC();
            }
        }).start();


    }

    private static void printC() {
        try {
            semaphoreC.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        print("C");
        semaphoreA.release();
    }

    private static void printB() {
        try {
            semaphoreB.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        print("B");
        semaphoreC.release();
    }

    private static void printA() {
        try {
            semaphoreA.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        print("A");
        semaphoreB.release();
    }

    private static void print(String ch) {
        System.out.print(ch);
    }
}
