package com.yan.java.exec.flink.code.chapter_seven;

import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Collections;
import java.util.List;

public class CounterSource extends RichParallelSourceFunction<Long> implements ListCheckpointed<Long> {
  //current offset for exactly once semantics
  private Long offset = 0L;

  //flog for job cancellation
  private volatile boolean isRunning = true;

  @Override
  public List snapshotState(long checkpointId, long checkpointTimestamp) throws Exception {
    return Collections.singletonList(offset);
  }

  @Override
  public void restoreState(List <Long>state) throws Exception {
    for(Long s : state){
      offset = s;
    }
  }

  @Override
  public void run(SourceContext<Long> sourceContext) throws Exception {
    final Object lock = sourceContext.getCheckpointLock();

    while (isRunning){
      // output and state update are atomic
      synchronized (lock){
        sourceContext.collect(offset);
        offset+=1;
      }
    }
  }

  @Override
  public void cancel() {
    isRunning = false;
  }
}
