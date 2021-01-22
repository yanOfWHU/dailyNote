import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Created by yanxujiang on 2020-11-05.
 */
public class ReplaceTestMain {
  static ObjectMapper objectMapper = new ObjectMapper();
  public static void main(String[] args) throws Exception {
    String ori = "1.1";
    String version = "v 1.0";
    String version2 = "v 20.1";
    Integer ver = 1;
    Integer ver2 = 12;
    Integer ver3 = 123;
    System.out.println(transformVersion(version));
    System.out.println(transformVersion(version2));
    System.out.println(transformVersion(ver));
    System.out.println(transformVersion(ver2));
    System.out.println(transformVersion(ver3));
    System.out.println(constructDeleteSqlContent("$testAttr"));

    String ruleContent = "{\"type\":\"rules_relation\",\"relation\":\"and\",\"rules\":[{\"type\":\"rules_relation\",\"relation\":\"and\",\"rules\":[{\"type\":\"rules_relation\",\"relation\":\"and\",\"rules\":[{\"type\":\"profile_rule\",\"field\":\"user.RegisterChannel\",\"function\":\"contain\",\"params\":[\"123\"]},{\"type\":\"profile_rule\",\"field\":\"user.name\",\"function\":\"isSet\",\"params\":[]}]}]},{\"type\":\"rules_relation\",\"relation\":\"and\",\"rules\":[{\"measure\":{\"aggregator\":\"general\",\"field\":\"\",\"type\":\"event_measure\",\"event_name\":\"$AppClick\"},\"type\":\"event_rule\",\"time_function\":\"relative_time\",\"time_params\":[\"1 day\"],\"params\":[1],\"function\":\"least\",\"filters\":[]}]},{\"type\":\"rules_relation\",\"relation\":\"and\",\"rules\":[{\"type\":\"event_sequence_rule\",\"time_function\":\"relative_time\",\"time_params\":[\"1 day\"],\"steps\":[{\"event\":\"$AppClick\"},{\"event\":\"$AppClick\"}]}]}]}";
    System.out.println(fillInExtraRule(ruleContent, "$userAttr"));

    List<Handler> handlers = new ArrayList<>();
    handlers.add(new Handler1());
    handlers.add(new Handler2());
    System.out.println(handlers.get(0).handle(1));
    System.out.println(handlers.get(1).handle("1"));

    List<Supplier<Integer>> tasks = new ArrayList<>();
    tasks.add(()-> doTask(1));
    tasks.add(() ->doTask(2));
    tasks.add(()-> doTask(3));
    tasks.add(()-> doTask(1));
    tasks.add(() ->doTask(2));
    tasks.add(()-> doTask(3));
    tasks.add(()-> doTask(1));
    tasks.add(() ->doTask(2));
    tasks.add(()-> doTask(3));
    List<Integer> result = syncSubmitTaskFuture(tasks);
    System.out.println(objectMapper.writeValueAsString(result));

  }

  public static Integer transformVersion(String version) {
    Integer spaceIdx = version.lastIndexOf(' ');
    String subStr = version.substring(spaceIdx + 1);
    return Integer.parseInt(subStr.replaceAll("\\.", ""));
  }

  /**
   * 1 -> v\s0.1
   * 9 -> v\s0.9
   * 10 -> v\s1.0
   * 100 -> v\s 10.0
   * @param version
   * @return
   */
  public static String transformVersion(Integer version) {
    StringBuilder sb = new StringBuilder();
    sb.append("v ");
    if (version < 10) {
      sb.append("0.");
      sb.append(version);
    } else if (version > 10){
      sb.append(version);
      sb.insert(sb.length() - 1, ".");
    } else {
      throw new RuntimeException("error");
    }
    return sb.toString();
  }

  private static String constructDeleteSqlContent(String openIdColumn) {
    ObjectNode rootNode = objectMapper.createObjectNode();
    ArrayNode rules = objectMapper.createArrayNode();
    ObjectNode rulesNode = objectMapper.createObjectNode();
    ObjectNode subRulesNode = objectMapper.createObjectNode();

    subRulesNode.put("type", "profile_rule")
        .put("field", String.format("user.%s", openIdColumn))
        .put("function", "contain")
        .set("params", objectMapper.createArrayNode().add("1"));

    rulesNode.put("type", "rules_relation")
        .put("relation", "and")
        .set("rules", subRulesNode);

    rules.add(rulesNode);

    rootNode.put("type", "rules_relation")
        .put("relation", "and")
        .set("rules", rules);
    return rootNode.toString();
  }

  private static String fillInExtraRule(String ruleContent, String openIdColumn) throws Exception {
    JsonNode readNode = objectMapper.readTree(ruleContent);

    ObjectNode rootNode = objectMapper.createObjectNode();
    ArrayNode arrayNode = objectMapper.createArrayNode();

    // tag profile exist node
    ObjectNode tagExistNode = objectMapper.createObjectNode();
    ArrayNode tagExistRuleNode = objectMapper.createArrayNode();
    tagExistRuleNode.add(objectMapper.createObjectNode()
        .put("type", "profile_rule")
        .put("field", String.format("user.%s", openIdColumn))
        .put("function", "isSet")
        .set("params", objectMapper.createArrayNode()));
    tagExistNode.put("type", "rules_relation")
        .put("relation", "and")
        .set("rules", tagExistRuleNode);

    // tag count < 20 node
    ObjectNode tagCountLessThan20Node = objectMapper.createObjectNode();
    ArrayNode tagCountRuleNode = objectMapper.createArrayNode();
    tagCountRuleNode.add(objectMapper.createObjectNode()
        .put("type", "profile_rule")
        .put("field", String.format("user.%s", openIdColumn))
        .put("function", "listsizecompare")
        .set("params", objectMapper.createArrayNode().add(false).add(false).add(20))
    );
    tagCountLessThan20Node.put("type", "rules_relation")
        .put("relation", "and")
        .set("rules", tagCountRuleNode);

    arrayNode.add(tagExistNode);
    arrayNode.add(tagCountLessThan20Node);
    arrayNode.add(readNode);
    rootNode.put("type", "rules_relation")
        .put("relation", "and")
        .set("rules", arrayNode);
    return rootNode.toString();
  }
  public interface Handler<K, V> {
    V handle(K key);
  }
  public static class Handler1 implements Handler<Integer, String> {

    @Override
    public String handle(Integer key) {
      return "1Str" + key;
    }
  }

  public static class Handler2 implements Handler<String, Integer> {

    @Override
    public Integer handle(String key) {
      return 1 + Integer.parseInt(key);
    }
  }

  private static Integer doTask(Integer input) {
    return input;
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T>  syncSubmitTaskFuture(List<Supplier<T>> suppliers) {
    List<T> futureResults = new ArrayList<>();
    List<CompletableFuture<T>> completableFutures = new ArrayList<>();
    suppliers.forEach(supplier -> completableFutures.add(CompletableFuture.supplyAsync(supplier).whenComplete((result, error) -> futureResults.add(result))));
    CompletableFuture<T>[] arrCompletableFuture = new CompletableFuture[suppliers.size()];
    completableFutures.toArray(arrCompletableFuture);
    CompletableFuture.allOf(arrCompletableFuture).join();
    return futureResults;
  }
}
