package com.yan.java.common.DesignModes.Factory.abstract_factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class MysqlFactory implements IFactory {
  @Override
  public IUserProfile createUserProfile() {
    return new MysqlUserProfile();
  }

  @Override
  public IEvent createEvent() {
    return new MysqlEvent();
  }
}
