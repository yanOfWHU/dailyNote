package com.yan.java.exec.flink.code.chapter_six.WaterMark;

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

/**
 * 周期性的watermark
 * 定期的水位线
 */
public class WordPeriodicWatermark implements AssignerWithPeriodicWatermarks<Word> {

  private long currentTimestamp = Long.MIN_VALUE;

  /**
   * 获取当前的watermark
   * @return
   */
  @Nullable
  @Override
  public Watermark getCurrentWatermark() {
    long maxTimeLag = 5000;
    return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE:currentTimestamp - maxTimeLag);
  }

  /**
   * 提取时间戳
   * @param word 提取的对象
   * @param prevElementTimestamp 先前元素的时间戳
   * @return
   */
  @Override
  public long extractTimestamp(Word word, long prevElementTimestamp) {
    this.currentTimestamp = word.getTimeStamp();
    return word.getTimeStamp();
  }
}
