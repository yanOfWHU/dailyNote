package com.yan.java.exec.flink.checkpoint_demo;

public class RecordPojo {
  private String id;
  private String name;
//  private Long createdTime;
  private Long updateTime;
  private int count;
//
//  public RecordPojo() {
//  }


  public RecordPojo(String id, String name, Long updateTime, int count) {
    this.id = id;
    this.name = name;
    this.updateTime = updateTime;
    this.count = count;
  }



//  public RecordPojo(String id, String name, Long createdTime, Long updateTime, int count) {
//    this.id = id;
//    this.name = name;
//    this.createdTime = createdTime;
//    this.updateTime = updateTime;
//    this.count = count;
//  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getname() {
    return name;
  }

  public void setname(String name) {
    this.name = name;
  }

//  public Long getCreatedTime() {
//    return createdTime;
//  }
//
//  public void setCreatedTime(Long createdTime) {
//    this.createdTime = createdTime;
//  }

    public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public RecordPojo plusCount(){
    this.count += 1;
    return this;
  }

  public RecordPojo updateTime(){
    this.updateTime = System.currentTimeMillis();
    return this;
  }

  @Override
  public String toString() {
    return "RecordPojo{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", updateTime=" + updateTime +
        ", count=" + count +
        '}';
  }
}
