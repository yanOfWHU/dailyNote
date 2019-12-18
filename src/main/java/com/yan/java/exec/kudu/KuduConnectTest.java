package com.yan.java.exec.kudu;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class KuduConnectTest {
  public static void main(String[] args) throws Exception{
    Properties props = new Properties();
    props.put("kudu_master","ip:7051");
    props.put("table_name", "table_name");
    MyKuduClient client = new MyKuduClient();
    client.init(false,props);
    //operation
    Map<String,Object> result = new HashMap<>();
    Map<String,Object> key = new HashMap<>();
    key.put("user_id",2908818850079905940L);
    key.put("_offset",621502L);
//    key.put("day",17956);
//    key.put("event_id",42);
//    key.put("sampling_group",20);
//    key.put("time",Timestamp.valueOf("2019-03-01 12:37:22.396000000").getTime());
    Set<String>fields = new HashSet<>();
    fields.add("p__city");
    fields.add("p__province");
    Long start = System.currentTimeMillis();
    System.out.println("begin:" + start);
    client.scan(key,fields,result);
    for(Map.Entry<String,Object> entry:result.entrySet()){
      System.out.println(entry.getKey() + ":" + entry.getValue());
    }
    System.out.println("cost:" + (System.currentTimeMillis()-start));
    client.close();
  }
}
