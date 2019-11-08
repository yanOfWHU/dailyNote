package com.yan.java.exec.flink.code.chapter_three;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

public class KafkaSource {
  public static void main(String[] args)throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("zookeeper.connect", "localhost:2181");
    props.put("group.id","test-consumer-group");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  //key 反序列化
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("auto.offset.reset","earliest");


    DataStreamSource<String> dataStreamSource = env.addSource(new FlinkKafkaConsumer010<String>(
        "flink_topic",
        new SimpleStringSchema(),//String序列化 将读取到的内容 序列化为String
        props
    )).setParallelism(1);

    dataStreamSource.keyBy(new KeySelector<String, Object>() {
      @Override
      public Object getKey(String s) throws Exception {
        return s.length();
      }
    });
    dataStreamSource.print();

    env.execute("Flink add data source");
  }
}
