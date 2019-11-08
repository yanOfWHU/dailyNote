package com.yan.java.exec.flink.code.chapter_four;

import com.google.gson.Gson;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 从Kafka topic读取的数据
 * 反序列化成对象
 * 可以使用简单的SimpleStringSchema
 * 这里使用自定义的Schema
 */
public class MetricSchema implements DeserializationSchema<MetricEvent>, SerializationSchema<MetricEvent> {

  private static final Gson gson = new Gson();

  /**
   * 反序列化操作  将二进制数据 反序列化成对象
   * @param bytes
   * @return
   * @throws java.io.IOException
   */
  @Override
  public MetricEvent deserialize(byte[] bytes) throws IOException {
    return gson.fromJson(new String(bytes), MetricEvent.class);
  }

  @Override
  public boolean isEndOfStream(MetricEvent metricEvent) {
    return false;
  }

  @Override
  public byte[] serialize(MetricEvent metricEvent) {
    return gson.toJson(metricEvent).getBytes(Charset.forName("UTF-8"));
  }

  @Override
  public TypeInformation<MetricEvent> getProducedType() {
    return TypeInformation.of(MetricEvent.class);
  }
}
