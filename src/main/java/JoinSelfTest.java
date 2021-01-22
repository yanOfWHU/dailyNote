import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by yanxujiang on 2020-11-26.
 */
public class JoinSelfTest {
  public static void main(String[] args) throws Exception {
//    System.out.println("before");
//    // following code will cause deal lock
//    Thread.currentThread().join();
//    System.out.println("after");

    List<Integer> arr = new ArrayList<>();
    arr.add(1);
    arr.stream().filter(ele -> ele == 2).map(ele -> {
      System.out.println(ele);
      return ele + 1;
    }).collect(Collectors.toList());
    System.out.println(arr.get(0));

    String s = "s s1\rs2\ns3";

    String[] sp = s.split("[\r|\n]");

    System.out.println(sp.length);


    String match = "gfsg_gdfg%%%sgsdg";

    System.out.println(match.replace("_", "\\_").replace("%", "\\%"));

    System.out.println("v=" + InnerClass.v + " v2=" + InnerClass.v2);

    long before = System.currentTimeMillis();
    run();
    long end = System.currentTimeMillis();
    System.out.println("cost:" + (end - before));

    System.out.println(getFile("http://thirdwx.qlogo.cn/mmopen/Q3auHgzwzM4jneX09sazzR53hd0Mg6znJXta8NicHKr7bMn7bMvfZD95vtdK5xEDDWgvF42ibLcg9WkouaFZ4N0w/132"));
  }


  static class InnerClass {
    static int v = 1;

    static {
      v = 2;
      v2 = 4;
    }

    static int v2 = 3;
  }

  public static void run() {
    List<Supplier<Integer>> tasks = new ArrayList<>();

    for (int i = 0; i< 10; i++) {
      tasks.add(createSupplier(i));
    }

    List<CompletableFuture<Integer>> futureResult = new ArrayList<>();
    futureResult.addAll( tasks.stream().map(task -> CompletableFuture.supplyAsync(task).whenComplete((result, ex) -> System.out.println(result))).collect(
        Collectors.toList()));
    CompletableFuture<Integer>[] arr = new CompletableFuture[futureResult.size()];
    futureResult.toArray(arr);
    CompletableFuture.allOf(arr).join();
    System.out.println("finished");
  }

  private static Supplier<Integer> createSupplier(int index) {
    return () ->  index;
  }

  private static byte[] getFile(String url) throws IOException {
    URL urlConet = new URL(url);
    HttpURLConnection con = (HttpURLConnection) urlConet.openConnection();
    con.setRequestMethod("GET");
    con.setConnectTimeout(4 * 1000);
    InputStream inStream = con.getInputStream();    //通过输入流获取图片数据
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[2048];
    int len = 0;
    while ((len = inStream.read(buffer)) != -1) {
      outStream.write(buffer, 0, len);
    }
    inStream.close();
    return outStream.toByteArray();
  }

}
