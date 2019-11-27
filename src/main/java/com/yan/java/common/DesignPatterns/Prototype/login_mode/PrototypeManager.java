package com.yan.java.common.DesignPatterns.Prototype.login_mode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class PrototypeManager {

  private static Map<Object, YPrototype> map = new HashMap<>();

  private PrototypeManager() {

  }

  public synchronized static void setPrototype(Object key, YPrototype value) {
    map.put(key, value);
  }

  public synchronized static void remotePrototype(Object key) {
    map.remove(key);
  }

  public synchronized static YPrototype getPrototype(Object key) throws NullPointerException{
    YPrototype prototype = map.get(key);
    // 可能会得到空的原型 如果没有注册的话
    if (prototype == null) {
      throw new NullPointerException(String.format("not such key in map. [key=%s]", key));
    }
    return prototype;
  }
}
