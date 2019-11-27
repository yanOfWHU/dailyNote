package com.yan.java.common.DesignPatterns.Factory;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public enum  ShapeType {
  Circle, Square, Rectangle;

  public static ShapeType parse(String shapeType) {
    if (shapeType == null) {
      return null;
    }
    if (shapeType.equalsIgnoreCase(Circle.name())) {
      return Circle;
    }
    if (shapeType.equalsIgnoreCase(Square.name())) {
      return Square;
    }
    if (shapeType.equalsIgnoreCase(Rectangle.name())) {
      return Rectangle;
    }
    return null;
  }
}
