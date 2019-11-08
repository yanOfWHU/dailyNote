package com.yan.java.exec.flink.code.chapter_six.WaterMark;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 简单的从socket获取数据源
 * 然后事件处理类型为event time
 * 抽取水位线为 事件本身的timestamp
 */
public class Main {
  public static void main(String[] args) throws Exception{
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

    SingleOutputStreamOperator<Word>data = env.socketTextStream("local",9001)
        .map(value->{
          String []split = value.split(",");
          return new Word(split[0],Integer.parseInt(split[1]),Long.parseLong(split[2]));
        });

    data.assignTimestampsAndWatermarks(new WordPunctuatedWaterMark());

    /**
     * transformation
     */

    data.keyBy(0) //field 0 为word 按照word进行分区
        .timeWindow(Time.seconds(10)) //时间窗口为10秒
        .allowedLateness(Time.milliseconds(2)) //允许延迟2毫秒
        .sum(1)
        .print();

    env.execute("watermark demo");
  }
}
