package com.yan.java.exec.flink.checkpoint_test.FlinkSpector;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCountFlatMap implements FlatMapFunction<String, Tuple2<String,Integer>> {
  @Override
  public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
    String[] arr = value.split("\\s");
    for(String s : arr){
      out.collect(Tuple2.of(s,1));
    }
  }
}
