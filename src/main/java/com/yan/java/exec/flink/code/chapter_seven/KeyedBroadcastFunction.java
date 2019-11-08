package com.yan.java.exec.flink.code.chapter_seven;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.List;

/**
 * four argument types
 * 1: Key -> the type of key of the keyedStream
 * 2: IN1 -> the non-broadcast stream element type
 * 3: IN2 -> the broadcast stream element type
 * 4: RES -> the type of result to be collected
 *
 *
 * Three type context
 * 1.ReadOnlyContext in the processElement() method gives access to Flink's underlying timer service, which allows to
 * register event &| processing time timers. When a timer fires, the OnTimer() is invoked with an OnTimerContext.
 *
 * 2.Context in the processBroadcastElement() method contains the method
 * applyToKeyedState(StateDescriptor<S ,VS> stateDescriptor,KeyedStateFunction<KS,S>function).
 * This allows to register a KeyedStateFunction to be applied to all state of all keys associated with the provided stateDescriptor
 */
public class KeyedBroadcastFunction extends KeyedBroadcastProcessFunction<String,String,String,String> {


  private final MapStateDescriptor<String, List<String>> itemsStateDescriptor =
      new MapStateDescriptor<>(
          "items",
          BasicTypeInfo.STRING_TYPE_INFO,
          TypeInformation.of(new TypeHint<List<String>>() {})
      );

  private final MapStateDescriptor<String,String> rulesStateDescriptor =
      new MapStateDescriptor<>(
          "rule",
          BasicTypeInfo.STRING_TYPE_INFO,
          TypeInformation.of(new TypeHint<String>(){})
      );


  @Override
  public void processElement(String s, ReadOnlyContext readOnlyContext, Collector<String> collector) throws Exception {

  }

  @Override
  public void processBroadcastElement(String s, Context context, Collector<String> collector) throws Exception {
    //put key -> value
    context.getBroadcastState(rulesStateDescriptor).put(s,s);
  }

  @Override
  public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
    super.onTimer(timestamp, ctx, out);
  }
}
