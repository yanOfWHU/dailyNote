package com.yan.java;

/**
 * Created by yanxujiang on 2019-11-27.
 * 非检查异常,也称运行时异常， RuntimeException 及其子类 以及 Error
 *
 * 常常是代码中出现逻辑问题，抛出非检查异常
 * 如 NullPointerException IndexOutOfBoundException
 */
public class YRuntimeException extends RuntimeException {
  public YRuntimeException() {
    super();
  }

  public YRuntimeException(String message) {
    super(message);
  }

  public YRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public YRuntimeException(Throwable cause) {
    super(cause);
  }
}
