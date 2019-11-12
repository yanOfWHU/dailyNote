package com.yan.java.common.DesignModes.Factory.factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class SubFactory implements Factory {
  @Override
  public YOperation createOperation() {
    return new YSub();
  }
}
