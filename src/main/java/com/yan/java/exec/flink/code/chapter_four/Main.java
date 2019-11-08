package com.yan.java.exec.flink.code.chapter_four;

import com.google.gson.Gson;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 简介：
 * 一个很好的例子  连接kafka获取数据源  并且sink数据到elastic search
 * 期间用到了 自定义Schema模型，Flink有自带的SimpleStringSchema 可以将数据序列化和反序列化成二进制数据和字符串
 * 同时对flink的ParameterTool类进行了很好的举例使用
 */
public class Main {
  public static void main(String[] args) throws Exception{
    //从参数中获取parameter信息  同时获取System的环境信息 以及获取系统的配置信息
    final ParameterTool parameterTool = ExecutionEnvUtil.createParameterTool(args);

    //创建并且设置flink 执行环境
    StreamExecutionEnvironment env = ExecutionEnvUtil.prepare(parameterTool);

    //创建数据源 自定义Schema
    DataStreamSource<MetricEvent>data = KafkaConfigUtil.buildSource(env);

    //获取elastic search的集群地址信息
    List<HttpHost> esAddresses = ElasticSearchSinkUtil.getEsAddress(parameterTool.get(PropertiesConstants.ELASTICSEARCH_HOSTS));

    //获取bulk size 以及进行sink操作的并行度
    int bulkSize = parameterTool.getInt(PropertiesConstants.ELASTICSEARCH_BULK_FLUSH_MAX_ACTIONS,40);
    int sinkParallelism = parameterTool.getInt(PropertiesConstants.STREAM_SINK_PARALLELISM,5);

    //sink操作
    //ElasticSinkFunction::process(data,RuntimeContext,RequestIndexer)
    ElasticSearchSinkUtil.addSink(esAddresses,bulkSize,sinkParallelism,data,
        (metric,context,requestIndexer)->
            requestIndexer.add(Requests.indexRequest()//添加索引
            .index("YANXUJIANG" + "_" + metric.getName()) //索引
            .type("YANXUJINAG") //设置类型
            .source(new Gson().toJson(metric).getBytes(Charset.forName("UTF-8")), XContentType.JSON)));//设置source

    env.execute("flink learning connector elastic search");
  }
}
