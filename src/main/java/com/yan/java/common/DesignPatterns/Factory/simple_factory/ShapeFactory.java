package com.yan.java.common.DesignPatterns.Factory.simple_factory;

import com.yan.java.common.DesignPatterns.Factory.Circle;
import com.yan.java.common.DesignPatterns.Factory.Rectangle;
import com.yan.java.common.DesignPatterns.Factory.Shape;
import com.yan.java.common.DesignPatterns.Factory.ShapeType;
import com.yan.java.common.DesignPatterns.Factory.Square;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class ShapeFactory {

  public Shape getShape(String shapeType) {
    switch (ShapeType.parse(shapeType)){
      case Square:
        return new Square();
      case Circle:
        return new Circle();
      case Rectangle:
        return new Rectangle();
        default:
          return null;
    }
  }
}
