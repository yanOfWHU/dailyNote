package com.yan.java.common.DesignPatterns.Decorator;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class PancackeDecoratorWithMeat extends PancakeDecorator {

  public PancackeDecoratorWithMeat(IPancake pancake) {
    super(pancake);
  }

  @Override
  public void cook() {
    System.out.println("加了片肉");
    super.cook();
  }
}
