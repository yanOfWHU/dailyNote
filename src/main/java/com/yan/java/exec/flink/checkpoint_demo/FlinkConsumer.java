package com.yan.java.exec.flink.checkpoint_demo;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FlinkConsumer {
  private final static String SERVER = "localhost:9092";
  private final static String ZK_HOST = "localhost:2181";
  private final static String GROUP = "consumer-test-group10";
  public static void main(String[] args) throws Exception{
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
    env.enableCheckpointing(10_000);
    env.setStateBackend(new FsStateBackend("file:///Users/yanxujiang/flink_file/checkpoint",false));
    env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
    env.getCheckpointConfig().setCheckpointTimeout(15_000);
    env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
    env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    env.getCheckpointConfig().setMinPauseBetweenCheckpoints(5_000);
    env.setParallelism(1);


    env.getConfig().enableSysoutLogging();

    Properties props = new Properties();
    props.setProperty("zookeeper.connect", ZK_HOST);
    props.setProperty("bootstrap.servers", SERVER);
    props.setProperty("group.id",GROUP);

    DataStream<String> source = env.addSource(
        // 必须要注明 startFromEarliest
        new FlinkKafkaConsumer010<String>("flink_topic", new SimpleStringSchema(), props).setStartFromEarliest()
    );
    /**
     * 添加state
     */
    source
      .map(data->{
        System.out.println(data);
        return data;
      })
        .map(KafkaProducerDemo.FlinkRecord::deserialization).name("map_name").uid("map_id")
        .filter(s->!s.getName().equalsIgnoreCase("C++")).name("filter_name").uid("filter_id")
        .keyBy(KafkaProducerDemo.FlinkRecord::getName)
        .process(new MainProcessFunction()).uid("state_process_id").name("state_process_name")
//        .process(new NonStateProcessFunction()).uid("non_state_process_id").name("non_state_process_name")
        .addSink(new RichSinkFunction<Tuple2<Integer, KafkaProducerDemo.FlinkRecord>>() {
          private FileWriter c_file;
          private FileWriter c_plus_file;
          private FileWriter java_file;
          private FileWriter script_file;
          private FileWriter py_file;
          Map<String,FileWriter> map = new HashMap<>();

          @Override
          public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            //第二个参数true 表示 追加模式
            c_file = new FileWriter("/Users/yanxujiang/flink_file/sink_data/c.txt", true);
            c_plus_file = new FileWriter("/Users/yanxujiang/flink_file/sink_data/c++.txt",true);
            java_file = new FileWriter("/Users/yanxujiang/flink_file/sink_data/java.txt",true);
            script_file = new FileWriter("/Users/yanxujiang/flink_file/sink_data/script.txt",true);
            py_file = new FileWriter("/Users/yanxujiang/flink_file/sink_data/py.txt",true);
            map.put("C",c_file);
            map.put("C++",c_plus_file);
            map.put("Java",java_file);
            map.put("JavaScript",script_file);
            map.put("Python",py_file);
          }

          @Override
          public void close() throws Exception {
            super.close();
            closeFile();
          }

          private void closeFile() throws IOException{
            if(null != c_plus_file){
              c_plus_file.close();
            }
            if(null != c_file){
              c_file.close();
            }
            if(null != java_file){
              java_file.close();
            }
            if(null != script_file){
              script_file.close();
            }
            if(null != py_file){
              py_file.close();
            }
          }

          @Override
          public void invoke(Tuple2<Integer, KafkaProducerDemo.FlinkRecord> value, Context context) throws Exception {
            FileWriter writer = map.get(value.f1.getName());
            writer.write(value.f0 + ":" + value.f1.toString() + "\n");
            writer.flush();
          }
        }).name("sink_name").uid("sink_id");


//    map.writeAsText("/Users/yanxujiang/flink_file/sink_data/data.txt", FileSystem.WriteMode.OVERWRITE);
    env.execute("simple flink test");
  }
}

