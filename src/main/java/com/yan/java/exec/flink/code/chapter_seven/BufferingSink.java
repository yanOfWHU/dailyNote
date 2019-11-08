package com.yan.java.exec.flink.code.chapter_seven;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Whenever a checkpoint has to be performed, snapshotState() is called.
 * The counterpart, initializeState(),
 * is called every time the user-defined function is initialized,
 * be that when the function is first initialized or
 * be that when the function is actually recovering from an earlier checkpoint.
 * Given this, initializeState() is not only the place where different types of state are initialized,
 * but also where state recovery logic is included.
 */
public class BufferingSink implements SinkFunction<Tuple2<String,Integer>>, CheckpointedFunction {

  private final int threshold;

  private transient ListState<Tuple2<String,Integer>> checkpointedState;

  private List<Tuple2<String,Integer>> bufferedElements;

  public BufferingSink(int threshold){
    this.threshold = threshold;
    this.bufferedElements = new ArrayList<>();
  }

  @Override
  public void invoke(Tuple2<String, Integer> value, Context context) throws Exception {
    bufferedElements.add(value);
    if(bufferedElements.size() == threshold){
      for(Tuple2<String,Integer> element : bufferedElements){
        //send it to sink
      }
      bufferedElements.clear();
    }
  }

  @Override
  public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {
    checkpointedState.clear();

    checkpointedState.addAll(bufferedElements);
  }

  @Override
  public void initializeState(FunctionInitializationContext functionInitializationContext) throws Exception {
    ListStateDescriptor<Tuple2<String,Integer>> descriptor =
        new ListStateDescriptor<Tuple2<String, Integer>>(
            "buffered-elements",
            TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {}));

    checkpointedState = functionInitializationContext.getOperatorStateStore().getListState(descriptor);

    if(functionInitializationContext.isRestored()){
      for(Tuple2<String,Integer> element: checkpointedState.get()){
        bufferedElements.add(element);
      }
    }
  }
}
