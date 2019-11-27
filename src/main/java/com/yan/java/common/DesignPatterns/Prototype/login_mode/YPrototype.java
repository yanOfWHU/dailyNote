package com.yan.java.common.DesignPatterns.Prototype.login_mode;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public interface YPrototype {
  YPrototype clone();

  Object getAttr();

  void setAttr(Object attr);
}
