package com.yan.java.exec.flink.checkpoint_test.FlinkSpector;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class Test {
  @org.junit.Test
  public void testAFilter() throws Exception{
    StartWithAFilter filter = new StartWithAFilter();

    Assert.assertEquals(true, filter.filter(Tuple2.of("aTest",1)));

    Assert.assertEquals(false, filter.filter(Tuple2.of("bTest",1)));
  }

  @org.junit.Test
  public void aWordCountTest() throws IOException {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    DataStream<Tuple2<String,Integer>> words = env.fromCollection(Arrays.asList(
        Tuple2.of("a1",1),
        Tuple2.of("a2",1),
        Tuple2.of("a1",1)
    ));

    DataStream<Tuple2<String,Integer>> results =
        words.keyBy(s->s.f0)
        .reduce(
            (s,s1)->Tuple2.of(s.f0,s.f1+s1.f1)
        )
        .map(s->{
          System.out.println("" + s.f0 +" " +  s.f1);
          return Tuple2.of(s.f0,s.f1);
        }).returns(new TypeHint<Tuple2<String, Integer>>() {
          @Override
          public TypeInformation<Tuple2<String, Integer>> getTypeInfo() {
            return super.getTypeInfo();
          }
        });

    Iterator<Tuple2<String,Integer>> output =
        DataStreamUtils.collect(results);


    Assert.assertEquals(output.next(),Tuple2.of("a1",1));
    Assert.assertEquals(output.next(),Tuple2.of("a2",1));
    // ?? 为什么有第一个数据
    Assert.assertEquals(output.next(),Tuple2.of("a1",2));
    Assert.assertEquals(output.hasNext(),false);
  }
}
