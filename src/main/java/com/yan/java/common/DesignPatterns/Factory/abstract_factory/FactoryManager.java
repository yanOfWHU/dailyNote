package com.yan.java.common.DesignPatterns.Factory.abstract_factory;

/**
 * Created by yanxujiang on 2019-11-27.
 *
 * todo getFactory(name) 通过反射创建 factory
 */
public class FactoryManager {

  public static Factory getPhoneFactory() {
    return new PhoneFactory();
  }

  public static Factory getDiskFactory() {
    return new DiskFactory();
  }

}
