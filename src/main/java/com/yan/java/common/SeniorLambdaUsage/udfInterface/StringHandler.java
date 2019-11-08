package com.yan.java.common.SeniorLambdaUsage.udfInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanxujiang on 2019-10-15.
 */
public class StringHandler {

  // 定义一个 public 静态变量，供全局使用
  public static Map<String,String> map = new HashMap<>();


  public static void loopHandle(Handler handler){
    for(Map.Entry<String,String> entry : map.entrySet()){
      String value = entry.getValue();
      handler.handle(value);
    }
  }
}
