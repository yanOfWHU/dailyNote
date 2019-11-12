package com.yan.java.common.DesignModes.Factory.abstract_factory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by yanxujiang on 2019-11-12.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YEvent {
  private int id;
  private String eventJson;
  private Date date;
}
