package com.yan.java.common.DesignPatterns.Factory;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class Circle implements Shape {
  @Override
  public void draw() {
    System.out.println("Inside Circle::draw() method.");
  }
}
