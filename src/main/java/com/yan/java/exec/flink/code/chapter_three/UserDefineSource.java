package com.yan.java.exec.flink.code.chapter_three;

import com.yan.java.exec.flink.code.Student;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 用户自定义Source
 * 参见chapter two 使用了自定义的Kafka FlinkKafkaConsumer010作为dataSource
 * 本章使用Mysql读取的内容作为数据源  自定义数据源使用
 */
public class UserDefineSource {
  public static void main(String[] args) throws Exception{
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    env.addSource(new SourceFromMysql()).print();

    env.execute("Flink add user defined source");
  }
}

//user defined SourceFunction


/**
 * 必须要充血run和cancel两个方法
 * open重写可选  为了数据获取作准备 比如mysql可以在open中写connect准备
 * close重写可选 为了数据关闭做好处理
 */
class SourceFromMysql extends RichSourceFunction<Student>{
  PreparedStatement ps;
  private Connection connection;

  @Override
  public void open(Configuration parameters) throws Exception {
    super.open(parameters);
    connection = getConnection();
    String sql = "select * from student;";
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

  /**
   * DataSource调用run方法获取Data数据
   * @param sourceContext source上下文 收集数据的用途
   * @throws Exception
   */
  @Override
  public void run(SourceContext<Student> sourceContext) throws Exception {
    ResultSet resultSet = ps.executeQuery();
    while (resultSet.next()){
      Student student = new Student(
          resultSet.getInt("id"),
          resultSet.getString("name").trim(),
          resultSet.getString("password").trim(),
          resultSet.getInt("age")
      );
      sourceContext.collect(student);
    }
  }

  @Override
  public void close() throws Exception {
    super.close();
    if(connection != null){
      connection.close();
    }
    if(ps != null){
      ps.close();
    }
  }

  /**
   * 取消获取数据
   */
  @Override
  public void cancel() {

  }
}
