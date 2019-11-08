package com.yan.java.exec.flink.checkpoint_demo;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * 获取kafka中event_topic的信息
 * created by yanxujiang
 * created on : 7/22/19
 */
public class KafkaConsumerDemo {
  public static void main(String[] args) throws Exception{
    // create the flink environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    // set the deal way of processing the stream data
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
    // in order to make the result to print on the console sequentially ,set the parallelism 1
    env.setParallelism(1);
    // get the kafka DataStream
    DataStream<String> event_topic_stream = getStream(env);
    System.out.println("succeed to connect to kafka ~~~");
    // use operator to deal with the data stream
    event_topic_stream.process(new EventTopicParseFunction())
        //抽取时间 生成waterMark
//        .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<EventTopic>(){
//          @Override
//          public long extractAscendingTimestamp(EventTopic eventTopic) {
//            return eventTopic.time ;//* 1000;
//          }
//        })
        .keyBy("distinct_id")
        .timeWindow(Time.minutes(10))//单变量  使用滚动窗口 窗口大小为1天
        .process(new TestEventWindowedTimeFunction())
        .print();
        //.reduce(new ReduceTopicFunction());
//        .name("event_topic_parse")
//        .uid("event_topic_parse_id")
//        //.startNewChain().shuffle()
//        .keyBy("distinct_id")
//        .timeWindow(Time.seconds(60))

        //使用计量窗口
//        .countWindow(3)//设置为数量窗口
//        .process(new TestEventWindowedCountFunction())
//        .print();

    env.execute("subscribe the kafka msg system");
  }

  /**
   *  get the kafka data stream
   * @param env
   * @return
   */
  private static DataStream<String> getStream(StreamExecutionEnvironment env) {
    Properties props = new Properties();
    // set the url of kafka cluster broker list,using "," to separate the url if there exist more than 1 broker
    props.setProperty("bootstrap.servers","debugbox438.sa:9092");
    // create the kafka consumer in flink
    FlinkKafkaConsumer010<String> consumer =
        new FlinkKafkaConsumer010("event_topic",new SimpleStringSchema(),props);
    //consumer.setStartFromLatest();
    consumer.setStartFromEarliest();   // 设置 offset 为最旧
    System.out.println("try to connect to kafka ... ");
    return env.addSource(consumer)
        .name("event_topic_flink_test").uid("test_flink_kafka");
  }



  /**
   * 未使用
   * keyBy之后的自定义聚合操作   flink有自带的max min 等操作
   */
  public static class CountAgg implements AggregateFunction<EventTopic, Long, Long> {
    // 初始化计数器
    @Override
    public Long createAccumulator() {
      return 0L;
    }

    @Override
    public Long add(EventTopic eventTopic, Long acc) {
      return acc + 1;
    }

    @Override
    public Long getResult(Long acc) {
      return acc;
    }

    @Override
    public Long merge(Long acc1, Long acc2) {
      return acc1 + acc2;
    }
  }


  /**
   *  dataStream 将simpleStringSchema模式获取到的string 转化为 EventTopic对象
   */
  private static class EventTopicParseFunction extends ProcessFunction<String,EventTopic> {
    @Override
    public void processElement(String s, Context context, Collector<EventTopic> collector) throws Exception {
      // the value s is the kafka stream data content
      System.out.println("parse the kafka stream data");
      try{
        EventTopic et = new ObjectMapper().readValue(s,EventTopic.class);
        collector.collect(et);
        System.out.println(et.toString());
        //System.out.println(et);
      }catch (Exception e){
        System.out.println("error occurs");
        throw e;
      }
    }
  }



  /**
   * reduce event_topic操作
   * @Deprecated 已经不推荐使用
   * 使用Aggregation Function替代
   */
  private static class ReduceTopicFunction implements org.apache.flink.api.common.functions.ReduceFunction<EventTopic> {
    /**
     * reduce操作 将数据分组之后，进行reduce逻辑运算操作
     * @param eventTopic
     * @param t1
     * @return
     * @throws Exception
     */
    @Override
    public EventTopic reduce(EventTopic eventTopic, EventTopic t1) throws Exception {
      //将两个topic合并为一个  可以进行一系列的逻辑运算
      return null;
    }
  }


  /**
   * 计量窗口 处理函数类
   */
  private static class TestEventWindowedCountFunction extends
      ProcessWindowFunction<EventTopic,Object, Tuple, GlobalWindow> {

    @Override
    public void process(Tuple tuple, Context context, Iterable<EventTopic> topics, Collector<Object> collector)
        throws Exception {
      String distinct_id = tuple.getField(0);
      System.out.println("================================");
      System.out.println("distinct_id:  " + distinct_id);
      for(EventTopic topic : topics){
        System.out.println(topic.toString());
      }
    }

  }


  /**
   * 时间窗口 处理函数类
   */
  private static class TestEventWindowedTimeFunction extends
      ProcessWindowFunction<EventTopic,Object, Tuple, TimeWindow> {
    /**
     * 处理对象是单个时间窗口的所有数据
     * @param tuple 每个数据的key 可以是单个tuple，
     * @param context
     * @param topics 数据data的迭代器
     * @param collector 处理之后的流收集器
     * @throws Exception
     */
    @Override
    public void process(Tuple tuple, Context context, Iterable<EventTopic> topics, Collector<Object> collector)
        throws Exception {
      String distinct_id = tuple.getField(0);
      System.out.println("================================");
      System.out.println("distinct_id:  " + distinct_id);
      for(EventTopic topic : topics){
        System.out.println(topic.toString());
      }
    }
  }
}
