package com.yan.java.common.DesignModes.Factory.factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class YDiv implements YOperation {
  @Override
  public double getRet(double d1, double d2) throws Exception {
    if (d2 == 0) {
      throw new IllegalArgumentException("divider cannot be zero");
    }
    return d1 / d2;
  }
}
