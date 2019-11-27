package com.yan.java;

/**
 * Created by yanxujiang on 2019-11-27.
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
