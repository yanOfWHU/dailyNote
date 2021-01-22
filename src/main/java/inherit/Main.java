package inherit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.spi.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yanxujiang on 2020-01-16.
 */
@Slf4j
public class Main {
  public static void main(String[] args) throws Exception {
    Child child = new Child();
    System.out.println(child.toString());

    List<Integer > a = Arrays.asList(1, 2 ,3 ,4);
    List<Integer > b = Arrays.asList(2,3,4,5);
    List<Integer> b1 = Arrays.asList(3,4,5, 2);
    System.out.println(CollectionUtils.subtract(a, b));
    System.out.println(CollectionUtils.subtract(b, a));
    System.out.println(CollectionUtils.isEmpty(CollectionUtils.subtract(b, b1)));
    System.out.println(CollectionUtils.isEqualCollection(b, b1));
    test();
    System.out.println(log.getClass());

  }

  public static void test() throws IOException {
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    bao.write("adbgsdg".getBytes());
    System.out.println(bao.toString());
    bao.reset();
    bao.write("agfsdgdfgwfew".getBytes());
    System.out.println(bao.toString());


  }
}
