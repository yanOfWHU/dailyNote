package com.yan.java.exec.flink.checkpoint_demo;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.io.FileWriter;

public class NonStateProcessFunction extends KeyedProcessFunction<String, KafkaProducerDemo.FlinkRecord, Tuple2<Integer, KafkaProducerDemo.FlinkRecord>> {
  private FileWriter writer;
  private static int count = 0;

  public NonStateProcessFunction() {
    super();
  }

  @Override
  public void open(Configuration parameters) throws Exception {
    super.open(parameters);
    writer = new FileWriter("/Users/yanxujiang/flink_file/sink_data/non-state.txt",true);
  }

  @Override
  public void close() throws Exception {
    super.close();
    writer.close();
  }

  @Override
  public void processElement(KafkaProducerDemo.FlinkRecord flinkRecord, Context context,
      Collector<Tuple2<Integer, KafkaProducerDemo.FlinkRecord>> collector) throws Exception {
    Tuple2<Integer, KafkaProducerDemo.FlinkRecord> res = new Tuple2<>();
    res.f0 = count++;
    res.f1 = flinkRecord;
    collector.collect(res);
    writer.write("count: " + count + "->value: "+ flinkRecord.toString());
    writer.flush();
  }
}
