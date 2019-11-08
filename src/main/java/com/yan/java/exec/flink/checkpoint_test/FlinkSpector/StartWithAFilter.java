package com.yan.java.exec.flink.checkpoint_test.FlinkSpector;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class StartWithAFilter implements FilterFunction<Tuple2<String,Integer>> {
  @Override
  public boolean filter(Tuple2<String, Integer> value) throws Exception {
    return value.f0.startsWith("a");
  }
}
