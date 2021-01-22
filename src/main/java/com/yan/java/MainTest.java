package com.yan.java;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yan.java.common.jackson.Conf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Created by yanxujiang on 2019-12-06.
 */
public class MainTest {
  public static void main(String[] args) throws Exception{
    ImmutablePair<List<Integer>, Integer> pair = ImmutablePair.of(new ArrayList<>(Arrays.asList(1, 2, 3)), 3);
    pair.left.remove(0);
    System.out.println(pair.right);
    System.out.println(pair.left);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");
    System.out.println(DateUtils.truncate(sdf.parse("2019-12-2 00:01:00"), Calendar.DATE));

    BiPredicate<String,Integer> equals = (str, integer) -> str.equalsIgnoreCase(integer.toString());
    biConsumer(MainTest::print);

    System.out.println(System.currentTimeMillis());
    System.out.println(new Date().getTime());

    Person p = new Person("yxj", null);
    ObjectNode objectNode = Conf.DEFAULT_OBJECT_MAPPER.createObjectNode();
    objectNode.put("name", Optional.ofNullable(p.getName()).orElse(null));
    objectNode.put("age", Optional.ofNullable(p.getAge()).orElse(null));
    System.out.println(objectNode.toString());

  }

  private static void print(String str, Integer integer){
    System.out.println(str);
    System.out.println(integer);
  }

  private static void biConsumer(BiConsumer<String, Integer> biConsumer) {
    System.out.println("1");
    biConsumer.accept("testStr", 1);
    System.out.println("2");
  }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Person {
  String name;
  String age;
}
