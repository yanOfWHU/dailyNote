package com.yan.java.common.DesignPatterns.Factory.factory;

import com.yan.java.common.DesignPatterns.Factory.Shape;
import com.yan.java.common.DesignPatterns.Factory.Square;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class SquareFactory implements ShapeFactory {
  @Override
  public Shape getShape() {
    return new Square();
  }
}
