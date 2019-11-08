package com.yan.java.exec.flink.code.chapter_four;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Elastic Search Sink Util
 */
public class ElasticSearchSinkUtil {

  /**
   * es sink
   * @param hosts es hosts
   * @param bulkFlushMaxActions bulk flush size
   * @param parallelism 并行数
   * @param data data数据
   * @param function sink Function
   * @param <T>
   */
  public static<T> void addSink(List<HttpHost>hosts,int bulkFlushMaxActions,
      int parallelism, SingleOutputStreamOperator<T>data, ElasticsearchSinkFunction<T>function){
    ElasticsearchSink.Builder<T> esSinkBuilder = new ElasticsearchSink.Builder<>(hosts,function);
    esSinkBuilder.setBulkFlushMaxActions(bulkFlushMaxActions);
    data.addSink(esSinkBuilder.build()).setParallelism(parallelism);
  }

  /**
   * 解析配置文件的es hosts
   * @param hosts
   * @return
   * @throws java.net.MalformedURLException
   */
  public static List<HttpHost>getEsAddress(String hosts) throws MalformedURLException{
    String[] hostList = hosts.split(",");
    List<HttpHost> addresses = new ArrayList<>();
    for(String host : hostList){
      if(host.startsWith("http")){
        URL url = new URL(host);
        addresses.add(new HttpHost(url.getHost(),url.getPort()));
      }else {
        String [] parts = host.split(":",2);
        if(parts.length>1){
          addresses.add(new HttpHost(parts[0],Integer.parseInt(parts[1])));
        }else {
          throw new MalformedURLException("invalid elastic search hosts format");
        }
      }
    }
    return addresses;
  }


}
