package java_function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Supplier：供给型接口，不需要传入参数，直接执行参数，返回执行结果
 */
public class Supplier_T {

  public static void main(String[] args) {
    List<String> data = Arrays.asList("张无忌", "张翠山", "张三丰", "小昭", "赵敏", "白眉鹰王");
    //使用Supplier获取姓张的人
    Supplier<List<String>> supplier = () -> {
      List<String> filter = new ArrayList<>();
      for (String datum : data) {
        if (datum.startsWith("张")) {
          filter.add(datum);
        }
      }
      return filter;
    };
    List<String> result = supplier.get();
    System.out.println("获取Supplier结果：" + result);
  }

}
