package com.yan.java.common.DesignPatterns.Prototype.simple_mode;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class Main {
  private YPrototype prototype;

  public Main(YPrototype prototype) {
    this.prototype = prototype;
  }

  public void cloneOperation(YPrototype prototype) {
    Object copy = prototype.clone();
  }
}
