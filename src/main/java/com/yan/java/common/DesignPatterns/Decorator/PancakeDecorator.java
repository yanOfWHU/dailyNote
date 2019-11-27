package com.yan.java.common.DesignPatterns.Decorator;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public abstract class PancakeDecorator implements IPancake {
  // 包装了 pancake 类
  private IPancake pancake;

  public PancakeDecorator(IPancake pancake) {
    this.pancake = pancake;
  }

  @Override
  public void cook() {
    if (this != null) {
      pancake.cook();
    }
  }
}
