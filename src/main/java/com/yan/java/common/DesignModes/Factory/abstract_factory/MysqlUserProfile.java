package com.yan.java.common.DesignModes.Factory.abstract_factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class MysqlUserProfile implements IUserProfile{
  @Override
  public void insert(YUserProfile userProfile) {
    System.out.println("insert into mysql. obj=" + userProfile);
  }

  @Override
  public YUserProfile get(int id) {
    System.out.println("get userProfile by id. id=" + id);
    return null;
  }
}
