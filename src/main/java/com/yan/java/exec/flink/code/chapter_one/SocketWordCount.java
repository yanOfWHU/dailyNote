package com.yan.java.exec.flink.code.chapter_one;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class SocketWordCount {
  public static void main(String[] args) throws Exception{
    if(args.length != 2){
      System.err.println("USAGE:\nSocketWordCount <hostname> <port>");
      return;
    }
    String hostName = args[0];
    Integer port = Integer.parseInt(args[1]);

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    //获取数据
    DataStreamSource source = env.socketTextStream(hostName,port);

    //计数
    SingleOutputStreamOperator<Tuple2<String,Integer>>sum= source.
        flatMap(new LineSplitter())
        .keyBy(0)
        .sum(1);
    sum.print();
    env.execute("run the word count program");
  }

  private static class LineSplitter implements org.apache.flink.api.common.functions.FlatMapFunction<String,Tuple2<String,Integer>> {

    /**
     * 将输入的数据扁平化统计flatmap
     * @param s s 为每一行的数据
     * @param collector collector收集处理后的数据
     * @throws Exception
     */
    @Override
    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
      String[]tokens = s.toLowerCase().split("\\W+");

      for(String token : tokens){
        if(token.length()>0){
          collector.collect(new Tuple2<>(token,1));
        }
      }
    }
  }
}
