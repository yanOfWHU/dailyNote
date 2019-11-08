package com.yan.java.common.jackson;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by yanxujiang on 2019-10-14
 *
 * 测试ObjectMapper
 */
@Getter
@Setter
@ToString
public class Conf {
  public final static ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();
  static {
    // 容忍出现未知列
    DEFAULT_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 兼容java的驼峰式命名方式
    DEFAULT_OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

  }
  private String name = "yanxujiang";
  private Integer age = 22;

  public static void main(String[] args) throws Exception {
    String s = "{\"name\":\"wanglaohu\", \"sex\":\"男\"}";
    Conf obj = DEFAULT_OBJECT_MAPPER.readValue(s, Conf.class);

    JsonNode jsonNode = DEFAULT_OBJECT_MAPPER.readTree(s);
    String name = jsonNode.get("sex").asText("女");
    String name1 = jsonNode.get("sex").toString();


    System.out.println(name);
    System.out.println(name1);

    System.out.println(obj);
  }
}
