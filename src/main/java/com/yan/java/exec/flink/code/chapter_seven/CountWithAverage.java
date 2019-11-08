package com.yan.java.exec.flink.code.chapter_seven;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class CountWithAverage extends RichFlatMapFunction<Tuple2<Long,Long>, Tuple2<Long,Long>> {
  public static void main(String[] args) throws Exception{
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    env.fromElements(Tuple2.of(1L,3L), Tuple2.of(1L,5L),Tuple2.of(1L,7L), Tuple2.of(1L,4L),Tuple2.of(1L,2L))
        .keyBy(0)
        .flatMap(new CountWithAverage())
        .print();

    env.execute("StatefulOperator");
  }
  private transient ValueState<Tuple2<Long,Long>> sum;

  @Override
  public void flatMap(Tuple2<Long, Long> input, Collector<Tuple2<Long,Long>> out) throws Exception {
    //access the state value
    Tuple2<Long, Long> currentSum = sum.value();

    //update the count
    currentSum.f0 += 1;

    //add the second field of the input value
    currentSum.f1 += input.f1;

    //update sum
    sum.update(currentSum);

    //if the count reached 2 ,emit the average and clear the state

    if(currentSum.f0 >=2){
      out.collect(new Tuple2<>(input.f0, currentSum.f1/currentSum.f0));
      sum.clear();
    }

  }

  @Override
  public void open(Configuration config){
    ValueStateDescriptor<Tuple2<Long,Long>> descriptor =
        new ValueStateDescriptor<>(
            "average", // the state name
            TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {}), // type information
            Tuple2.of(0L,0L)
        );
    // set the stateDescriptor the time-to-live
    //setTTL(descriptor);
    sum = getRuntimeContext().getState(descriptor);
  }

  private void setTTL(ValueStateDescriptor<Tuple2<Long, Long>> descriptor) {
    StateTtlConfig ttlConfig = StateTtlConfig
        // mandatory, it is the ttl value
        .newBuilder(Time.seconds(5))
        //StateTtlConfig.UpdateType:
        //OnCreateAndWrite: refresh only on creation and write access
        //OnReadAndWrite: refresh also on read access
        .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
        //StateTtlConfig.StateVisibility:
        //NeverReturnExpired: expired value is never returned
        //ReturnExpiredNotCleanedUp: returned if still available
        .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
        .build();

    //cleanup strategy: 4 types  ,choose one of them when running
    StateTtlConfig.newBuilder(Time.seconds(1))
        .cleanupFullSnapshot()
        .cleanupInBackground()
        .cleanupIncrementally(10,true)
        .cleanupInRocksdbCompactFilter(1000)//queryTimeAfterNumEntries
        .build();
    descriptor.enableTimeToLive(ttlConfig);
  }
}

