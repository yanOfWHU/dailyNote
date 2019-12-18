package com.yan.java.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanxujiang on 2019-12-03.
 */
public class MainTest {
  public static void main(String[] args) {
    Map<String,String> map = new HashMap<>();
    map.put("1", null);
    System.out.println(map.get("2"));
    System.out.println(map.get("1"));
    System.out.println(map.get("1") == map.get("2"));
    System.out.println((String)null);
  }
}
