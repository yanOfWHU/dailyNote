package com.yan.java.common.DesignPatterns.Decorator;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("====================土豪 全部加上 ==================");
    IPancake base = new Pancake();
    IPancake addEgg = new PancakeDecoratorWithEgg(base);
    IPancake addMeat = new PancackeDecoratorWithMeat(addEgg);
    IPancake addLettuce = new PancakeDecoratorWithLettuce(addMeat);
    addLettuce.cook();

    System.out.println("================== 我是程序员 只要2个鸡蛋和一个生菜===========");
    IPancake base1 = new Pancake();
    IPancake addEgg1 = new PancakeDecoratorWithEgg(base1);
    IPancake addTwoEgg = new PancakeDecoratorWithEgg(addEgg1);
    IPancake addLettuce1 = new PancakeDecoratorWithLettuce(addTwoEgg);
    addLettuce1.cook();
  }
}
