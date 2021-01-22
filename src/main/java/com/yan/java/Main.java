package com.yan.java;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanxujiang on 2019-12-27.
 */
public class Main {
  public static void main(String[] args) {
    System.out.println(new Main().simplifyPath("/home/a/b/c/..../d/e/f"));
  }
  public String simplifyPath(String path) {
    List<String> list = getRetList(path);
    if (list.size() == 0) {
      return "/";
    }
    StringBuilder sb = new StringBuilder("");
    list.stream().forEach(str -> {
      sb.append("/");
      sb.append(str);
    });
    return sb.toString();
  }

  public List<String> getRetList(String path) {
    char[] arr = path.toCharArray();
    StringBuilder sb = new StringBuilder("");
    LinkedList<String> list = new LinkedList<>();
    int countDot = 0;

    for (int i = 0; i < arr.length; i++) {
      if (arr[i] == '/') {
        if (sb.length() != 0 && countDot == 0) {
          list.add(sb.toString());
          sb.delete(0, sb.length());
          countDot = 0;
        }
        else if (countDot == 1) {
          countDot = 0;
        }
        else if (countDot == 2) {
          countDot = 0;
          list.removeLast();
          sb.delete(0, sb.length());
        } else if (countDot >2){
          list.add(getDotStr(countDot));
          countDot = 0;
        }
        continue;
      }
      if (arr[i] == '.') {
        countDot += 1;
        if (i == arr.length - 1) {
          list.add(getDotStr(countDot));
        }
        continue;
      }
      sb.append(arr[i]);
    }
    return list;
  }

  public String getDotStr(int countDot) {
    StringBuilder sb = new StringBuilder("");
    for(int i = 0; i < countDot; i++) {
      sb.append('.');
    }
    return sb.toString();
  }
}
