package com.yan.java.common.DesignModes.Factory.abstract_factory;

import java.util.Date;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class Main {
  public static void main(String[] args) {
    YUserProfile userProfile = new YUserProfile(1, "张三", "man", "12345", "beijing");
    YEvent event = new YEvent(1,"{}", new Date());

    IFactory factory = new MysqlFactory();

    // 获取两个数据表的操作对象
    IUserProfile userProfileOperation = factory.createUserProfile();
    IEvent eventOperation = factory.createEvent();

    userProfileOperation.insert(userProfile);
    userProfileOperation.get(1);

    eventOperation.insert(event);
    eventOperation.get(1);

  }
}
