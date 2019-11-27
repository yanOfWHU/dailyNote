package com.yan.java.common.DesignPatterns.Factory.abstract_factory;

import com.yan.java.common.DesignPatterns.Factory.Shape;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public interface Factory {
  Shape getShape();
  Color getColor();
  Product getProduct();
}
