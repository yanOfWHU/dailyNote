package com.yan.java.common.DesignPatterns.Factory.simple_factory;

import com.yan.java.common.DesignPatterns.Factory.Shape;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class Main {
  public static void main(String[] args) {
    ShapeFactory shapeFactory = new ShapeFactory();

    Shape rectangleShape = shapeFactory.getShape("rectangle");
    rectangleShape.draw();

    Shape square = shapeFactory.getShape("square");
    square.draw();

    Shape circle = shapeFactory.getShape("circle");
    circle.draw();

  }
}
