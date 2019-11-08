package com.yan.java.exec.flink.code.chapter_two;

import com.alibaba.fastjson.JSON;
import com.yan.java.exec.flink.code.Student;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

/**
 * 到达窗口的操作符被传递给WindowAssigner  Window Assigner 将元素分配给一个或者多个窗口 可能会创建新的窗口
 * 窗口本身是元素的标符号 如TimeWindow的开始和结束时间。
 * 注意：一个元素可以在多个窗口存在
 *
 * 每个窗口都有一个触发器Trigger 触发器决定何时计算和清除窗口
 * 先前注册的计时器超时之后，将为插入窗口的每个元素调用触发器
 * 触发器可以决定删除窗口或者丢弃其内容，或者启动并清除窗口 一个窗口可以被求值多次 取决于清除方案 是否purgeAndFire
 * 触发器Trigger触发时，可以给窗口元素列表提供可选的Evictor，evictor可以遍历窗口元素列表，并且可以决定从列表的开头删除首先进入窗口的元素
 * 然后其余分元素被赋予一个计算函数，如果没有定义evictor，触发器直接将所有窗口元素交给计算函数
 * dataStream api接受不同类型的计算函数，包括聚合函数sum min max 以及ReduceFunction，FoldFunction WindowFunction
 *
 *
 * 注意：
 * 窗口中的数据计算处理  并不就是时间到了或者数量到了就立刻计算
 * 事实上，基于事件事件处理机制，数据会在有些意想不到的情况下滞后，比如forward故障等等。
 * 对于这些情况，可以设置一些参数允许处理滞后的元素，比如允许滞后一个小时，那么实际上输出时间间隔就是要加上这个间隔时间了，
 * 或者可以设置数量到了1000立刻触发
 * ⬇
 * ⬇
 * 这时候就需要用到trigger了
 * Flink带有一些内置的触发器
 * EventTimeTrigger
 * ProcessingTimeTrigger
 * CountTrigger
 * PurgingTrigger
 *
 * Trigger接口定义了五个方法允许Trigger对不同的事件作出反应
 * onElement：每个元素进入窗口都会调用这个方法
 *    Called for every element that gets added to a pane.
 *    The result of this will determine whether the pane is evaluated to emit results.
 * onEventTime：基于事件时间处理机制，timer被触发的时候被调用
 *    Called when an event-time timer that was set using the trigger context fires.
 * onProcessingTime：基于处理时间处理机制，timer被触发的时候被调用
 *    Called when a processing-time timer that was set using the trigger context fires.
 * onMerge：有状态的触发器相关，并在它们相应的窗口合并时合并两个触发器的状态，例如使用会话窗口。
 *    Called when several windows have been merged into one window by the WindowAssigner.
 * clear：该方法主要是执行窗口的删除操作。
 *    Clears any state that the trigger might still hold for the given window. This is called when a window is purged.
 *    Timers set using Trigger.TriggerContext.registerEventTimeTimer(long) and
 *    Trigger.TriggerContext.registerProcessingTimeTimer(long)
 *    should be deleted here as well as state acquired using Trigger.TriggerContext.getPartitionedState(StateDescriptor).
 *
 * TriggerResult:
 *  CONTINUE: 什么也不做
 *  FIRE:触发计算
 *  PURGE：清除窗口中的数据
 *  PURGE_AND_FIRE:触发计算并且清除窗口中的数据
 *
 *  注意 实际上
 *  timeWindow会设置默认的EventTimeTrigger或者ProcessingTimeTrigger，根据env.TimeCharacteristic来定
 *  而countWindow也会设置默认的PurgeTrigger.of(CountTrigger.of(size)) 使用
 *  详见timeWindow方法以及countWindow方法
 *
 *  所以调用timeWindow和countWindow 实际上有两个功能
 *  设置时间/数量窗口  以及  设置默认触发器
 *  后续的.trigger()方法才可以更改触发器
 */
public class UserDefinedWindow {
  public static void main(String[] args) {
    /**
     * 使用示例
     */
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("zookeeper.connect","localhost:2181");
    props.put("group.id","test-consumer-group");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  //key 反序列化
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("auto.offset.reset","earliest");
    SingleOutputStreamOperator<Student> students = env.addSource(new FlinkKafkaConsumer010<>(
        "student",//topic name
        new SimpleStringSchema(),
        props
    )).setParallelism(1).map(string-> JSON.parseObject(string,Student.class));

    students.timeWindowAll(Time.seconds(10))
        .trigger(new CountTriggerWithTimeout<>(1000,TimeCharacteristic.ProcessingTime))
        .sum("age")
        .name("count trigger")
        .uid("yanxujiang-count-trigger");
    students.keyBy("id")
        .timeWindow(Time.seconds(10))
        .trigger(CountTrigger.of(1000));

    students.keyBy("id")
        .timeWindow(Time.seconds(10))
        .trigger(ProcessingTimeTrigger.create())
        .sum("age");

    students.countWindowAll(100)
        .trigger(CountTrigger.of(10))
        .sum("age")
        .name("count_trigger")
        .uid("test_count_trigger");
    students.keyBy("id")
        .countWindow(10)
        .trigger(CountTrigger.of(1000))
        .sum("age");
  }
}


/**
 * 带超时时间的count trigger 需要dataStream使用Time window 再使用该触发器
 * 同理
 * 可以定义带数量限制的time window 需要dataStream使用Count window
 *
 *
 * 下面的类 完全可以使用Flink的自带的CountTrigger实现
 */
class CountTriggerWithTimeout<T>extends Trigger<T, TimeWindow>{
  /**
   * 窗口最大数量
   */
  private int maxCount;

  /**
   * StreamExecutionEnvironment的时间处理类型
   * Processing Time ；Ingesting Time；Event Time
   */
  private TimeCharacteristic timeType;

  /**
   * 用于存储窗口当前数据量的状态对象
   */
  private ReducingStateDescriptor<Long> countStateDescriptor =
      new ReducingStateDescriptor<>("counter",new Sum(), LongSerializer.INSTANCE);

  public CountTriggerWithTimeout(int maxCount,TimeCharacteristic timeType){
    this.maxCount = maxCount;
    this.timeType = timeType;
  }

  private TriggerResult fireAndPurge(TimeWindow window, TriggerContext context) throws Exception{
    clear(window, context);
    return TriggerResult.FIRE_AND_PURGE;
  }
  /**
   *
   * @param element The element that arrived.
   * @param time The timestamp of the element that arrived.
   * @param timeWindow The window to which the element is being added.
   * @param triggerContext A context object that can be used to register timer callbacks.
   * @return 返回TriggerResult类型
   * @throws Exception
   */
  @Override
  public TriggerResult onElement(T element, long time, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
    ReducingState<Long> countState = triggerContext.getPartitionedState(countStateDescriptor);
    countState.add(1L);
    if(countState.get() >= maxCount){
      System.out.println("file with count" + countState.get());
      return fireAndPurge(timeWindow,triggerContext);
    }
    //如果当前元素的时间比窗口的最大时间大 说明时间窗口也到达限制
    if(time >= timeWindow.getEnd()){
      System.out.println("file with time" + time);
      return fireAndPurge(timeWindow,triggerContext);
    }
    //如果二者都不满足 则继续
    return TriggerResult.CONTINUE;
  }

  @Override
  public TriggerResult onProcessingTime(long time, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
    //如果事件时间类型不同 则不处理
    if(timeType != TimeCharacteristic.ProcessingTime){
      return TriggerResult.CONTINUE;
    }
    if(time >= timeWindow.getEnd()){
      return TriggerResult.CONTINUE;
    }
    System.out.println("fire with process time" + time);
    return fireAndPurge(timeWindow,triggerContext);
  }

  @Override
  public TriggerResult onEventTime(long time, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
    if(timeType != TimeCharacteristic.EventTime){
      return TriggerResult.CONTINUE;
    }
    if(time>= timeWindow.getEnd()){
      return TriggerResult.CONTINUE;
    }
    System.out.println("fire with event time:" + time);
    return fireAndPurge(timeWindow,triggerContext);
  }

  @Override
  public void clear(TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
    ReducingState<Long> countState = triggerContext.getPartitionedState(countStateDescriptor);
    //清除计数
    countState.clear();
  }

  private class Sum implements ReduceFunction<Long> {

    @Override
    public Long reduce(Long aLong, Long t1) throws Exception {
      return aLong + t1;
    }
  }
}
