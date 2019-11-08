package com.yan.java.exec.kudu;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.Delete;
import org.apache.kudu.client.Insert;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;
import org.apache.kudu.client.KuduScanner;
import org.apache.kudu.client.KuduSession;
import org.apache.kudu.client.KuduTable;
import org.apache.kudu.client.Operation;
import org.apache.kudu.client.PartialRow;
import org.apache.kudu.client.RowResult;
import org.apache.kudu.client.RowResultIterator;
import org.apache.kudu.client.SessionConfiguration;
import org.apache.kudu.client.Update;
import org.apache.kudu.client.Upsert;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kudu client
 * created by Yanxujiang on 19/8/4
 */
public class MyKuduClient {
  //properties 配置常量key
  public static final String KUDU_MASTER = "kudu_master";
  public static final String KUDU_TIMEOUT = "kudu_timeout";
  public static final String TABLE_TYPE = "table_type";
  public static final String TABLE_NAME = "table_name";
  public static final String PRE_SPLIT_NUM_TABLETS_OPT = "pre_split_num_tablets";
  public static final String TABLE_NUM_REPLICAS = "table_num_replicas";
  public static final String BLOCK_SIZE_OPT = "block_size";
  public static final String FLUSH_MODE = "flush_mode";
  public static final String IGNORE_ALL_DUPLICATE_ROWS = "ignore_all_duplicate_rows";
  public static final String MUTATION_BUFFER_SIZE = "mutation_buffer_size";
  public static final String USER_ID = "user_id";
  public static final String OFFSET = "_offset";

  private Long defaultSleepTime;
  // 运行的线程池
  private ExecutorService executorService;
  //访问kudu的参数列表
  private Properties properties;
  //构建的kudu client
  private KuduClient kuduClient;
  //访问kudu的session 用于增删改三个操作
  private KuduSession kuduSession;
  //kudu table
  private KuduTable kuduTable;

  public MyKuduClient(){
    executorService = Executors.newCachedThreadPool();
  }

  /**
   * 初始化
   * properties参数:
   *    kudu_master:kudu服务器地址 necessary
   *    kudu_time out:optional
   *    table_type:创建table时需要用 optional
   *    flush_mode:kudu session操作，数据flush mode optional 默认manual flush
   *    ignore_all_duplicate_rows:optional session操作 是否忽略重复行 默认false
   *    mutation_buffer_size:缓冲大小 optional 默认1024
   *    table_name：打开kudu的table necessary
   *
   * @param createTable 是否创建kudu table
   * @param properties 访问kudu的一系列配置属性
   */
  public void init(boolean createTable, Properties properties)throws Exception{
    this.properties = properties;
    String masterQuorum = properties.getProperty(KUDU_MASTER);
    if(masterQuorum == null){
      throw new Exception("no kudu_master found in properties");
    }
    defaultSleepTime = Long.valueOf(properties.getProperty(KUDU_TIMEOUT, "60000"));
    /**
     * supported kerberos module
     * impala", "hadoop", "kudu", "hive", "zookeeper", "kafka
     */
    //如果不需要登陆检查的话  可以直接build()
    kuduClient = initClient(new KuduClient.KuduClientBuilder(masterQuorum)
        .defaultOperationTimeoutMs(defaultSleepTime)
        .defaultOperationTimeoutMs(defaultSleepTime));
    //kuduClient = new KuduClient.KuduClientBuilder("kudu-master-address").build();
    if(createTable){
      // 0 表示Event类型 详见 com.sensorsdata.platform.common TableType枚举类
      int tableType = Integer.parseInt(properties.getProperty(TABLE_TYPE, "0"));
      //### done create kudu table
      createTableIfNotExist(tableType);
    }

    //创建session
    kuduSession = kuduClient.newSession();
    //设置session flush mode
    if(properties.getProperty(FLUSH_MODE) != null &&
      properties.getProperty(FLUSH_MODE).equals("auto_flush_sync")){
        kuduSession.setFlushMode(SessionConfiguration.FlushMode.AUTO_FLUSH_SYNC);
      }else if(properties.getProperty(FLUSH_MODE) != null &&
      properties.getProperty(FLUSH_MODE).equals("auto_flush_background")){
      kuduSession.setFlushMode(SessionConfiguration.FlushMode.AUTO_FLUSH_BACKGROUND);
    }else{
      kuduSession.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
    }
    // 设置kudu session是否忽略重复的row
    boolean ignoreDuplicateRows = properties.getProperty(IGNORE_ALL_DUPLICATE_ROWS)!=null &&
        properties.getProperty(IGNORE_ALL_DUPLICATE_ROWS).equalsIgnoreCase("false");
    kuduSession.setIgnoreAllDuplicateRows(ignoreDuplicateRows);

    //设置kudu session的mutation缓冲区大小
    int bufferSize = Integer.parseInt(properties.getProperty(MUTATION_BUFFER_SIZE, "1024"));
    kuduSession.setMutationBufferSpace(bufferSize);
    try{
      kuduTable = kuduClient.openTable(properties.getProperty(TABLE_NAME));
    }catch (Exception e){
      System.out.printf("can not open the kudu table : %s", properties.getProperty(TABLE_NAME));
    }
  }

  /**
   * 查询kudu表 获取数据
   * @param key key为查询的关键字  kudu表都有primary key ；创建kudu表的时候 列column可以设置key(true)设置为primary key
   * @param fields 需要获取的列 fields
   * @param result 获取到的返回结果
   */
  public void scan (Map<String,Object> key,
      Set<String> fields,Map<String,Object>result)throws Exception{
    List<Map<String,Object>> results = new ArrayList<>();
    query(key,fields,results,20);
//    query(key,fields,results,1);
//    if(results.size()!=1){
//      throw new Exception("No matched record find by the key");
//    }
//    result.putAll(results.get(0));
    for(Map<String,Object> a_result : results){
      result.putAll(a_result);
    }
  }

  /**
   *  查询
   * @param key 查询需要的primary key 如果查询event表 需要提供user_id _offset等信息
   * @param fields 需要查询获取的列fields
   * @param results 查询到的结果 保存在results中
   * @param limit 限制的读取返回条数
   */
  private void query(Map<String, Object> key, Set<String> fields,
      List<Map<String, Object>> results, int limit)throws Exception {
    KuduScanner.KuduScannerBuilder builder = kuduClient.newScannerBuilder(kuduTable);
    //创建querySchema
    List<String> querySchema = new ArrayList<>(fields);
    // builder设置 列名字
    builder.setProjectedColumnNames(querySchema);

    // 设置builder的lowerBound 以及 upperBound
    //kudu基于MVCC多版本并发控制，对主键进行了排序操作，设置lower bound以及upper bound可以加快查询效率
//    PartialRow lowerBound = kuduTable.getSchema().newPartialRow();
//    setColumnsForRow(lowerBound, key, null);
//    builder.lowerBound(lowerBound);

    //如果只需要获取一条数据 则可以添加upper bound 更加快速的定位
    //经过测试 添加upper bound可以提高2倍查询速度
//    if(1 == limit){
//      PartialRow upperBound = kuduTable.getSchema().newPartialRow();
//      Object userId = key.get(USER_ID);
//      if(userId instanceof Long){
//        upperBound.addLong(USER_ID, (Long) key.get(USER_ID)+1);
//      }else if(userId instanceof Integer){
//        upperBound.addInt(USER_ID,(Integer)key.get(USER_ID)+1);
//      }
//      upperBound.addLong(OFFSET,(Long)key.get(OFFSET));
//      upperBound.addInt("day",(Integer)key.get("day")+1);
//      upperBound.addInt("event_id",(Integer)key.get("event_id")+1);
//      upperBound.addInt("sampling_group",(Integer)key.get("sampling_group")+1);
//      upperBound.addLong("time",(Long)key.get("time")+1);
//      //设置upperBound
//      builder.exclusiveUpperBound(upperBound);
//    }

    //不设置lower bound 以及upper bound的快捷创建方法
//    KuduScanner scanner = kuduClient.newScannerBuilder(kuduTable)
//        .setProjectedColumnNames(querySchema)
//        .build();

    // 查询
    KuduScanner kuduScanner = builder.limit(limit).build();

    while (kuduScanner.hasMoreRows()){
      RowResultIterator dataIterator = kuduScanner.nextRows();
      addDataToResult(dataIterator, limit, querySchema, results);
      if(limit == results.size()) break;
    }
    RowResultIterator closer = kuduScanner.close();
    addDataToResult(closer, limit, querySchema, results);
  }

  /**
   * 将获取到的数据添加到result list中
   * @param dataIterator 获取到的数据
   * @param limit 限制数量
   * @param querySchema 查询的字段list
   * @param results 返回的结果
   */
  private void addDataToResult(RowResultIterator dataIterator, int limit,
      List<String> querySchema, List<Map<String, Object>> results)throws Exception {
    RowResult row;
    Map<String,Object> rowResult = new HashMap<>(querySchema.size());
    if( dataIterator == null) return;
    while (dataIterator.hasNext()){
      //如果数量到了 则直接返回
      if(results.size() == limit ) return;
      row = dataIterator.next();
      for(String col : querySchema){
        rowResult.put(col, getValueFromColumn(col,row));
      }
      results.add(rowResult);
    }
  }

  /**
   * 在一行数据中  通过列名 获取
   * @param col
   * @param row
   * @return
   */
  private Object getValueFromColumn(String col, RowResult row) throws Exception{
    switch (row.getColumnType(col)){
      case INT8:
        return row.getByte(col);
      case INT16:
        return row.getShort(col);
      case INT32:
        return row.getInt(col);
      case INT64:
        return row.getLong(col);
      case BINARY:
        return row.getBinary(col);
      case STRING:
        return row.getString(col);
      case BOOL:
        return row.getBoolean(col);
      case FLOAT:
        return row.getFloat(col);
      case DOUBLE:
        return row.getDouble(col);
        default:
          throw new Exception("unknown type : " + row.getColumnType(col));
    }
  }

  /**
   * 给行设置column值
   * @param row 操作对象row
   * @param key kudu 表的key
   * @param value 设置的值
   */
  private void setColumnsForRow(PartialRow row, Map<String, Object> key, Map<String, Object> value) throws Exception{
    Schema schema = kuduTable.getSchema();
    //获取primary key主键的几个列
    List<ColumnSchema> primaryColumns = schema.getPrimaryKeyColumns();
    for(int i = 0 ;i<schema.getPrimaryKeyColumnCount(); i++){
      //获取主键关键字名字
      String primaryColumnName = primaryColumns.get(i).getName();
      if(!key.containsKey(primaryColumnName)){
        throw new Exception("don't exist key :" + primaryColumnName);
      }
      //给当前主键位置设值
      setColumnData(row, primaryColumns.get(i), key.get(primaryColumnName));
    }

    if(null != value){
      //给所有column 设值
      for(int i = 0;i<schema.getColumnCount();i++){
        String columnName = schema.getColumnByIndex(i).getName();
        // 如果value中存在该列 key 才会设值
        if(value.containsKey(columnName)){
          setColumnData(row,schema.getColumnByIndex(i),value.get(columnName));
        }
      }
    }
  }

  /**
   * 给partitionRow设值
   * @param row 需要操作的row
   * @param columnSchema primary key column
   * @param value the value to set
   */
  private void setColumnData(PartialRow row, ColumnSchema columnSchema, Object value) throws Exception{
    System.out.print("set data for column : " + columnSchema.getName() + ", value: " + value);
    switch (columnSchema.getType()){
      case INT8:
        if(!(value instanceof Byte)){
            throwValueNotMatchException();
        }
        row.addByte(columnSchema.getName(), (Byte)value);
        break;
      case INT16:
        if(!(value instanceof Short)){
          throwValueNotMatchException();
        }
        row.addShort(columnSchema.getName(), (Short)value);
        break;
      case INT32:
        if(!(value instanceof Integer)){
          throwValueNotMatchException();
        }
        row.addInt(columnSchema.getName(),(Integer)value);
        break;
      case INT64:
        if(!(value instanceof Long)){
          throwValueNotMatchException();
        }
        row.addLong(columnSchema.getName(),(Long)value);
        break;
      case UNIXTIME_MICROS:
        if(!(value instanceof String)){
          throwValueNotMatchException();
        }
        //添加时间戳  将string转为时间戳
        row.addLong(columnSchema.getName(), Timestamp.valueOf((String)value).getTime());
        break;
      case BINARY:
        if(!(value instanceof byte[])){
          throwValueNotMatchException();
        }
        row.addBinary(columnSchema.getName(),(byte[])value);
        break;
      case STRING:
        if(!(value instanceof String)){
          throwValueNotMatchException();
        }
        row.addString(columnSchema.getName(),(String)value);
        break;
      case BOOL:
        if(!(value instanceof Boolean)){
          throwValueNotMatchException();
        }
        row.addBoolean(columnSchema.getName(),(Boolean)value);
        break;
      case FLOAT:
        if(!(value instanceof Float)){
          throwValueNotMatchException();
        }
        row.addFloat(columnSchema.getName(),(Float)value);
        break;
      case DOUBLE:
        if(!(value instanceof Double)){
          throwValueNotMatchException();
        }
        row.addDouble(columnSchema.getName(),(Double)value);
        break;
      case DECIMAL:
        return;
        default:
          throwValueNotMatchException();
    }

  }

  private void throwValueNotMatchException() throws Exception{
    throw new Exception("value do not match error");
  }
  private void throwValueNotMatchException(String operation,String currentType,String needType) throws Exception{
    throw new Exception("operation: " + operation + ",value do not match error,expected "
              + needType + ",current:" + currentType);
  }

  /**
   *0 表示Event类型 详见 com.sensorsdata.platform.common TableType枚举类
   * @param tableType 创建的kudu表类型
   */
  private void createTableIfNotExist(int tableType) throws Exception {
    if(kuduClient.tableExists(properties.getProperty(TABLE_NAME))){
      System.out.println("kudu table " + properties.getProperty(TABLE_NAME) + " do not exist");
      return;
    }
    int numTablets = Integer.parseInt(properties.getProperty(PRE_SPLIT_NUM_TABLETS_OPT, "8"));
    if(numTablets>9000){
      throw new Exception("number of tablets is over 9000");
    }
    int replicas = Integer.parseInt(properties.getProperty(TABLE_NUM_REPLICAS, "3"));

    int blockSize = Integer.parseInt(properties.getProperty(BLOCK_SIZE_OPT, "4096"));

    if(0 == tableType){
      createEventTable(numTablets, replicas, blockSize);
    }else{
      createProfileTable(numTablets, replicas, blockSize);
    }
  }

  /**
   * 创建kudu profile 表
   * @param numTablets tablet数目
   * @param replicas  replicas数量
   * @param blockSize block大小
   */
  private void createProfileTable(int numTablets, int replicas, int blockSize) {
    System.out.printf("create profile table: tablets %d, replicas %d , blockSize %d",
        numTablets,replicas,blockSize);
  }

  /**
   * 创建kudu event table
   * @param numTablets tablet数目
   * @param replicas replicas数量
   * @param blockSize block大小
   */
  private void createEventTable(int numTablets, int replicas, int blockSize) {
    System.out.printf("create event table: tablets %d, replicas %d , blockSize %d",
        numTablets,replicas,blockSize);
  }

  /**
   * 初始化KuduClient
   * @param builder kudu client builder
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  private KuduClient initClient(KuduClient.KuduClientBuilder builder) throws IOException ,InterruptedException{
    UserGroupInformation ugi = UserGroupInformation.getLoginUser();
    /**
     * 没有kerberos login启动kudu
     */
    long start = System.currentTimeMillis();

    ugi.checkTGTAndReloginFromKeytab();

    System.out.println("checkout relogin time " +(System.currentTimeMillis() - start));
    return ugi.doAs((PrivilegedExceptionAction<KuduClient>) builder::build);
  }

  /**
   * 插入操作
   * @param values 需要插入的值 key 表示插入的列名字，value表示插入的值
   */
  public void insert(Map<String,Object>values)throws Exception {
    Schema schema = this.kuduTable.getSchema();
    Insert insert = this.kuduTable.newInsert();
    for(Map.Entry<String,Object> entry : values.entrySet()){
      operateByValueType(schema, insert,entry.getKey(), entry.getValue());
    }
    applyOperation(insert);
  }
  /**
   * 批量插入
   * @param list 需要批量插入的值
   */
  public void batchInsert(List<Map<String,Object>> list) {
    for(Map<String,Object> row : list){
      try {
        insert(row);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 更新操作
   * @param values 更新操作的key value,需要提供主键key值 和需要更新的列值
   * @throws Exception
   */
  public void update(Map<String,Object>values) throws Exception{
    Update update = this.kuduTable.newUpdate();
    for(Map.Entry<String,Object>entry : values.entrySet()){
      operateByValueType(this.kuduTable.getSchema(),update,entry.getKey(),entry.getValue());
    }
    applyOperation(update);
  }

  /**
   * 删除操作
   * @param values 一般只需要提供kudu主键key值即可
   * @throws Exception
   */
  public void delete(Map<String,Object>values) throws Exception{
    Delete delete = this.kuduTable.newDelete();
    for(Map.Entry<String,Object> entry:values.entrySet()){
      operateByValueType(this.kuduTable.getSchema(),delete,entry.getKey(),entry.getValue());
    }
    applyOperation(delete);
  }
  /**
   * 根据value的类型进行设置
   * @param schema kudu table的schema 用于判断列的类型
   * @param operation operation 对象 适用于insert update delete，insert一般需要提供需要的所有列值，update delete只需要提供关键列值
   * @param key 需要插入的列名
   * @param value 列的值
   */
  private void operateByValueType(Schema schema, Operation operation, String key, Object value) throws Exception{
    ColumnSchema columnSchema = schema.getColumn(key);
    //columnSchema.getType 有个列的数据类型  value也有一个数据类型  二者类型必须吻合
    Type columnType = columnSchema.getType();
    switch (columnType){
      case INT8:
        if(value instanceof Byte){
          operation.getRow().addByte(key, (Byte)value);
        }else{
          throwValueNotMatchException(operationName(operation),"byte",columnType.toString());
        }
        break;
      case INT16:
        if(value instanceof Short){
          operation.getRow().addShort(key, (Short)value);
        }else{
          throwValueNotMatchException(operationName(operation), "short", columnType.toString());
        }
        break;
      case INT32:
        if(value instanceof Integer){
          operation.getRow().addInt(key, (Integer) value);
        }else {
          throwValueNotMatchException(operationName(operation), "int", columnType.toString());
        }
        break;
      case INT64:
        if(value instanceof Long){
          operation.getRow().addLong(key, (Long)value);
        }else {
          throwValueNotMatchException(operationName(operation), "long", columnType.toString());
        }
        break;
      case FLOAT:
        if(value instanceof Float){
          operation.getRow().addFloat(key,(Float)value);
        }else {
          throwValueNotMatchException(operationName(operation), "float", columnType.toString());
        }
        break;
      case DOUBLE:
        if(value instanceof Double){
          operation.getRow().addDouble(key,(Double)value);
        }else {
          throwValueNotMatchException(operationName(operation),"double",columnType.toString());
        }
        break;
      case BOOL:
        if(value instanceof Boolean){
          operation.getRow().addBoolean(key, (Boolean)value);
        }else {
          throwValueNotMatchException(operationName(operation),"boolean",columnType.toString());
        }
        break;
      case BINARY:
        if(value instanceof byte[]){
          operation.getRow().addBinary(key,(byte[])value);
        }else {
          throwValueNotMatchException(operationName(operation),"binary",columnType.toString());
        }
        break;
      case STRING:
        if(value instanceof String){
          operation.getRow().addString(key,(String)value);
        }else {
          throwValueNotMatchException(operationName(operation),"String",columnType.toString());
        }
        break;
      case UNIXTIME_MICROS:
        if(value instanceof  String){
          operation.getRow().addLong(key,dateTimeStrToTimeStamp((String)value));
        }else {
          throwValueNotMatchException(operationName(operation),"String",columnType.toString());
        }
        break;
        default:
          throw new Exception("unexpected type error");
    }
  }

  private String operationName(Operation ope){
    if(ope instanceof Insert){
      return "insert";
    }
    if(ope instanceof Delete){
      return "delete";
    }
    if(ope instanceof Update){
      return "update";
    }
    if(ope instanceof Upsert){
      return "upsert";
    }
    return null;
  }

  private Long dateTimeStrToTimeStamp(String date) throws ParseException{
    Date ret = null;
    List<FastDateFormat>formatList = new ArrayList<>();
    formatList.add(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS"));
    formatList.add(FastDateFormat.getInstance("yyyy-MM-dd"));
    formatList.add(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss"));

    for(FastDateFormat fastDateFormat : formatList){
      try {
        ret = fastDateFormat.parse(date);
      }catch (ParseException e){
        //pass
      }
    }
    if(null == ret){
      throw new ParseException("bad date format, source=" + date, 0);
    }else {
      return ret.getTime();
    }
  }

  /**
   * session执行operation
   * @param operation insert update delete操作
   * @throws org.apache.kudu.client.KuduException 操作可能抛出的异常
   */
  private void applyOperation(Operation operation) throws KuduException {
    this.kuduSession.apply(operation);
  }

  public void close() throws KuduException {
    //先关闭session
    this.kuduSession.close();
    this.kuduClient.shutdown();
    this.executorService.shutdown();
  }
}
