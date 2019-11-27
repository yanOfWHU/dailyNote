package com.yan.java.common.DesignPatterns.Prototype.login_mode;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class CreatePrototpye2  implements YPrototype{
  private Object attr;

  @Override
  public YPrototype clone() {
    YPrototype prototype = new CreatePrototpye2();
    prototype.setAttr(this.attr);
    return prototype;
  }

  @Override
  public Object getAttr() {
    return this.attr;
  }

  @Override
  public void setAttr(Object attr) {
    this.attr = attr;
  }

  @Override
  public String toString() {
    return "CreatePrototpye2{" +
        "attr=" + attr +
        '}';
  }
}
