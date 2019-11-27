package com.yan.java;

/**
 * Created by yanxujiang on 2019-11-27.
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
