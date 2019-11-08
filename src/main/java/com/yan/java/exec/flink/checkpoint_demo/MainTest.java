package com.yan.java.exec.flink.checkpoint_demo;

public class MainTest {
  public static void main(String[] args) throws Exception{
    String res = "FlinkRecord{time=1568172588283, id='191a7951-cc0c-4580-860a-e7759d21757b', name='JavaScript'}";
    KafkaProducerDemo.FlinkRecord record = KafkaProducerDemo.FlinkRecord.deserialization(res);

    System.out.println(record);
  }
}
