package com.yan.java.common.DesignModes.Factory.simple_factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class YMathOperationFactory {

  public static YOperation createOperation(String operand) {
    YOperation operation = null;
    switch (operand) {
      case "+":
        operation = new YAdd();
        break;
      case "-":
        operation = new YSub();
        break;
      case "*":
        operation = new YMul();
        break;
      case "/":
        operation = new YDiv();
        break;
    }
    return operation;
  }

  static class YAdd implements YOperation {

    @Override
    public double getRet(double d1, double d2) throws Exception {
      return d1 + d2;
    }
  }
  static class YSub implements YOperation {

    @Override
    public double getRet(double d1, double d2) throws Exception {
      return d1 - d2;
    }
  }
  static class YMul implements YOperation {

    @Override
    public double getRet(double d1, double d2) throws Exception {
      return d1 * d2;
    }
  }
  static class YDiv implements YOperation {

    @Override
    public double getRet(double d1, double d2) throws Exception {
      if (d2 == 0){
        throw new Exception("divider cannot be zero");
      }
      return d1 / d2;
    }
  }
}

