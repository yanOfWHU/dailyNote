package com.yan.java;

/**
 * Created by yanxujiang on 2019-11-27.
 * 检查异常，也称非运行时异常，非 RuntimeException 及其子类 以及 Error ，其他都是检查异常
 * 检查异常，意味着编译器要求开发者必须要处理，方法必须 throws exception 或者 try catch
 * 注意，由于 java 要求子类抛出的异常不能超过父类抛出异常的范围。
 * 通常如果父类方法不抛出异常，创建匿名子类编写逻辑时，要抛出异常，往往只能通过 try catch 实现
 * 检查异常往往都是程序语法异常，不处理，无法编译通过
 */
public class YException extends Exception {

  public YException() {
    super();
  }

  public YException(String message) {
    super(message);
  }

  public YException(String message, Throwable cause) {
    super(message, cause);
  }

  public YException(Throwable cause) {
    super(cause);
  }
}
