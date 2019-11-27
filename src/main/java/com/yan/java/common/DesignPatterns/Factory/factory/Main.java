package com.yan.java.common.DesignPatterns.Factory.factory;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class Main {
  public static void main(String[] args) {
    ShapeFactory rectangleFactory = new RectangleFactory();
    rectangleFactory.getShape().draw();

    ShapeFactory circleFactory = new CircleFactory();
    circleFactory.getShape().draw();

    ShapeFactory squareFactory = new SquareFactory();
    squareFactory.getShape().draw();
  }
}
