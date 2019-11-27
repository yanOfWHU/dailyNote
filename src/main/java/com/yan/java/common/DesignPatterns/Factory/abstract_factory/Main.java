package com.yan.java.common.DesignPatterns.Factory.abstract_factory;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class Main {
  public static void main(String[] args) {
    Factory diskFactory = FactoryManager.getDiskFactory();
    diskFactory.getColor().paint();
    System.out.println(diskFactory.getProduct().id());
    System.out.println(diskFactory.getProduct().name());
    diskFactory.getShape().draw();

    Factory phoneFactory = FactoryManager.getPhoneFactory();
    phoneFactory.getColor().paint();
    phoneFactory.getShape().draw();
    System.out.println(phoneFactory.getProduct().id());
    System.out.println(phoneFactory.getProduct().name());
  }
}
