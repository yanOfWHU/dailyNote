package com.yan.java.exec.flink.checkpoint_demo;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;

public class Constant {
  // 设置一个初始值
  public static final ValueStateDescriptor<Integer> count = new ValueStateDescriptor<>(
      "name_count",Integer.class, 0
  );

  public static final ValueStateDescriptor<Integer> count2 = new ValueStateDescriptor<>(
    "name_count_2",Integer.class,1000
  );

  public static final ValueStateDescriptor<RecordPojo> RECORD = new ValueStateDescriptor<>(
    "record_pojo", RecordPojo.class
  );

  public static final MapStateDescriptor<String,RecordPojo> MAP_RECORD = new MapStateDescriptor<>(
      "map_record",String.class,RecordPojo.class
  );


}
