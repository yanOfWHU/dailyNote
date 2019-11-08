package com.yan.java.exec.flink.code.chapter_seven;

import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * three arguments:
 *  1 :IN1 -> the non-broadcast stream element type
 *  2 :IN2 -> the broadcast stream element type
 *  3 :OUt -> the result type to be collect by the collector
 */
public class BroadcastFunction extends BroadcastProcessFunction< String, String, String> {
  /**
   * called when process a non-broadcast element
   * @param element non-broadcast element
   * @param readOnlyContext running context
   * @param collector result collector
   * @throws Exception
   */
  @Override
  public void processElement(String element, ReadOnlyContext readOnlyContext, Collector<String> collector) throws Exception {

  }

  /**
   * called when process a broadcast element
   * @param broadcastElement broadcast element
   * @param context running context
   * @param collector collector
   * @throws Exception
   */
  @Override
  public void processBroadcastElement(String broadcastElement, Context context, Collector<String> collector) throws Exception {

  }
}
