package com.yan.java.common.jackson;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

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

    ObjectNode dataNode = DEFAULT_OBJECT_MAPPER.createObjectNode();
    dataNode.put("name", "yan");
    dataNode.put("age", 10);
    dataNode.put("id", (Integer) null);

    System.out.println(dataNode.toString());
    LinkedList<Integer>  list = new LinkedList<>();
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);
    list.removeLast();
    System.out.println(list);
    StringBuilder sb = new StringBuilder("");
    System.out.println(sb.length());
    sb.append("agsgdgbd");
    System.out.println(sb.toString());
    sb.delete(0, sb.length());
    System.out.println(sb.toString());
    sb.append("1rvsdvgs");
    System.out.println(sb.toString());

  }
}
