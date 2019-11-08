package com.yan.java.exec.flink.code.chapter_four;

import java.util.Map;

public class MetricEvent {
  private String name;

  private Long timestamp;

  private Map<String,Object> fields;

  private Map<String,String> tags;

  public MetricEvent() {
  }

  public MetricEvent(String name, Long timestamp, Map<String, Object> fields,
      Map<String, String> tags) {
    this.name = name;
    this.timestamp = timestamp;
    this.fields = fields;
    this.tags = tags;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public void setFields(Map<String, Object> fields) {
    this.fields = fields;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }
}
