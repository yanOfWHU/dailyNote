package com.yan.java.common.DesignPatterns.Decorator;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class PancakeDecoratorWithLettuce extends PancakeDecorator {

  public PancakeDecoratorWithLettuce(IPancake pancake) {
    super(pancake);
  }

  @Override
  public void cook() {
    System.out.println("加了一颗生菜");
    super.cook();
  }
}
