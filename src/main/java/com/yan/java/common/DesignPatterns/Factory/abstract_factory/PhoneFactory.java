package com.yan.java.common.DesignPatterns.Factory.abstract_factory;

import com.yan.java.common.DesignPatterns.Factory.Rectangle;
import com.yan.java.common.DesignPatterns.Factory.Shape;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class PhoneFactory implements Factory {
  @Override
  public Shape getShape() {
    return new Rectangle();
  }

  @Override
  public Color getColor() {
    return new Blue();
  }

  @Override
  public Product getProduct() {
    return new Phone();
  }
}
