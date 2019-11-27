package com.yan.java.common.DesignPatterns.Factory.factory;

import com.yan.java.common.DesignPatterns.Factory.Rectangle;
import com.yan.java.common.DesignPatterns.Factory.Shape;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class RectangleFactory implements ShapeFactory {
  @Override
  public Shape getShape() {
    return new Rectangle();
  }
}
