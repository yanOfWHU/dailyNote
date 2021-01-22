package com.yan.java.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by yanxujiang on 2019-12-21.
 */
public class BuilderUsage {

  public static void main(String[] args) {
    InnerBuilder ib = InnerBuilder.builder()
        .name("test")
        .age(null)
        .id("id")
        .address("beijing")
        .build();
    System.out.println(ib);
  }
  @Data
  @ToString
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class InnerBuilder implements Serializable {
    private String name;
    private String id;
    private Integer age;
    private String address;
  }
}
