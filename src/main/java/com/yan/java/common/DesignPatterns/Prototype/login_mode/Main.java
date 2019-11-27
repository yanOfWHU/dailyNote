package com.yan.java.common.DesignPatterns.Prototype.login_mode;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class Main {
  private static String KEY = "p1";
  public static void main(String[] args) {
    YPrototype p1 = new CreatePrototpye1();
    PrototypeManager.setPrototype(KEY, p1);

    YPrototype p3 = PrototypeManager.getPrototype(KEY).clone();
    p3.setAttr("赵3");
    System.out.println("obj:" + p3);

    YPrototype p2 = new CreatePrototpye2();
    PrototypeManager.setPrototype(KEY, p2);

    YPrototype p4 = PrototypeManager.getPrototype(KEY).clone();
    p4.setAttr("钱4");
    System.out.println("obj:" + p4);

    PrototypeManager.remotePrototype(KEY);

    YPrototype p5 = PrototypeManager.getPrototype(KEY).clone();
    p5.setAttr("孙5");
    System.out.println("obj:" + p5);
  }
}
