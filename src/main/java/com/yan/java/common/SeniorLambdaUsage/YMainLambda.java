package com.yan.java.common.SeniorLambdaUsage;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.*;

/**
 * Created by yanxujiang on 2019-11-07.
 */
public class YMainLambda {
  public static void main(String[] args) {
    callAllTemplateMethod();
  }

  private static void callAllTemplateMethod() {
    // method 1 匿名类(接口只有一个方法，且是无参方法)
    {
      new Thread(
          ()->System.out.println("lambda")
      ).start();
    }
    // method 2 匿名类(接口只有一个方法，且是含参方法)
    {
      JButton show = new JButton("show");
      show.addActionListener(
          e->System.out.println("test")
      );
      // 如果参数就是内部方法调用的参数
      show.addActionListener(
          System.out::println // 等同于 e->System.out.println(e)
      );
    }
    // method 3 使用 lambda 对列表进行迭代
    {
      /**
       * note foreach 参数是 Consumer 类
       */
      List<String> features = Arrays.asList("1", "2", "3");
      features.forEach(System.out::print);
    }
    {
      // Consumer 类的使用
      // 注意 testConsumer  与 ConsumerTest 类
      testConsumer(ConsumerTest::test);
    }
    List<String> languages = Arrays.asList(
        "Java",
        "Scala",
        "C++",
        "C",
        "Python",
        "Lisp"
    );
    // method 4:lambda 表达式 以及 函数式接口 Predicate
    {
      filter(languages, str->((String)str).startsWith("J"));
      betterFilter(languages, str->((String)str).startsWith("J"));
    }
    // method 5: 在 lambda 表达式中加入 Predicate
    {
      Predicate<String> startWithJ = n->n.startsWith("J");
      Predicate<String> fourLetterLong = n->n.length() == 4;
      // 取相反逻辑 单元操作符
      Predicate<String> negate = startWithJ.negate();
      // and or 二元操作符
      Predicate<String> and = startWithJ.and(fourLetterLong);
      Predicate<String> or = startWithJ.or(fourLetterLong);
      // 甚至可以用 Predicate 的 and 和 or 添加逻辑
      languages.stream().filter(startWithJ.and(fourLetterLong)).forEach(
          n->System.out.println("StartWithJ and 4 length")
      );
    }
    // method 6: 使用 Map 和 Reduce 示范
    {
      List<Integer> ints = Arrays.asList(100, 200, 300, 400, 500);
      ints.stream().map(value->(value * 2 + 1)).forEach(System.out::println);
      Optional<Integer> result = ints.stream().map(value->(value * 2 + 1)).reduce(Integer::sum);
      result.ifPresent(System.out::println);
    }

    List<String> countries = Arrays.asList(
        "USA",
        "Japan",
        "China",
        "France",
        "Germany"
    );
    // method 7: collect 方法使用
    {
      List<String> filtered = countries.stream().filter(x->x.length()>2).collect(Collectors.toList());
      System.out.println(filtered);
    }

    // method 8： 对列表的每个元素都调用函数
    {
      String concatRet = countries.stream().map(String::toUpperCase).collect(Collectors.joining(", "));
      System.out.println(concatRet);
    }
    List<Integer> integers = Arrays.asList(
        1,
        2,
        3,
        1,
        2,
        5
    );
    // method 9： distinct() 去重
    {
      integers.stream().map(Math::getExponent).distinct().collect(Collectors.toList());
    }
    // method 10: 最大值，最小值
    {
      IntSummaryStatistics stats = integers.stream().mapToInt(x->x).summaryStatistics();
      stats.getMax();
      stats.getMin();
      stats.getAverage();
      stats.getCount();
      stats.getSum();
    }
  } //end method

  /**
   * Consumer 类使用 ->1
   * 参数 Consumer<Object> 对象
   * @param input
   */
  public static void testConsumer(Consumer<Object> input) {
    // 无关事情
    System.out.println(1);
    System.out.println(2);
    // accept 表示传入的参数， accept 几次，表示调用几次函数
    input.accept(new Object()); // 输出 not String
    input.accept("String"); // 输出 String
    // 其他无关事情
    System.out.println(3);
    // ...
  }

  // Predicate 类使用
  public static void filter(List<String> names, Predicate condition) {
    for (String name : names) {
      if (condition.test(name)) {
        System.out.println("name" + "");
      }
    }
  }
  // Predicate 类使用
  public static void betterFilter(List<String> names, Predicate condition) {
    names.stream().filter(condition::test).forEach(System.out::println);
    names.stream().filter(condition).forEach(System.out::println);
  }
}
// Consumer 类 ->2
class ConsumerTest {
  public static void test(Object o) {
    if (o instanceof String) {
      System.out.println("String");
    } else {
      System.out.println("not String");
    }
  }
}
