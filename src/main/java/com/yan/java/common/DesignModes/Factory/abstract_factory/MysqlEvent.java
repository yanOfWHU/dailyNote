package com.yan.java.common.DesignModes.Factory.abstract_factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class MysqlEvent implements IEvent{
  @Override
  public void insert(YEvent event) {
    System.out.println("insert into mysql. obj:" + event);
  }

  @Override
  public YEvent get(int id) {
    System.out.println("get event by id. id=" + id);
    return null;
  }
}
