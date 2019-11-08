package com.yan.java.common.SeniorLambdaUsage.udfInterface;

/**
 * Created by yanxujiang on 2019-10-15.
 */
public class Main {
  public static void main(String[] args) {
    StringHandler.map.put("test-key","test-value");
    StringHandler.map.put("test1-key","test1-value");

    new Main().run();
  }

  public void run(){
    StringHandler.loopHandle(this::subHandle);
  }

  /**
   * 该方法只要参数和 Handler::handle 方法对应
   * 即便有返回值，甚至方法名不同 都可以使用 lambda 表达式
   *
   * @param s
   * @return
   */
  public String subHandle(String s){
    System.out.println(s);
    return "s";
  }
}
