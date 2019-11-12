package com.yan.java.common.DesignModes.Factory.simple_factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class Main {
  public static void main(String[] args) throws Exception{
    YOperation add = YMathOperationFactory.createOperation("+");
    YOperation sub = YMathOperationFactory.createOperation("-");
    YOperation mul = YMathOperationFactory.createOperation("*");
    YOperation div = YMathOperationFactory.createOperation("/");

    System.out.println(add.getRet(1, 1));
    System.out.println(sub.getRet(1, 1));
    System.out.println(mul.getRet(1, 1));
    System.out.println(div.getRet(1, 1));
  }
}
