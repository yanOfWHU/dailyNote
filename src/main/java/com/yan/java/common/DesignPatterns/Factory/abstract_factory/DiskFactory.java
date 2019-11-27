package com.yan.java.common.DesignPatterns.Factory.abstract_factory;

import com.yan.java.common.DesignPatterns.Factory.Circle;
import com.yan.java.common.DesignPatterns.Factory.Shape;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class DiskFactory implements Factory {
  @Override
  public Shape getShape() {
    return new Circle();
  }

  @Override
  public Color getColor() {
    return new White();
  }

  @Override
  public Product getProduct() {
    return new Disk();
  }
}
