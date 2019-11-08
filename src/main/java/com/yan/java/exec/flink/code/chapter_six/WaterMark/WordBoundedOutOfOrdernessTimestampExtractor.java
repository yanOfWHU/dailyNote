package com.yan.java.exec.flink.code.chapter_six.WaterMark;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * 边界的
 */
public class WordBoundedOutOfOrdernessTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<Word> {

  public WordBoundedOutOfOrdernessTimestampExtractor(
      Time maxOutOfOrderness) {
    super(maxOutOfOrderness);
  }

  @Override
  public long extractTimestamp(Word word) {
    return word.getTimeStamp();
  }
}
