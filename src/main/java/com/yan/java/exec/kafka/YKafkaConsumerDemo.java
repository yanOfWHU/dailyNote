package com.yan.java.exec.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by yanxujiang on 2019-11-08.
 */
public class YKafkaConsumerDemo {
  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "debugbox233.sa:9092");
    props.put("group.id", "test");
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("session.timeout.ms", "30000");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    // 创建kafka consumer
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    // consumer 订阅topic
    consumer.subscribe(Arrays.asList("event_topic"));
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(100);
      for (ConsumerRecord<String, String> record : records)
        System.out.printf("offset = %d, key = %s, value = %s %n", record.offset(), record.key(), record.value());
    }
  }
}
