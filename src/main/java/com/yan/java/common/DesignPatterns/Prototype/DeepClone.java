package com.yan.java.common.DesignPatterns.Prototype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by yanxujiang on 2019-11-12.
 */
public class DeepClone {
  public static void main(String[] args) throws Exception{
    System.out.println(deepClone("123"));
  }
  public static Object deepClone(Object obj) throws IOException, ClassNotFoundException {
    // 写到流中
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(obj);

    // 再从流中读出
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);

    System.out.println(ois.readObject());

    oos.flush();
    oos.writeObject("456");
    bis = new ByteArrayInputStream(bos.toByteArray());
    ois = new ObjectInputStream(bis);
    return ois.readObject();
  }
}
