package com.yan.java.exec.flink.checkpoint_demo;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * kafka producer demo
 * 前提：在kafka中新建一个topic flink_test
 * partition以及replicas都设置为1
 */
public class KafkaProducerDemo {
  public static void main(String[] args) throws Exception{
    //设置flink环境
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    //本地的数据  为了体现真实的数据流的效果  使用代码 while循环产生无限的数据
    //发送二十条
    DataStream<String> data = env.addSource(new MyInfiniteSource(20));
    //设置并行度为1
    env.setParallelism(1);

    Properties props = new Properties();
    props.setProperty("bootstrap.servers","localhost:9092");
    FlinkKafkaProducer010<String>producer010 = new FlinkKafkaProducer010<String>("flink_topic",new SimpleStringSchema(),props);

    data.addSink(producer010);

    env.execute();
  }

  public static class MyInfiniteSource implements SourceFunction<String>{
    private boolean isRunning = true;
    private int maxCountN;
    private static Integer totalCount = 0;
    private static final Object key = new Object();
    private static Map<Integer,String> bookMap = new HashMap<>();
    static {
      bookMap.put(0,"JavaScript");
      bookMap.put(1,"C++");
      bookMap.put(2,"Java");
      bookMap.put(3,"C");
      bookMap.put(4,"Python");
    }
    public MyInfiniteSource(){
      maxCountN = Integer.MAX_VALUE;
    }

    public MyInfiniteSource(int count){
      maxCountN = count;
    }

    /**
     * 产生数据的的run 函数
     * @param sourceContext
     * @throws Exception
     */
    @Override
    public void run(SourceContext sourceContext) throws Exception {
      //使用多线程
      ThreadPoolExecutor executor = new ThreadPoolExecutor(2,10,
          1000, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(5));
      while (isRunning && totalCount < maxCountN){
        Random random = new Random(System.currentTimeMillis());//set the seed
        String name;
        name = bookMap.get(random.nextInt(5));
        sourceContext.collect(new FlinkRecord(System.currentTimeMillis(),UUID.randomUUID().toString(),name).toString());
        totalCount++;
        System.out.println("Send Flink record :" + totalCount);
//        synchronized (key){
//          if(totalCount >= maxCountN){
//            // 关闭线程池
//            executor.shutdown();
//            cancel();
//            return;
//          }
//          executor.execute(()->{
//            Random random = new Random(System.currentTimeMillis());//set the seed
//            String name;
//            name = bookMap.get(random.nextInt(5));
//            sourceContext.collect(new FlinkRecord(System.currentTimeMillis(),UUID.randomUUID().toString(),name).toString());
//            totalCount++;
//          });
//        }
        // 每 1秒发送1条数据
        Thread.sleep(1_000);
      }
    }

    /**
     * 数据产生截止的run函数
     */
    @Override
    public void cancel() {
      isRunning = false;
      System.out.println("producer end sending msg && up to now, it has sent " + totalCount + " records");
    }
  }

  public static class FlinkRecord implements Serializable {
    private long time;//record 的产生时间 时间戳
    private String id;//uuid
    private String name;

    public FlinkRecord(){

    }

    public FlinkRecord(long time, String id, String name) {
      this.time = time;
      this.id = id;
      this.name = name;
    }

    public long getTime() {
      return time;
    }

    public void setTime(long time) {
      this.time = time;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return "FlinkRecord{" +
          "time=" + time +
          ", id='" + id + '\'' +
          ", name='" + name + '\'' +
          '}';
    }


    //"FlinkRecord{time=1568172588283, id='191a7951-cc0c-4580-860a-e7759d21757b', name='JavaScript'}"
    public static FlinkRecord deserialization(String str) throws IOException {
      FlinkRecord res = new FlinkRecord();
      int index;
      int temp_index;
      index = str.indexOf("=");
      temp_index = str.indexOf(",",index);
      res.setTime(Long.parseLong(str.substring(index+1,temp_index)));

      index = str.indexOf("'", temp_index);
      temp_index = str.indexOf("'", index+1);
      res.setId(str.substring(index+1,temp_index));

      index = str.indexOf("'", temp_index+1);
      temp_index = str.indexOf("'", index+1);
      res.setName(str.substring(index+1, temp_index));
      return res;
    }
  }
}
