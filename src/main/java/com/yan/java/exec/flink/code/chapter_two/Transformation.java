package com.yan.java.exec.flink.code.chapter_two;

import com.alibaba.fastjson.JSON;
import com.yan.java.exec.flink.code.Student;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.CoGroupedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class Transformation {
  public static void main(String[] args) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //从kafka里面获取数据 并且sink数据到mysql
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("zookeeper.connect","localhost:2181");
    props.put("group.id","test-consumer-group");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");  //key 反序列化
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("auto.offset.reset","earliest");

    //获取到数据源
    SingleOutputStreamOperator <Student> students = env.addSource(new FlinkKafkaConsumer010<>(
        "student",//topic name
        new SimpleStringSchema(),
        props
    )).setParallelism(1).map(string-> JSON.parseObject(string,Student.class));

    /**
     * Map操作
     * 一对一操作  一条记录 输出一条记录
     */
    SingleOutputStreamOperator<Student> map = students.map(value->{
      Student res = new Student();
      res.id = value.id;
      res.name = value.name;
      res.password = value.password;
      res.age = value.age;
      return res;
    });
    map.print();

    /**
     * FlatMap操作
     * 一条记录 输出0——∞条记录
     */
    SingleOutputStreamOperator<Student> flatmap = students.flatMap((value, collector)->{
      if(value.id % 2 == 0) collector.collect(value);
    });
    flatmap.print();

    /**
     * Filter 筛选
     */
    SingleOutputStreamOperator<Student> filter = students.filter(value->value.id>95?true:false);
    filter.print();

    /**
     * KeyBy操作
     * 这里不用lambda 便于理解 日后可以自定义key selector 进行key by操作
     * 根据student的age进行分区 默认是使用hash函数进行分区处理
     */
    KeyedStream<Student,Integer> keyBy = students.keyBy(new KeySelector<Student, Integer>() {
      @Override
      public Integer getKey(Student student) throws Exception {
        return student.age;
      }
    });
    keyBy.print();

    /**
     * Reduce 返回单个结果  一般是对数据进行聚合处理
     * 每次reduce操作一个元素 都会创建一个新的值
     * 常见的有average sum min max count
     * reduce已经不常用，可以使用aggregation替代
     * 当然也可以自定义用户自定的reduce操作
     */
    SingleOutputStreamOperator<Student>reduce = keyBy.reduce((s1,s2)->{
      Student res = new Student();
      res.id = (s1.id + s2.id)/2;
      res.age = (s1.age + s2.age) /2;
      res.password = s1.password + s2.password;
      res.name = s1.name + s2.name;
      return res;
    });
    reduce.print();

    /**
     * Fold操作
     * fold操作 总是将最后一个数据与当前数据结合组成KeyedStream
     * fold第一个参数指最开始进行计算的数值
     * 每进行一次fold操作 Accumulator会成为上一个操作的return结果
     */
    SingleOutputStreamOperator<String> fold = keyBy.fold("0", new FoldFunction<Student, String>() {
      // Accumulator最开始为initialValue
      @Override
      public String fold(String Accumulator, Student o) throws Exception {
        return Accumulator + "-" + o.id;
      }
    });
    //fold输出的结果
    //0-1; 0-1-2; 0-1-2-3;.....0-1-2-3-....-n;...
    fold.print();

    /**
     * Aggregation操作
     */
    SingleOutputStreamOperator<Student> aggregationResult = keyBy.sum(0);//可以指定位置
    keyBy.sum("key");//也可以指定关键字
    keyBy.min(0);
    keyBy.min("key");
    keyBy.minBy(0);
    keyBy.minBy("key");
    //max 同
    //min和minBy的区别在于 min返回的是最小值，而minBy返回的是最小值的键key

    /**
     * Window 分窗口处理
     * WindowAll()可以对常规获取的DataStream数据进行处理
     * window()函数只能对现有的keyedStream数据进行分组
     */
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
    students.keyBy(0).timeWindow(Time.seconds(10));
    students.keyBy(0).countWindow(10);
    //window也可以自定义windowAssigner window(new WindowAssigner())

    /**
     * Union操作
     * 将两个或者多个数据流结合起来
     * 可以与自身结合
     */
    students.union(students);

    /**
     * Join操作
     *
     */
    students.join(students)
        .where(new KeySelector<Student, Object>() {
          @Override
          public Object getKey(Student student) throws Exception {
            return null;
          }
        }).equalTo(new KeySelector<Student, Object>() {
          @Override
          public Object getKey(Student student) throws Exception {
            return null;
          }
        })
        .window(new WindowAssigner<CoGroupedStreams.TaggedUnion<Student, Student>, Window>() {
          @Override
          public Collection<Window> assignWindows(
              CoGroupedStreams.TaggedUnion<Student, Student> studentStudentTaggedUnion, long l,
              WindowAssignerContext windowAssignerContext) {
            return null;
          }

          @Override
          public Trigger<CoGroupedStreams.TaggedUnion<Student, Student>, Window> getDefaultTrigger(
              StreamExecutionEnvironment streamExecutionEnvironment) {
            return null;
          }

          @Override
          public TypeSerializer<Window> getWindowSerializer(ExecutionConfig executionConfig) {
            return null;
          }

          @Override
          public boolean isEventTime() {
            return false;
          }
        })
        .apply(new JoinFunction<Student, Student, Object>() {
          @Override
          public Object join(Student student, Student student2) throws Exception {
            return null;
          }
        });

    /**
     * Split操作  将数据流进行拆分 拆分成多个流 每个流都要设定一个key name
     *
     * @Deprecated 使用Side Output 替代 split以及select
     *
     * flink不支持split以及select连续分流操作
     * 不过可以使用split select filter 从而进行连续分流
     */
    SplitStream<Student> split = students.split(new OutputSelector<Student>() {
      @Override
      public Iterable<String> select(Student student) {
        List<String> output = new ArrayList<>();
        if(student.id % 2 == 0){
          output.add("even");
        }else {
          output.add("odd");
        }
        return output;
      }
    });
    /**
     * Select 从Split流中选择出需要的流
     */
    DataStream<Student> evenStudent = split.select("even");
    DataStream<Student> oddStudent = split.select("odd");
    DataStream<Student> allStudent = split.select("even","odd");
    /**
     * 目前已经推荐使用SideOutPut 替代split进行操作了
     *
     */
    OutputTag<Student> youngStudent = new OutputTag<>("young");
    OutputTag<Student> midStudent = new OutputTag<>("mid");
    OutputTag<Student> oldStudent = new OutputTag<>("old");
    SingleOutputStreamOperator<Student> sideStudents = students.process(new ProcessFunction<Student, Student>() {
      @Override
      public void processElement(Student student, Context context, Collector<Student> collector) throws Exception {
        if(student.age < 10){
          context.output(youngStudent,student);
        }else if(student.age < 20){
          context.output(midStudent,student);
        }else if(student.age < 30){
          context.output(oldStudent,student);
        }else {
          //其他情况
          collector.collect(student);
        }
      }
    });
    sideStudents.print(); //获取到的是 age >= 30的学生
    sideStudents.getSideOutput(youngStudent).print();
    sideStudents.getSideOutput(midStudent).print();
    sideStudents.getSideOutput(oldStudent).print();
    /**
     * Project操作
     * 从数据流中 筛选出子集
     */
    DataStream<Tuple2<Integer,String>> out = students.project(1,3);
    out.print();
  }
}
