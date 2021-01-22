package inherit;

import lombok.Data;

import java.util.UUID;

/**
 * Created by yanxujiang on 2020-01-16.
 */
@Data
public class Base {
  private long time;
  private String id;

  public Base() {
    time = System.currentTimeMillis();
    id = UUID.randomUUID().toString();
  }
}
