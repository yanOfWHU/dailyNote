package com.yan.java.exec.flink.code.chapter_six.WaterMark;

public class Word {
  private String word;
  private int count;
  private long timeStamp;

  public Word(String word, int count, long timeStamp) {
    this.word = word;
    this.count = count;
    this.timeStamp = timeStamp;
  }

  public Word() {
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }
}
