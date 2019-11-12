package com.yan.java.common.DesignModes.Factory.factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class YSub implements YOperation{
  @Override
  public double getRet(double d1, double d2) throws Exception {
    return d1 - d2;
  }
}
