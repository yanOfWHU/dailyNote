package com.yan.java.exec.flink.code.chapter_four;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExecutionEnvUtil {
  public static final ParameterTool PARAMETER_TOOL = createParameterTool();

  private static ParameterTool createParameterTool() {
    try{
      return ParameterTool
          .fromPropertiesFile(ExecutionEnvUtil.class.getResourceAsStream(PropertiesConstants.PROPERTIES_FILE_NAME))
          .mergeWith(ParameterTool.fromSystemProperties())
          .mergeWith(ParameterTool.fromMap(getEnv()));
    }catch (IOException e){
      e.printStackTrace();
    }
    return null;
  }

  public static ParameterTool createParameterTool(String[] args) throws Exception{
    return ParameterTool
        .fromPropertiesFile(ExecutionEnvUtil.class.getResourceAsStream(PropertiesConstants.PROPERTIES_FILE_NAME))
        .mergeWith(ParameterTool.fromArgs(args))
        .mergeWith(ParameterTool.fromSystemProperties())
        .mergeWith(ParameterTool.fromMap(getEnv()));
    
  }

  /**
   * 获取System 环境的map
   * @return
   */
  private static Map<String, String> getEnv() {
    Map<String,String> map = new HashMap<>();
    for(Map.Entry<String,String> entry : System.getenv().entrySet()){
      map.put(entry.getKey(),entry.getValue());
    }
    return map;
  }

  /**
   * 创建flink 执行环境
   * @param parameterTool 参数
   * @return
   */
  public static StreamExecutionEnvironment prepare(ParameterTool parameterTool) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(parameterTool.getInt(PropertiesConstants.STREAM_PARALLELISM,5));
    env.getConfig().disableSysoutLogging();
    //设置重启策略
    env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4,10000));
    //设置检查点配置
    if(parameterTool.getBoolean(PropertiesConstants.STREAM_CHECKPOINT_ENABLE,true)){
      env.enableCheckpointing(parameterTool.getInt(PropertiesConstants.STREAM_CHECKPOINT_INTERVAL,1000));
    }
    //设置全局任务参数
    env.getConfig().setGlobalJobParameters(parameterTool);
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    return env;
  }
}
