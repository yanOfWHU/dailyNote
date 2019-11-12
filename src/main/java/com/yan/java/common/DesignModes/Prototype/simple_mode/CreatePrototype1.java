package com.yan.java.common.DesignModes.Prototype.simple_mode;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class CreatePrototype1 implements YPrototype {
  @Override
  public YPrototype clone() {
    YPrototype prototype = new CreatePrototype1();
    return prototype;
  }
}
