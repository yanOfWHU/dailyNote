package com.yan.java.exec.flink.code.chapter_six.WaterMark;

import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;


/**
 * 由元素本身触发的watermark  比如Work element本身有timestamp
 */
public class WordPunctuatedWaterMark implements AssignerWithPunctuatedWatermarks<Word> {


  /**
   * 核查当前的watermark 并且获取下一个watermark
   * @param word 当前的Word Element对象
   * @param extractedTimestamp 抽取的timestamp
   * @return
   */
  @Nullable
  @Override
  public Watermark checkAndGetNextWatermark(Word word, long extractedTimestamp) {
    return extractedTimestamp % 3 == 0 ? new Watermark(extractedTimestamp):null;
  }

  /**
   * 从Element中抽取timeStamp
   * 直接从element中抽取其属性timestamp即可s
   * @param word element对象
   * @param elementPrevTimestamp 当前元素的上一个的时间戳
   * @return
   */
  @Override
  public long extractTimestamp(Word word, long elementPrevTimestamp) {
    return word.getTimeStamp();
  }
}
