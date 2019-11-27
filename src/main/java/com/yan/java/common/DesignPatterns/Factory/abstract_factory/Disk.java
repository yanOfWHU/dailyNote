package com.yan.java.common.DesignPatterns.Factory.abstract_factory;

import java.util.UUID;

/**
 * Created by yanxujiang on 2019-11-27.
 */
public class Disk implements Product {
  @Override
  public String name() {
    return "disk";
  }

  @Override
  public String id() {
    return UUID.randomUUID().toString();
  }
}
