package com.yan.java.common.DesignModes.Factory.factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class Main {
  public static void main(String[] args) throws Exception {
    Factory addFactory = (Factory) Class.forName("com.yan.java.common.DesignModes.Factory.factory.AddFactory").newInstance();
    Factory subFactory = (Factory) Class.forName("com.yan.java.common.DesignModes.Factory.factory.SubFactory").newInstance();
    Factory mulFactory = (Factory) Class.forName("com.yan.java.common.DesignModes.Factory.factory.MulFactory").newInstance();
    Factory divFactory = (Factory) Class.forName("com.yan.java.common.DesignModes.Factory.factory.DivFactory").newInstance();

    YOperation add = addFactory.createOperation();
    YOperation sub = subFactory.createOperation();
    YOperation mul = mulFactory.createOperation();
    YOperation div = divFactory.createOperation();

    System.out.println(add.getRet(1 ,1));
    System.out.println(sub.getRet(1, 1));
    System.out.println(mul.getRet(1 ,1));
    System.out.println(div.getRet(1, 1));
  }
}
