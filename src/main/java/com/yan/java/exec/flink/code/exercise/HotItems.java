package com.yan.java.exec.flink.code.exercise;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.io.PojoCsvInputFormat;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 基于一个大的csv文件  记录了用户的一系列时间数据
 * 将文件作为flink输入源
 * 不同于kafka mysql
 * 使用了pojoType + input 结合创建输入源
 * 后续使用了一系列的转化
 *
 * 比较有参考价值的：
 *  1.读取csv文件  创建csv input，创建pojo type，结合pojo type和csv input 创建flink 数据源
 *  2.对数据源 抽取时间戳 生成watermark
 *  3.对windowedStream进行自定义聚合操作 产生新的数据流 参见CountAgg() 以及 WindowResultFunction()
 *  4.对keyedStream 自定义KeyedStreamFunction 对一个窗口内的数据进行操作
 *    processElement()必须要实现.处理每个元素都要调用这个事件
 *    open()函数可选，一般用于准备好一些属性设置
 *    onTimer()可选一般用于当一个TimerService设置的Timer fires调用  可以在processElement注册Timer
 *    close()可选，一般用于处理后续处理
 */
public class HotItems {
  public static void main(String[] args)throws Exception {
    //创建Stream 执行环境
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //基于事件时间处理
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    //设置并行度
    env.setParallelism(1);

    //获取资源  从本地的resources文件获取文件
    URL fileUrl = HotItems.class.getClassLoader().getResource(""
        + "UserBehavior.csv");
    //获取资源路径path
    Path filePath = Path.fromLocalFile(new File(fileUrl.toURI()));
    //抽取UserBehavior的TypeInformation   创建一个pojo type信息
    PojoTypeInfo<UserBehavior> pojoType = (PojoTypeInfo<UserBehavior>) TypeExtractor.createTypeInfo(UserBehavior.class);
    //java反射的顺序是不固定的  需要显示的指定文件中字段的顺序
    String[]fieldOrder = new String[]{"userId","itemId","categoryId","behavior","timestamp"};
    //创建PojoCsvInputFormat
    PojoCsvInputFormat<UserBehavior> csvInput = new PojoCsvInputFormat<>(filePath,pojoType,fieldOrder);

    //创建输入源
    env.createInput(csvInput,pojoType)
        //分配时间戳并且生成流水线
        .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehavior>() {
          @Override
          public long extractAscendingTimestamp(UserBehavior userBehavior) {
            return userBehavior.timestamp * 1000;
          }
        })
        //筛选出需要的数据
        .filter((userBehavior -> userBehavior.behavior.equals("pv")))
        //将数据进行分区处理
        .keyBy("itemId")
        //设置滑动窗口 窗口大小为60分钟  窗口滑动大小为5分钟
        .timeWindow(Time.minutes(60),Time.minutes(5))
        //对每个窗口的数据进行聚合  CountAgg()为聚合函数  WindowResultFunction为聚合后的数据收集处理函数
        .aggregate(new CountAgg(),new WindowResultFunction())
        //对窗口聚合处理并窗口数据处理的新的数据流进行分区
        .keyBy("windowEnd")
        //对分区的每个窗口 执行相应的window function 函数
        .process(new TopHotItem(3))
        // sink 操作即为print
        .print();

    env.execute("get hot top 3 items");


  }

  public static class ItemViewCount {
    public long itemId;
    public long windowEnd;
    public long viewCount;

    public static ItemViewCount of(long itemId,long windowEnd,long viewCount){
      ItemViewCount itemViewCount = new ItemViewCount();
      itemViewCount.itemId = itemId;
      itemViewCount.windowEnd = windowEnd;
      itemViewCount.viewCount = viewCount;
      return itemViewCount;
    }
  }


  /**
   * Count统计的聚合函数
   * AggregateFunction<IN , ACC, OUT>
   * 第一个参数 IN 表示需要聚合的数据类型
   * 第二个参数 ACC 表示聚合过程的数据
   * 第三个参数 OUT 表示聚合返回的结果
   *
   * 该类用于统计一个窗口内元素的个数
   */
  public static class CountAgg implements AggregateFunction<UserBehavior, Long,Long> {

    /**
     *
     * Long返回类型为ACC 表示聚合过程的返回数据类型
     * @return
     */
    @Override
    public Long createAccumulator() {
      return 0L;
    }

    /**
     * 返回类型 Long: ACC
     * UserBehavior：IN
     * 第二个参数Long 为上一个聚合计算的结果 ACC
     * @param userBehavior 当前要添加的数据
     * @param aLong 当前数据前的所有数据 统计的数据
     * @return
     */
    @Override
    public Long add(UserBehavior userBehavior, Long aLong) {
      return aLong + 1;
    }

    /**
     * 返回类型Long：OUT
     * 参数Long：为最后聚合计算的ACC类型的结果
     * @param aLong
     * @return
     */
    @Override
    public Long getResult(Long aLong) {
      return aLong;
    }

    /**
     * 对多个聚合计算的结果的Merge运算 加快聚合运算的效率
     * 参数和返回值都是ACC
     * @param aLong
     * @param acc1
     * @return
     */
    @Override
    public Long merge(Long aLong, Long acc1) {
      return aLong + acc1;
    }
  }


  /**
   * 四个参数
   * <IN, OUT, KEY, WINDOW>
   * IN 表示输入的数据类型
   * OUT 表示输入的数据类型
   * KEY 表示窗口的key
   * WINDOW 表示窗口的类型
   */
  public static class WindowResultFunction implements WindowFunction<Long,ItemViewCount, Tuple, TimeWindow> {
    /**
     *
     * @param tuple 窗口的主键key 即 itemId
     * @param window time window
     * @param iterable 聚合函数的结果  这里即是每个窗口的元素的个数 也就是view count
     * @param collector 收集器
     * @throws Exception
     */
    @Override
    public void apply(Tuple tuple, TimeWindow window, Iterable<Long> iterable, Collector<ItemViewCount> collector)
        throws Exception {
      Long itemId = ((Tuple1<Long>)tuple).f0;
      Long count = iterable.iterator().next();
      //该个ItemViewCount的windowEnd即为当前所在第一个时间窗口的最大时间戳
      //由于开始所有数据都是按照同一个时间窗口设定的 所以会有多个ItemViewCount有相同的windowEnd 这也是后续按照windowEnd进行分区的原理
      collector.collect(ItemViewCount.of(itemId,window.getEnd(),count));
    }
  }


  /**
   * KEY, OBJECT, RETURN
   * 第一个参数指代窗口key : windowEnd
   * 第二个参数指代keyed对象 : ItemViewCount
   * 第三个参数指代返回数据 所以Collector收集类型为String
   */
  public static class TopHotItem extends KeyedProcessFunction<Tuple, ItemViewCount,String> {

    private final int topSize;

    public TopHotItem(int topSize) {
      this.topSize = topSize;
    }

    //存储商品以及点击量的状态， 等待收集齐一个窗口的数据滞后，触发TopN计算
    private ListState<ItemViewCount> itemState;

    /**
     * Process one element from the input stream.
     * @param itemViewCount one element
     * @param context execution context
     * @param collector collector to collect the result
     * @throws Exception
     */
    @Override
    public void processElement(ItemViewCount itemViewCount, Context context, Collector<String> collector)
        throws Exception {
      //every element should be added to the list state
      itemState.add(itemViewCount);
      //register a event-time timer
      context.timerService().registerEventTimeTimer(itemViewCount.windowEnd + 1);
    }

    /**
     * init method for the function.
     * It is called before the actual working methods like map or join, and thus suitable for ont time setup work.
     * For functions that are part of an iteration,
     * this method will be invoked at the beginning of each iteration superstep.
     * @param parameters The configuration containing the parameters attached to the contract.
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
      super.open(parameters);
      //状态的注册
      ListStateDescriptor<ItemViewCount> itemStateDesc = new ListStateDescriptor<ItemViewCount>(
          "itemState-state",
          ItemViewCount.class
      );
      itemState = getRuntimeContext().getListState(itemStateDesc);
    }

    /**
     * Called when a timer set using TimerService fires.
     * @param timestamp The timestamp of the firing timer
     * @param ctx context
     * @param out collector for returning result values
     * @throws Exception
     */
    @Override
    public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
      super.onTimer(timestamp, ctx, out);
      // get all the pv count of all the received item
      List<ItemViewCount> allItems = new ArrayList<>();
      for(ItemViewCount item : itemState.get()){
        allItems.add(item);
      }

      //clear the state memory
      itemState.clear();

      //sort the data
      allItems.sort((i1,i2)->(int)(i2.viewCount - i1.viewCount));

      //print the data
      StringBuilder sb = new StringBuilder();
      sb.append("================================").append("\n");
      sb.append("time: ").append(new Timestamp(timestamp - 1)).append("\n");
      for(int i = 0; i < allItems.size() && i < topSize;i++){
        ItemViewCount currentItem = allItems.get(i);
        sb.append("No.").append(i).append(":")
            .append(" productId=").append(currentItem.itemId)
            .append(" viewCount=").append(currentItem.viewCount)
            .append("\n");
      }
      //用于控制输出频率 模拟实时滚动结果
      Thread.sleep(1000);
      out.collect(sb.toString());
    }


  }
}
