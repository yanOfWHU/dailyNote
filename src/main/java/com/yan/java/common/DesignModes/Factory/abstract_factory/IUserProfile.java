package com.yan.java.common.DesignModes.Factory.abstract_factory;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public interface IUserProfile {
  void insert(YUserProfile userProfile);
  YUserProfile get(int id);
}
