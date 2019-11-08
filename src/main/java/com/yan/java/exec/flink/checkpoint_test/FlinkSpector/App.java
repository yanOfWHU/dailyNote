package com.yan.java.exec.flink.checkpoint_test.FlinkSpector;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.concurrent.TimeUnit;

public class App {
  public static void main(String[] args) throws Exception{
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream<String> text = env.socketTextStream("localhost", 9999);

    DataStream<Tuple2<String,Integer>> words = text.flatMap(new WordCountFlatMap());

    DataStream<Tuple2<String,Integer>> aWords = words.filter(new StartWithAFilter());

    DataStream<Tuple2<String,Integer>> counts =
        aWords.keyBy(0)
        .timeWindow(Time.of(2, TimeUnit.SECONDS))
        .sum(1);

    counts.print();

    env.execute("WordCount Example");
  }
}
