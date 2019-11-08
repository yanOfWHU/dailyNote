package com.yan.java.exec.flink.checkpoint_demo;

import java.util.ArrayList;
import java.util.Map;

public class EventTopic {
  public String type;
  public Long time;
  public String original_id;
  public String distinct_id;
  public Map properties;
  public Map lib;
  public String event;
  public String map_id;
  public String user_id;
  public Long recv_time;
  public Map extractor;
  public int project_id;
  public String project;
  public int ver;
  public String ngx_ip;
  public Long process_time;
  public Boolean raw_is_login_id;
  public ArrayList<String> dtk;
  public Boolean is_login_id;
  public Boolean time_free;

  @Override
  public String toString() {
    return "EventTopic{" +
        "type='" + type + '\'' +
        ", time=" + time +
        ", original_id='" + original_id + '\'' +
        ", distinct_id='" + distinct_id + '\'' +
        ", properties=" + properties +
        ", lib=" + lib +
        ", event='" + event + '\'' +
        ", map_id='" + map_id + '\'' +
        ", user_id='" + user_id + '\'' +
        ", recv_time=" + recv_time +
        ", extractor=" + extractor +
        ", project_id=" + project_id +
        ", project='" + project + '\'' +
        ", ver=" + ver +
        ", ngx_ip='" + ngx_ip + '\'' +
        ", process_time=" + process_time +
        ", raw_is_login_id=" + raw_is_login_id +
        ", dtk=" + dtk +
        ", is_login_id=" + is_login_id +
        ", time_free=" + time_free +
        '}';
  }
}

