package com.yan.java.exec.flink.code.chapter_four;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaConfigUtil {
  /**
   * 创建kafka 数据源
   * @param env flink执行环境
   * @return
   * @throws IllegalAccessException
   */
  public static DataStreamSource<MetricEvent> buildSource(StreamExecutionEnvironment env) throws IllegalAccessException{
    ParameterTool parameter = (ParameterTool)env.getConfig().getGlobalJobParameters();
    String topic = parameter.getRequired(PropertiesConstants.METRICS_TOPIC);
    Long time = parameter.getLong(PropertiesConstants.CONSUMER_FROM_TIME,0L);
    return buildSource(env,topic,time);
  }

  /**
   * 创建kafka 数据源
   * @param env flink 环境
   * @param topic kafka topic
   * @param time 时间 kafka.from.time  如果为0 则默认从latest获取数据，可以设置其他值，从时间指定位置获取数据
   * @return
   */
  public static DataStreamSource<MetricEvent> buildSource(StreamExecutionEnvironment env, String topic, Long time) {
    ParameterTool parameterTool = (ParameterTool)env.getConfig().getGlobalJobParameters();
    Properties props = buildKafkaProps(parameterTool);
    FlinkKafkaConsumer010<MetricEvent>consumer = new FlinkKafkaConsumer010<MetricEvent>(
        topic,new MetricSchema(),props);

    //重制offset到time
    if(time != 0L){
      Map<KafkaTopicPartition,Long> partitionOffset = buildOffsetByTime(props,parameterTool,time);
      //从特定位置进行消费
      consumer.setStartFromSpecificOffsets(partitionOffset);
    }
    return env.addSource(consumer);
  }


  /**
   * 根据时间 获取kafka的指定的offset位置
   * @param props
   * @param parameterTool
   * @param time
   * @return
   */
  public static Map<KafkaTopicPartition, Long> buildOffsetByTime(Properties props, ParameterTool parameterTool, Long time) {
    props.setProperty("group.id","quert_time_" + time);
    //创建kafka consumer
    KafkaConsumer consumer = new KafkaConsumer(props);

    //获取 metric topic 的 partition info信息
    List<PartitionInfo> partitionInfos = consumer.partitionsFor(parameterTool.getRequired(PropertiesConstants.METRICS_TOPIC));
    Map<TopicPartition,Long> partitionLongMap = new HashMap<>();

    for(PartitionInfo partitionInfo : partitionInfos){
      partitionLongMap.put(new TopicPartition(partitionInfo.topic(),partitionInfo.partition()),time);
    }

    //通过给定的时间 search 每个partition的offset位置
    Map<TopicPartition, OffsetAndTimestamp> offsetResult = consumer.offsetsForTimes(partitionLongMap);
    Map<KafkaTopicPartition,Long> partitionOffset = new HashMap<>();
    offsetResult.forEach((key,value)->partitionOffset.put(new KafkaTopicPartition(key.topic(),key.partition()),value.offset()));

    consumer.close();
    return partitionOffset;
  }

  /**
   * 构建kafka 连接参数
   * @param parameterTool flink 参数工具
   * @return
   */
  public static Properties buildKafkaProps(ParameterTool parameterTool) {
    Properties props = parameterTool.getProperties();
    props.put("bootstrap.servers",parameterTool.get(PropertiesConstants.KAFKA_BROKERS,PropertiesConstants.DEFAULT_KAFKA_BROKERS));
    props.put("zookeeper.connect",parameterTool.get(PropertiesConstants.KAFKA_ZOOKEEPER_CONNECT,
        PropertiesConstants.DEFAULT_KAFKA_ZOOKEEPER_CONNECT));
    props.put("group.id",parameterTool.get(PropertiesConstants.KAFKA_GROUP_ID,PropertiesConstants.DEFAULT_KAFKA_GROUP_ID));
    props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    props.put("auto.offset.reset","latest");
    return props;
  }
}
