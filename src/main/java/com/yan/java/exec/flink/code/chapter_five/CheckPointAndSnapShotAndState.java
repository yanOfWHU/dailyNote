package com.yan.java.exec.flink.code.chapter_five;

import com.yan.java.exec.flink.code.Student;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * 一个文档  用于记录flink的检查点 快照达成exactly-once机制 以及状态机制
 *
 * 一.容错：
 * barrier：
 * 在数据流中，会插入一些barrier
 * 1.出现一个barrier，表示该barrier之前的数据都属于该barrier对应的snapshot，
 *    反之，该barrier之后的数据都属于下一个snapshot
 * 2.来自不同snapshot的barrier可能同时存在于数据流中，也就是说，同一时刻，可能发生多个snapshot
 * 3.当一个中间的operator，接收到barrier之后，他会发送barrier到属于该barrier的snapshot的数据流中，等到sink operator接收到barrier之后
 *    会向checkpoint coordinator确认该snapshot，直到所有的sink operator都确认了该barrier，才认为该snapshot成功
 * 4.对齐--当Operator接收到多个输入的数据流时，需要在snapshot barrier中对数据流进行对齐处理。
 *    operator从一个输入流收到barrier n(以下统称为 bn),他会暂停处理，直到其他的输入流的bn都到达该operator
 * 5.注意，接收到bn之前的数据都将被搁置处理，不会立刻推送到下一个operator，而是放在一个一个buffer中
 * 6.一旦输入流中的所有bn都到达operator，operator将会emit所有暂停的buffer数据 然后向checkpoint coordinator发送snapshot n
 * 7.而后继续来自多个stream的数据  下一个operator亦如此
 *
 * 注意：基于stream aligning 很容易实现exactly once语义，然而，这样也会给流处理带来事件处理延迟，排列对齐barrier，会暂存一部分的stream的记录
 * 到buffer中，在并行度很高的情况下 可能延迟会更加明显，通常以最迟最齐barrier的一个stream为处理buffer中缓存记录的时刻点。
 * Flink中提供了一个开关，允许用户使用Stream Aligning，如果关掉则会将exactly once变为at least once
 *
 *
 * CheckPoint 检查点
 * snapshot不仅仅对数据流做了一个状态的checkpoint，他也包含了一个Operator内部所持有的状态。这样才能在保证流处理系统失败的时候能够正常的恢复数据。
 *
 * operator状态的checkpoint包含两种
 * 1.系统状态。一个Operator进行计算处理的时候需要对数据进行缓冲，所以数据缓冲区的状态是于Operator相关联的。以窗口操作的缓冲区为例，Flink系统会
 * 收集或者聚合记录数据并且放到缓冲区中，直到该缓冲区的数据被处理完成。
 *
 * 2.另一种就是用户自定的状态。可以是函数中java对象这种最简单的变量，也可以是与函数相关的key/value状态
 * 下面是状态使用的一个简单示例
 * 参见： https://github.com/apache/flink/blob/master/flink-examples/flink-examples-streaming/src/main/java/org/apache/flink/streaming/examples/statemachine/dfa/State.java
 *           +--[a]--> W --[b]--> Y --[e]---+
 *           |                    ^         |
 *   Initial-+                    |         |
 *           |                    |         +--> (Z)-----[g]---> Terminal
 *           +--[c]--> X --[b]----+         |
 *                     |                    |
 *                     +--------[d]---------+
 */
public class CheckPointAndSnapShotAndState {
  public static void main(String[] args) {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.enableCheckpointing(2000L);

    //状态备份的方式
    final String []stateBackend = {"memory", "rocksDB", "file"};
    env.setStateBackend(new MemoryStateBackend());

    //Source
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    FlinkKafkaConsumer010<Student> kafka = new FlinkKafkaConsumer010<Student>("flink_test",new StudentDeSerializer(),props);
    kafka.setStartFromEarliest();

    env.addSource(kafka)
        .keyBy(Student::getId)
        .flatMap(new StateMachineMapper())
        .print();

  }

  private static class StateMachineMapper extends RichFlatMapFunction<Student,String> {
    /**
     * 当前key的state
     */
    private ValueState<VState> currentState;

    /**
     * 最开始 初始化state
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
      super.open(parameters);
      currentState = getRuntimeContext().getState(new ValueStateDescriptor<VState>("state",VState.class));
    }

    @Override
    public void flatMap(Student student, Collector<String> collector) throws Exception {
      //获取当前key的state
      VState state = currentState.value();

      if(state == null){
        state = VState.Initial;
      }

      VState nextState = state.transition(student);

      if(nextState == VState.Invalid){
        collector.collect("the current state resulted in an invalid transition");
      }else if(nextState.isTerminal()){
        currentState.clear();
      }
      else{
        currentState.update(nextState);
      }
    }
  }
}
