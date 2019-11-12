package com.yan.java.common.DesignModes.Factory.factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class AddFactory implements Factory {
  @Override
  public YOperation createOperation() {
    return new YAdd();
  }
}
