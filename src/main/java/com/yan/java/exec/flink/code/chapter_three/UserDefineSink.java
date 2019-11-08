package com.yan.java.exec.flink.code.chapter_three;

import com.alibaba.fastjson.JSON;
import com.yan.java.exec.flink.code.Student;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Properties;

/**
 * 用户自定义Sink方法
 */
public class UserDefineSink {
  public static final String broker_list = "localhost:9092";
  public static final String topic = "flink_topic";
  public static void main(String[] args)throws Exception {
    //插入数据到Kafka
    new Thread(()-> {
      try {
        writeToKafka();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();


    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //从kafka里面获取数据 并且sink数据到mysql
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("zookeeper.connect","localhost:2181");
    props.put("group.id","test-consumer-group");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  //key 反序列化
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("auto.offset.reset","earliest");

    SingleOutputStreamOperator<Student> students = env.addSource(new FlinkKafkaConsumer010<>(
        "student",//topic name
        new SimpleStringSchema(),
        props
    )).setParallelism(1).map(string->JSON.parseObject(string,Student.class));

    students.addSink(new SinkToMysql());

    env.execute("Flink user defined sink operation");
  }

  static class SinkToMysql extends RichSinkFunction<Student>{
    PreparedStatement ps;
    private Connection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
      super.open(parameters);
      connection = getConnection();
      String sql = "insert into student(id,name,password,age) values(?,?,?,?)";
      ps = this.connection.prepareStatement(sql);
    }

    private Connection getConnection() {
      Connection con = null;
      try{
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useUnicode=true",
            "root",
            "pwd");
      }catch (Exception e){
        System.out.println("----mysql connection error----" + "\n" + e.getMessage());
      }
      return con;
    }

    @Override
    public void close() throws Exception {
      super.close();
      if(connection!=null){
        connection.close();
      }
      if(ps !=null){
        ps.close();
      }
    }

    /**
     * 每次数据的sink都要调用invoke方法
     * @param value sink的Student值
     * @param context sink的上下文
     * @throws Exception
     */
    @Override
    public void invoke(Student value, Context context) throws Exception {
      ps.setInt(1,value.getId());
      ps.setString(2,value.getName());
      ps.setString(3,value.getPassword());
      ps.setInt(4,value.getAge());
      ps.executeUpdate();
    }
  }
  /**
   * 数据写入kafka的工具
   * @throws InterruptedException
   */
  public static void writeToKafka() throws InterruptedException{
    Properties props = new Properties();
    props.put("bootstrap.servers", broker_list);
    // key 序列化
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

    KafkaProducer producer = new KafkaProducer<String,String>(props);
//    KafkaProducer producer = new KafkaProducer(props);

    for(int i = 1;i<1000;i++){
      Student student = new Student(i,"yanxujiang"+i,"password"+i, 10+i);
      ProducerRecord record = new ProducerRecord(topic,null,null, JSON.toJSONString(student));
      producer.send(record);
      System.out.println("发送数据:"+JSON.toJSONString(student));
    }

//    Metric metric = new Metric();
//    metric.setTimestamp(System.currentTimeMillis());
//    metric.setName("mem");
//    Map<String,String> tags = new HashMap<>();
//    Map<String,Object>fields = new HashMap<>();
//    tags.put("cluster", "yanxujiang");
//    tags.put("host_id", "192.168.7.156");
//
//    fields.put("user_percent",90d);
//    fields.put("max",27244873d);
//    fields.put("used", 17244873d);
//    fields.put("init", 27244873d);
//
//    metric.setTags(tags);
//    metric.setFields(fields);
//
//    // topic  partition key content
//    ProducerRecord record = new ProducerRecord<String,String>(topic, null, null, JSON.toJSONString(metric));
//    producer.send(record);
//    System.out.println("send msg:" + JSON.toJSONString(metric));
    producer.flush();
  }
}


/**
 * entity
 */
class Metric {
  public String name;
  public long timestamp;
  public Map<String,Object> fields;
  public Map<String,String> tags;

  public Metric(){

  }

  public Metric(String name, long timestamp, Map<String, Object> fields,
      Map<String, String> tags) {
    this.name = name;
    this.timestamp = timestamp;
    this.fields = fields;
    this.tags = tags;
  }

  @Override
  public String toString() {
    return "Metric{" +
        "name='" + name + '\'' +
        ", timestamp=" + timestamp +
        ", fields=" + fields +
        ", tags=" + tags +
        '}';
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public void setFields(Map<String, Object> fields) {
    this.fields = fields;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }
}

