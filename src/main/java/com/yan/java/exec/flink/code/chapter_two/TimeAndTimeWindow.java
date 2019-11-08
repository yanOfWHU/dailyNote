package com.yan.java.exec.flink.code.chapter_two;

import com.alibaba.fastjson.JSON;
import com.yan.java.exec.flink.code.Student;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

public class TimeAndTimeWindow {
  public static void main(String[] args) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    /**
     * 设立flink处理事件事件类型
     * Event Time;
     * Ingest Time;
     * Processing Time
     * 其中Processing Time是最简单的机制
     * EventTime需要事件本身提供时间戳信息
     */
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("zookeeper.connect","localhost:2181");
    props.put("group.id","test-consumer-group");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  //key 反序列化
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("auto.offset.reset","earliest");
    SingleOutputStreamOperator<Student> students = env.addSource(new FlinkKafkaConsumer010<>(
        "student",//topic name
        new SimpleStringSchema(),
        props
    )).setParallelism(1).map(string-> JSON.parseObject(string,Student.class));


    /**
     * TimeWindow 以及 CountWindow
     *
     */
    //滚动窗口  只有一个时间单位  每5秒一个滚动窗口
    students.keyBy("age")
        .timeWindow(Time.seconds(5))
        .min("id")
        .print();
    // 滑动窗口  大小是5秒  每一秒滑动一次  第二个参数不能比第一个大
    students.keyBy("age")
        .timeWindow(Time.seconds(5),Time.seconds(1))
        .min("id")
        .print();

    // count 滚动窗口
    students.keyBy("age")
        .countWindow(10)
        .min("id")
        .print();
    //count 滑动窗口
    students.keyBy("age")
        .countWindow(10,5)
        .min("id")
        .print();


  }
}
