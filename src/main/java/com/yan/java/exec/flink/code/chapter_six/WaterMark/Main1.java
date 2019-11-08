package com.yan.java.exec.flink.code.chapter_six.WaterMark;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;


public class Main1 {
  public static void main(String[] args) throws Exception{

    OutputTag<Word> lateDataTag = new OutputTag<>("late");

    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    /**
     * 设置自动水位线时间间隔5000 五秒
     */
    env.getConfig().setAutoWatermarkInterval(5000);
    env.setParallelism(1);

    SingleOutputStreamOperator<Word>data = env.socketTextStream("local",9001)
        .map(value->
            new Word(value.split(",")[0],
                Integer.parseInt(value.split(",")[1]),
                Long.parseLong(value.split(",")[2])));

    //operator 分配时间戳 和水位线
    data.assignTimestampsAndWatermarks(new WordPeriodicWatermark());

    SingleOutputStreamOperator<Word>sum =
        data.keyBy(0)
        .timeWindow(Time.seconds(10))
        .sideOutputLateData(lateDataTag)
        .sum(1);

    //对正常的数据处理 以及对side output的数据处理
    sum.print();
    sum.getSideOutput(lateDataTag).print();


    env.execute("periodic watermark");
  }
}
