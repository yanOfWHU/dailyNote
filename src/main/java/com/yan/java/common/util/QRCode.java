package com.yan.java.common.util;

import lombok.Getter;

/**
 * Created by yanxujiang on 2020-01-07.
 * https://www.cnblogs.com/super-yu/p/8532841.html
 */
public class QRCode {
  private static final Color BLACK = Color.BLACK;
  private static final Color WHITE = Color.WHITE;
  private static final int WIDTH = 300;
  private static final int HEIGHT = 300;
  private static String FORMAT = "png";

  public static void main(String[] args) {

  }
}

enum Color{

  BLACK(0xFF000000), WHITE(0xFFFFFFFF);

  @Getter
  private int value;

  Color(int value) {
    this.value = value;
  }
}
