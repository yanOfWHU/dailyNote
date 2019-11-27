package com.yan.java.common.DesignPatterns.Decorator;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class PancakeDecoratorWithEgg extends PancakeDecorator {

  public PancakeDecoratorWithEgg(IPancake pancake) {
    super(pancake);
  }

  @Override
  public void cook() {
    System.out.println("加了一个鸡蛋");
    super.cook();
  }
}
