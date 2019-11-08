package com.yan.java.exec.flink.code.chapter_five;

import com.yan.java.exec.flink.code.Student;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StudentDeSerializer implements DeserializationSchema<Student>, SerializationSchema<Student> {
  public static final long serialVersionUID = 1L;

  @Override
  public Student deserialize(byte[] bytes) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    int id = buffer.getInt(0);
    int age = buffer.getInt(4);
    int nameLength = buffer.getInt(8);
    byte[]name = new byte[nameLength];
    buffer.get(name,12,nameLength);
    String nameStr = String.valueOf(name);
    int passLength = buffer.getInt(12 + nameLength);
    byte[]pass = new byte[passLength];
    buffer.get(pass,16 + nameLength,passLength);
    String passStr = String.valueOf(pass);
    return new Student(id,nameStr,passStr,age);
  }

  @Override
  public boolean isEndOfStream(Student student) {
    return false;
  }

  @Override
  public byte[] serialize(Student student) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(128).order(ByteOrder.LITTLE_ENDIAN);
    //序列化  按照 id age name password的顺序进行序列化
    byteBuffer.putInt(0,student.getId());
    byteBuffer.putInt(4,student.getAge());
    //存入byte的大小  便于反序列化操作
    byteBuffer.putInt(8,student.getName().getBytes().length);
    byteBuffer.put(student.getName().getBytes());
    byteBuffer.putInt(8+student.getName().getBytes().length,student.getPassword().getBytes().length);
    byteBuffer.put(student.getPassword().getBytes());
    return byteBuffer.array();
  }

  @Override
  public TypeInformation<Student> getProducedType() {
    return TypeInformation.of(Student.class);
  }
}
