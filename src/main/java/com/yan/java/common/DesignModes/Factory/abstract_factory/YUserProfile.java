package com.yan.java.common.DesignModes.Factory.abstract_factory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yanxujiang on 2019-11-12.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YUserProfile {
  private int id;
  private String name;
  private String sex;
  private String phone;
  private String address;
}
