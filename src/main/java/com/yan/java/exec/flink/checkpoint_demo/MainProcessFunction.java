package com.yan.java.exec.flink.checkpoint_demo;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.io.FileWriter;
import java.util.UUID;

public class MainProcessFunction extends KeyedProcessFunction<String, KafkaProducerDemo.FlinkRecord, Tuple2<Integer, KafkaProducerDemo.FlinkRecord>> {

  private transient ValueState<Integer> intState;

  private transient ValueState<Integer> int2State;

  private transient ValueState<RecordPojo> recordState;

//  private transient MapState<String,RecordPojo> mapState;

  private FileWriter writer;
//  private FileWriter mapWriter;

  @Override
  public void open(Configuration parameters) throws Exception {
    super.open(parameters);
    initState();
    writer = new FileWriter("/Users/yanxujiang/flink_file/sink_data/state.txt",true);
//    mapWriter = new FileWriter("/Users/yanxujiang/flink_file/sink_data/mapState.txt",true);
//    mapWriter.write("mapState:\n");
//    mapWriter.flush();
  }

  private void initState() {
    intState = getRuntimeContext().getState(Constant.count);
    int2State = getRuntimeContext().getState(Constant.count2);
    recordState = getRuntimeContext().getState(Constant.RECORD);
//    mapState = getRuntimeContext().getMapState(Constant.MAP_RECORD);
  }




  @Override
  public void close() throws Exception {
    super.close();
    if(null != writer){
      writer.close();
    }
//    if(null != mapWriter){
//      mapWriter.close();
//    }
  }

  @Override
  public void processElement(KafkaProducerDemo.FlinkRecord flinkRecord, Context context,
      Collector<Tuple2<Integer, KafkaProducerDemo.FlinkRecord>> collector) throws Exception {
    if(null == recordState.value()){
      recordState.update(
          new RecordPojo(UUID.randomUUID().toString(),"recordName",/*System.currentTimeMillis(),*/System.currentTimeMillis(),0)
      );
    }
//    putOrSetMapState(flinkRecord);

    //由于后面update的错误set值，目前intState和Int2State值一致
    int value = intState.value();
    int value2 = int2State.value();
    Tuple2<Integer, KafkaProducerDemo.FlinkRecord> res = Tuple2.of(value2,flinkRecord);
    collector.collect(res);
    writer.write(flinkRecord.getName() + "###" +  value + "###"  +  value2 + "#####" + recordState.value().toString() + "\n");

    //更新值
    intState.update(value + 1);
    int2State.update(value2 + 1);
    recordState.value().plusCount();

    writer.flush();
//    mapWriter.flush();
  }

//  private void putOrSetMapState(KafkaProducerDemo.FlinkRecord flinkRecord) throws Exception {
//    mapStatePutIfAbsent(flinkRecord);
//    mapState.get(flinkRecord.getName()).plusCount().updateTime();
//    mapWriter.write(String.format("key:%s, value:%s\n",flinkRecord.getName(),mapState.get(flinkRecord.getName())));
//  }

//  private void mapStatePutIfAbsent(KafkaProducerDemo.FlinkRecord flinkRecord) throws Exception{
//    if(mapState.contains(flinkRecord.getName())){
//      return;
//    }
//    mapState.put(flinkRecord.getName(),new RecordPojo(UUID.randomUUID().toString(),"recordName",/*System.currentTimeMillis(),*/System.currentTimeMillis(),0));
//  }
}
