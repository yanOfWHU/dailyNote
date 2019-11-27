package com.yan.java.common.DesignPatterns.Factory;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class Rectangle implements Shape {
  @Override
  public void draw() {
    System.out.println("Inside Rectangle::draw() method.");
  }
}
