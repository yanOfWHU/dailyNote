package com.yan.java.exec.flink.code.chapter_six.WaterMark;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

public class Main2 {
  public static void main(String[] args) throws Exception{
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

    SingleOutputStreamOperator<Word> data = env.socketTextStream("local",9001)
        .map(value->{
          String []split = value.split(",");
          return new Word(split[0],Integer.parseInt(split[1]),Long.parseLong(split[2]));
        });

    data.assignTimestampsAndWatermarks(new WordBoundedOutOfOrdernessTimestampExtractor(Time.seconds(10)));



    env.execute("boundedOutOfOrderness demo");
  }
}
